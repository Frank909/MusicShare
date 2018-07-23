package com.sms.musicshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.DevicesAdapter;
import com.sms.musicshare.connectivity.ConnectionEstablishedListener;
import com.sms.musicshare.connectivity.P2pPeersChangedListener;
import com.sms.musicshare.connectivity.WifiBroadcastReceiver;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevicesActivity extends AppCompatActivity implements P2pPeersChangedListener, ConnectionEstablishedListener {

    private ArrayList<String> mDevices = new ArrayList<>();
    private ArrayList<String> mDevicesAddress = new ArrayList<>();
    private DevicesAdapter adapter;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiBroadcastReceiver mReceiver;
    private RecyclerItemClickListener adapterListener;
    private ProgressDialog progressDialog;

    private int mSystemBarHeight, positionSelected;

    @BindView(R.id.devices_toolbar) Toolbar toolbar;
    @BindView(R.id.id_devices_list) RecyclerView rvDevices;
    @BindView(R.id.devices_fab) FloatingActionButton fab;
    @BindView(R.id.devices_empty_textview) TextView mEmptyListTextView;
    @BindView(R.id.devices_activity_coordinator_layout) CoordinatorLayout mCoordinatorLayout;

    @BindString(R.string.empty_list_devices) String empty_list_devices;
    @BindString(R.string.available_devices) String available_device;

    @OnClick(R.id.devices_fab) void FabOnClick(){
        search();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(available_device);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        mEmptyListTextView.setText(empty_list_devices);
        adapter = new DevicesAdapter(this, mDevices);

        rvDevices.setAdapter(adapter);
        rvDevices.setLayoutManager(new LinearLayoutManager(this));

        adapterListener = new RecyclerItemClickListener(this, rvDevices, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                position = rvDevices.getChildLayoutPosition(view);
                connect(mReceiver.getP2pDevice(position));
                positionSelected = position;
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        rvDevices.addOnItemTouchListener(adapterListener);

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                mManager.removeGroup(mChannel, null);
                mManager.cancelConnect(mChannel, null);
            }
        });
        mReceiver = new WifiBroadcastReceiver(this, mManager, mChannel);
        mReceiver.setP2pPeersChangedListener(this);
        mReceiver.setConnectionEstablishedListener(this);
    }

    @Override
    public void onBackPressed() {
        mManager.stopPeerDiscovery(mChannel, null);
        mManager.removeGroup(mChannel, null);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void search() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
               Snackbar.make(fab, "Sto cercando...", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i) {
                Snackbar.make(fab, "Errore!!!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onPeersChanged(List<WifiP2pDevice> deviceList) {
        mDevices.clear();
        mDevicesAddress.clear();

        for(WifiP2pDevice device : deviceList){
            mDevices.add(device.deviceName);
            mDevicesAddress.add(device.deviceAddress);
        }
        adapter.notifyDataSetChanged();
    }

    public void connect(final WifiP2pDevice wifiP2pDevice){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        //config.groupOwnerIntent = 0;
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                progressDialog.setMessage("Verifica accettazione in corso");
                progressDialog.show();
                mReceiver.setProgressDialogTimeout(progressDialog);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "errore nella connessione", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onEstablishedConnection() {
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), SharedMusicFromActivity.class);
            intent.putExtra("HostAddress", mDevicesAddress.get(positionSelected));
            startActivity(intent);
        }
    }

    @Override
    public void onRefuseOrTimeOutConnection() {
        Snackbar.make(fab, "connessione rifiutata o fuori tempo limite", Snackbar.LENGTH_SHORT).show();
    }
}
