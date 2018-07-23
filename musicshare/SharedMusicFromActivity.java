package com.sms.musicshare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.QueuePlaylistAdapter;
import com.sms.musicshare.connectivity.ClientThread;
import com.sms.musicshare.connectivity.ConnectionEstablishedListener;
import com.sms.musicshare.connectivity.WifiBroadcastReceiver;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnStartDragListener;
import com.sms.musicshare.helper.SimpleItemTouchHelperCallback;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sms.musicshare.connectivity.WifiBroadcastReceiver.getIPFromMac;

public class SharedMusicFromActivity extends AppCompatActivity implements OnStartDragListener{

    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private QueuePlaylistAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.Callback callback;
    private int mSystemBarHeight;
    private DragPlayerFragment mDragPlayerFragment;

    @BindString(R.string.permission_required) String perms_required;
    @BindString(R.string.button_allow) String allow_button;
    @BindString(R.string.cancel_button) String cancel_button;
    @BindView(R.id.shared_music_from_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.shared_music_from_toolbar) Toolbar toolbar;
    @BindView(R.id.id_shared_music_from_list) RecyclerView rvSharedSongs;
    @BindView(R.id.shared_music_from_empty_textview) TextView mEmptyListTextView;

    @BindString(R.string.title_shared_songs) String title_shared_songs;
    @BindString(R.string.no_shared_songs) String no_shared_songs;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_music_from);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title_shared_songs);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.shared_music_from_fragment_drag_player, R.id.content_shared_music_from);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.shared_music_from_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
        /*************************************************************************/
    }

    WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            try {
                String netInterface = group.getInterface(), macAddr = "";
                Collection<WifiP2pDevice> wifiP2pDevices = group.getClientList();

                for (WifiP2pDevice device : wifiP2pDevices) {
                    macAddr = device.deviceAddress;
                }

                String ip = getIPFromMac(macAddr, netInterface);
                InetAddress address = InetAddress.getByName(ip);

                ClientThread clientThread = new ClientThread(getApplicationContext(), address, WifiBroadcastReceiver.port);
                clientThread.execute();

            }catch (IOException e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mManager.requestGroupInfo(mChannel, groupInfoListener);

        if(mTrackList.isEmpty()){
            mEmptyListTextView.setText(no_shared_songs);
        }else {
            adapter = new QueuePlaylistAdapter(this, mTrackList, this);

            rvSharedSongs.setAdapter(adapter);
            rvSharedSongs.setLayoutManager(new LinearLayoutManager(this));

            callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(rvSharedSongs);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDragPlayerContainer.getHeight() > mDragPlayerFragment.getSnackBarHeight())
        {
            int mResult = this.getWindow().getDecorView().getWidth()-(mSystemBarHeight);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();
            layoutParams.height = mResult;
            mDragPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onBackPressed() {
        disconnectWifiDirect();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                disconnectWifiDirect();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disconnectWifiDirect(){
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "errore nella disconnessione", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
