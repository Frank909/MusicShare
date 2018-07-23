package com.sms.musicshare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.MyMusicSharedAdapter;
import com.sms.musicshare.adapters.TrackSelectAllAdapter;
import com.sms.musicshare.connectivity.ServerThread;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.database.JSONHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnCheckBoxClickListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.helper.customTaskPackage.CustomTask;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyMusicSharedActivity extends AppCompatActivity implements OnCheckBoxClickListener {

    private ArrayList<InfoTrack> mSharedTrackList = new ArrayList<>();
    private ArrayList<Boolean> mItemsChecked = new ArrayList<>();
    private MyMusicSharedAdapter adapter;
    private TrackSelectAllAdapter otherAdapter;
    private DBHelper mDBHelper;
    private boolean isContextualMenu = false, isSelectedAll = false, layoutChanged = false;

    private ServerThread mServer;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    @BindView(R.id.my_music_shared_toolbar) Toolbar toolbar;
    @BindView(R.id.id_my_content_music_shared_empty_textview) TextView mEmptyListTextView;
    @BindView(R.id.id_content_music_shared_list) RecyclerView rvSharedTrackList;

    @BindString(R.string.share_your_music) String share_your_music;
    @BindString(R.string.no_shared_songs) String no_shared_songs;
    @BindString(R.string.message_dialog_title) String dialog_title;
    @BindString(R.string.delete_tracks_message) String dialog_msg;
    @BindString(R.string.no_track_selected) String msg_no_selected;
    @BindString(R.string.message_wificontrol_title) String message_wificontrol_title;
    @BindString(R.string.message_wificontrol_settings) String message_wificontrol_settings;
    @BindString(R.string.button_wificontrol_settings) String button_wificontrol_settings;
    @BindString(R.string.message_exit) String message_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music_shared);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(share_your_music);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                mManager.removeGroup(mChannel, null);
                mManager.cancelConnect(mChannel, null);
            }
        });

        mManager.discoverPeers(mChannel, null);

        mDBHelper = new DBHelper(this);
        mSharedTrackList.clear();
        mSharedTrackList.addAll(mDBHelper.getAllSharedTracks());

        /***************INVIA AL CLIENT*****/
        try {
            mServer = new ServerThread(getApplicationContext(), 8888, mSharedTrackList);
            mServer.setInjectOnTaskCancelled(new ServerThread.InjectOnTaskCancelled() {
                @Override
                public void onCancel() {
                    finish();
                }
            });
            mServer.executeServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){

        }
        /************************************/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_music_shared, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mSharedTrackList.isEmpty()) {
            mEmptyListTextView.setText(no_shared_songs);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /****************************SERVER************************/
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean pref_switch_enabling_disabling_connection = sp.getBoolean("pref_switch_enabling_disabling_connection", false);
        if(!pref_switch_enabling_disabling_connection)
            WifiControlDialog();
        /***********************************************************/

        if(mSharedTrackList.isEmpty()){
            mEmptyListTextView.setText(no_shared_songs);
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }else {

            adapter = new MyMusicSharedAdapter(this, mSharedTrackList);
            otherAdapter = new TrackSelectAllAdapter(this, mSharedTrackList, mItemsChecked, this); //secondo costruttore
            otherAdapter.initCheckboxes();

            rvSharedTrackList.setAdapter(adapter);
            rvSharedTrackList.setLayoutManager(new LinearLayoutManager(this));

            rvSharedTrackList.addOnItemTouchListener(new RecyclerItemClickListener(this, rvSharedTrackList, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    isContextualMenu = true;
                    if (isContextualMenu && !layoutChanged)
                        layoutChanged = changeLayout(isContextualMenu);
                    else
                        layoutChanged = false;
                }
            }));
        }
    }

    @Override
    public void onBackPressed() {
        if(isContextualMenu){
            isContextualMenu = false;
            if (!isContextualMenu || layoutChanged)
                layoutChanged = changeLayout(isContextualMenu);
            else
                layoutChanged = true;
        }else {

            if(mServer != null) {
                if(mServer.getTaskSocketStatus() == AsyncTask.Status.RUNNING){
                    try {
                        mServer.getServerSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mServer.getTask().cancel(true);
                }
                mManager.removeGroup(mChannel, null);
                mManager.cancelConnect(mChannel, null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if(mServer != null) {
                    if(mServer.getTaskSocketStatus() == CustomTask.Status.RUNNING) {
                        try {
                            mServer.getServerSocket().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mServer.getTask().cancel(true);
                    }
                    mManager.removeGroup(mChannel, null);
                    mManager.cancelConnect(mChannel, null);
                }
                finish();
                break;

            case R.id.item_delete:

                new AlertDialog.Builder(this)
                        .setTitle(dialog_title)
                        .setMessage(dialog_msg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList<InfoTrack> tempTrackList = new ArrayList<>();
                                ArrayList<Boolean> tempBoolean = new ArrayList<>();
                                mSharedTrackList = (ArrayList<InfoTrack>) otherAdapter.getListTracks();
                                mItemsChecked = (ArrayList<Boolean>) otherAdapter.getListCheckedItems();
                                tempTrackList.addAll(mSharedTrackList);
                                tempBoolean.addAll(mItemsChecked);
                                boolean flag = false;
                                int i = 0, j = 0;

                                if (mSharedTrackList.size() != 0) {
                                    for (boolean element : mItemsChecked) {
                                        if (element) {
                                            flag = true;
                                            mDBHelper.removeTrackFromSharedMusic(mSharedTrackList.get(i).getPath());
                                            for (InfoTrack track : RetainInfoFromTracksList.getStaticTracks())
                                                if (track.getPath().equals(mSharedTrackList.get(i).getPath()))
                                                    track.setShared(false);
                                            tempTrackList.remove(i - j);
                                            tempBoolean.remove(i - j);
                                            j++;
                                        }
                                        i++;
                                    }
                                    mSharedTrackList.clear();
                                    mSharedTrackList.addAll(tempTrackList);
                                    mItemsChecked.clear();
                                    mItemsChecked.addAll(tempBoolean);
                                    otherAdapter.notifyDataSetChanged();
                                }

                                if (mSharedTrackList.size() == 0) {
                                    mEmptyListTextView.setText(no_shared_songs);
                                    toolbar.getMenu().clear();
                                    rvSharedTrackList.setAdapter(adapter);
                                    rvSharedTrackList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    isContextualMenu = false;
                                }

                                if (!flag)
                                    Toast.makeText(getApplicationContext(), msg_no_selected, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;

            case R.id.item_select_all:
                if (!isSelectedAll) {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked));
                    isSelectedAll = true;
                    otherAdapter.setChecked();
                    otherAdapter.notifyDataSetChanged();
                } else {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
                    isSelectedAll = false;
                    otherAdapter.setUnchecked();
                    otherAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.item_done:
                isContextualMenu = false;
                changeLayout(isContextualMenu);
                break;

            case R.id.action_search:
                JSONHelper mJSONHelper = new JSONHelper();
                mJSONHelper.createJSONObject(mSharedTrackList);

                Intent intent = new Intent(getApplicationContext(), TrackPlaylistSelectionActivity.class);
                intent.putExtra("shared_tracks", mJSONHelper.returnJSONObject().toString());
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnCheckedCheckbox(View view) {
        boolean flag = true;

        for(int i=0; i<mItemsChecked.size(); i++)
            if(!mItemsChecked.get(i)){
                toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
                isSelectedAll = false;
                flag = false;
                break;
            }
        if(flag){
            toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_checked));
            isSelectedAll = true;
        }
    }

    @Override
    public void OnUncheckedCheckbox(View view) {
        toolbar.getMenu().getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unchecked));
        isSelectedAll = false;
    }

    private boolean changeLayout(boolean contestual_menu) {
        boolean flag;
        toolbar.getMenu().clear();

        if(contestual_menu) {
            toolbar.inflateMenu(R.menu.selectall_delete_moveitem);
            rvSharedTrackList.setAdapter(otherAdapter); //mItemChecked Vuoto
            rvSharedTrackList.setLayoutManager(new LinearLayoutManager(this));
            flag = true;
        }else {
            rvSharedTrackList.setAdapter(adapter);
            rvSharedTrackList.setLayoutManager(new LinearLayoutManager(this));
            flag = false;
        }

        return flag;
    }

    private void WifiControlDialog(){

        new AlertDialog.Builder(this)
                .setTitle(message_wificontrol_title)
                .setMessage(message_wificontrol_settings)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(button_wificontrol_settings, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(message_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }
}
