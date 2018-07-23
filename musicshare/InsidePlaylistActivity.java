package com.sms.musicshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnCheckBoxClickListener;
import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.adapters.TrackDragSelectAllAdapter;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.database.JSONHelper;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.helper.SimpleItemTouchHelperCallback;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;


import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InsidePlaylistActivity extends AppCompatActivity implements OnStartDragListener, OnCheckBoxClickListener{

    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private ArrayList<Boolean> mItemsChecked = new ArrayList<>();
    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight, playlist_id;
    private DBHelper mDBHelper;
    private String playlist_name;
    private TrackAdapter adapter;
    private TrackDragSelectAllAdapter otherAdapter;
    private boolean isContestualMenu = false, isSelectedAll = false, layoutChanged = false;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private int default_visibility;

    @BindView(R.id.inside_playlist_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.inside_playlist_empty_textview) TextView mEmptyListTextView;
    @BindView(R.id.inside_playlist_toolbar) Toolbar toolbar;
    @BindView(R.id.id_inside_playlist_list) RecyclerView rvPlaylist;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindString(R.string.empty_playlist) String empty_playlist;
    @BindString(R.string.message_dialog_title) String dialog_title;
    @BindString(R.string.delete_tracks_message) String dialog_msg;
    @BindString(R.string.no_track_selected) String msg_no_selected;

    @OnClick(R.id.fab) void onFabClick(View view){
        JSONHelper mJSONHelper = new JSONHelper();
        mJSONHelper.createJSONObject(mTrackList);

        Intent intent = new Intent(getApplicationContext(), TrackPlaylistSelectionActivity.class);
        intent.putExtra("playlist_tracks", mJSONHelper.returnJSONObject().toString());
        intent.putExtra("playlist_name",playlist_name);
        intent.putExtra("playlist_id", playlist_id);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);

        ButterKnife.bind(this);
        this.default_visibility = fab.getVisibility();

        playlist_id = getIntent().getIntExtra("playlist_id", -1);
        playlist_name = getIntent().getStringExtra("playlist_name");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(playlist_name);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHelper = new DBHelper(this);
        mTrackList.addAll(mDBHelper.getAllTracksFromPlaylist(playlist_id));
        RetainInfoFromTracksList.setStaticPlayListTracks(mTrackList);

        if(mTrackList.isEmpty()){
            mEmptyListTextView.setText(empty_playlist);
        }else {
            adapter = new TrackAdapter(this, mTrackList);
            otherAdapter = new TrackDragSelectAllAdapter(this, mTrackList, mItemsChecked, playlist_id, this, this);
            otherAdapter.initCheckboxes();

            rvPlaylist.setAdapter(adapter);
            rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

            callback = new SimpleItemTouchHelperCallback(otherAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rvPlaylist);

            rvPlaylist.addOnItemTouchListener(new RecyclerItemClickListener(this, rvPlaylist, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    isContestualMenu = true;
                    if (isContestualMenu && !layoutChanged)
                        layoutChanged = changeLayout(isContestualMenu);
                    else
                        layoutChanged = false;
                }
            }));
        }

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.inside_playlist_fragment_drag_player, R.id.content_inside_playlist);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.inside_playlist_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
        /*************************************************************************/

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDragPlayerContainer.getHeight() > mDragPlayerFragment.getSnackBarHeight())
        {
            //int mResult = this.getWindow().getDecorView().getWidth();
            int mResult = this.getWindow().getDecorView().getWidth()-(mSystemBarHeight);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();
            layoutParams.height = mResult;
            mDragPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onBackPressed() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();

        if(layoutParams.height == (this.getWindow().getDecorView().getHeight()-mSystemBarHeight)) {
            //mDragPlayerFragment.closeDragPlayer(layoutParams);
        }else if(isContestualMenu){
            isContestualMenu = false;
            if (!isContestualMenu || layoutChanged)
                layoutChanged = changeLayout(isContestualMenu);
            else
                layoutChanged = true;
                mDBHelper.removeAllTracksFromPlaylist(playlist_id);
                mDBHelper.insertTracksInPlayList(mTrackList,playlist_id);
        }else{
            finish();
            Intent intent = new Intent(this, PlaylistActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(this, PlaylistActivity.class);
                startActivity(intent);
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
                                mTrackList = (ArrayList<InfoTrack>) otherAdapter.getListTracks();
                                mItemsChecked = (ArrayList<Boolean>) otherAdapter.getListCheckedItems();
                                tempTrackList.addAll(mTrackList);
                                tempBoolean.addAll(mItemsChecked);
                                boolean flag = false;
                                int i = 0, j = 0;

                                if(mTrackList.size() != 0){
                                    for(boolean element : mItemsChecked){
                                        if(element){
                                            flag = true;
                                            mDBHelper.removeTrackFromPlaylist(playlist_id, mDBHelper.getTrackID(mTrackList.get(i).getPath()));
                                            tempTrackList.remove(i - j);
                                            tempBoolean.remove(i - j);
                                            j++;
                                        }
                                        i++;
                                    }
                                    mTrackList.clear();
                                    mTrackList.addAll(tempTrackList);
                                    mItemsChecked.clear();
                                    mItemsChecked.addAll(tempBoolean);
                                    otherAdapter.notifyDataSetChanged();
                                }

                                if(mTrackList.size() == 0) {
                                    mEmptyListTextView.setText(empty_playlist);
                                    toolbar.getMenu().clear();
                                    fab.setVisibility(View.VISIBLE);
                                    rvPlaylist.setAdapter(adapter);
                                    rvPlaylist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    isContestualMenu = false;
                                }

                                if(!flag)
                                    Toast.makeText(getApplicationContext(), msg_no_selected, Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;

            case R.id.item_select_all:
                if(!isSelectedAll){
                    item.setIcon(getResources().getDrawable(R.drawable.ic_checked));
                    isSelectedAll = true;
                    otherAdapter.setChecked();
                    otherAdapter.notifyDataSetChanged();
                }else{
                    item.setIcon(getResources().getDrawable(R.drawable.ic_unchecked));
                    isSelectedAll = false;
                    otherAdapter.setUnchecked();
                    otherAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.item_done:
                isContestualMenu = false;
                changeLayout(isContestualMenu);
                mDBHelper.removeAllTracksFromPlaylist(playlist_id);
                mDBHelper.insertTracksInPlayList(mTrackList,playlist_id);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private boolean changeLayout(boolean contestual_menu) {
        toolbar.getMenu().clear();
        if(contestual_menu) {
            fab.setVisibility(View.GONE);
            toolbar.inflateMenu(R.menu.selectall_delete_moveitem);
            rvPlaylist.setAdapter(otherAdapter);
            rvPlaylist.setLayoutManager(new LinearLayoutManager(this));
            return true;
        }else {
            fab.setVisibility(this.default_visibility);
            rvPlaylist.setAdapter(adapter);
            rvPlaylist.setLayoutManager(new LinearLayoutManager(this));
            return false;
        }
    }

    @Override
    public void OnCheckedCheckbox(View view) {
        boolean flag = true;

        for(int i=0; i<mItemsChecked.size(); i++)
            if(!mItemsChecked.get(i)){
                toolbar.getMenu().getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_unchecked));
                isSelectedAll = false;
                flag = false;
                break;
            }
        if(flag){
            toolbar.getMenu().getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_checked));
            isSelectedAll = true;
        }
    }

    @Override
    public void OnUncheckedCheckbox(View view) {
        toolbar.getMenu().getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_unchecked));
        isSelectedAll = false;
    }

}
