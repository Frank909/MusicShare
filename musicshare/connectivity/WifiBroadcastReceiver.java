package com.sms.musicshare.connectivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    public static final int port = 8888;
    private String networkName = "", passphrase = "";
    private Context context;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private ArrayList<WifiP2pDevice> peers = new ArrayList<>(), clients = new ArrayList<>();
    private P2pPeersChangedListener p2pPeersChangedListener;
    private ConnectionEstablishedListener connectionEstablishedListener;

    public WifiBroadcastReceiver(Context context, WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel) {
        super();
        this.context = context;
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
    }

    public WifiBroadcastReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            onStateP2pChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            onPeersChanged(intent);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            try {
                onConnectionChanged(intent);
            }catch (IOException e){
                e.printStackTrace();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            try {
                onDeviceChange(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

        }
    }

    public void setP2pPeersChangedListener(P2pPeersChangedListener p){
        this.p2pPeersChangedListener = p;
    }

    public void setConnectionEstablishedListener(ConnectionEstablishedListener connectionEstablishedListener) {
        this.connectionEstablishedListener = connectionEstablishedListener;
    }

    private void onPeersChanged(final Intent intent){
        this.wifiP2pManager.requestPeers(this.channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                WifiBroadcastReceiver.this.peers.clear();
                WifiBroadcastReceiver.this.peers.addAll(peers.getDeviceList());
                p2pPeersChangedListener.onPeersChanged(WifiBroadcastReceiver.this.peers);
            }
        });
    }

    private void onConnectionChanged(Intent intent) throws IOException{
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        boolean networkStateFailed = networkInfo.getDetailedState().equals(NetworkInfo.DetailedState.FAILED);
        if(!networkStateFailed){
            wifiP2pManager.requestConnectionInfo(channel,  new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if(info.groupFormed) {
                        connectionEstablishedListener.onEstablishedConnection();
                    }
                }
            });
        }

        updateInfo();
    }

    private void updateInfo(){
        this.wifiP2pManager.requestGroupInfo(this.channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    networkName = group.getNetworkName();
                    passphrase = group.getPassphrase();

                    WifiBroadcastReceiver.this.clients.clear();
                    WifiBroadcastReceiver.this.clients.addAll(group.getClientList());
                }else {
                    networkName = "";
                    passphrase = "";
                }
            }
        });
    }

    public Collection<WifiP2pDevice> getClientDevices(){
        return this.clients;
    }

    private void onDeviceChange(Intent intent) throws IOException{

    }

    private Context getContext(){
        return this.context;
    }

    private void onStateP2pChanged(Intent intent){
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Toast.makeText(getContext(), "Il Wifi è attivo!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Il Wifi non è attivo!", Toast.LENGTH_SHORT).show();
        }
    }

    public WifiP2pDevice getP2pDevice(int position){
       return this.peers.get(position);
    }

    public static String getIPFromMac(String MAC, String string) {
        BufferedReader br = null;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*"+ string + ".*")){
                        String mac = splitted[3];

                        char[] in_mac = MAC.toCharArray();
                        char[] splitted_mac = splitted[3].toCharArray();

                        int difference = 0;
                        for (int i = 0; i<in_mac.length; i++){
                            if(in_mac[i] != splitted_mac[i])
                                difference++;
                        }

                        if (mac.matches(MAC) || difference == 1)
                            return splitted[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getNetworkName(){
        return this.networkName;
    }
    private String getPassPhrase(){
        return this.passphrase;
    }

    public void setProgressDialogTimeout(final ProgressDialog dialogTimeout){
        Runnable cancelDialog = new Runnable() {
            public void run() {
                if(dialogTimeout != null){
                    dialogTimeout.cancel();
                    wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            connectionEstablishedListener.onRefuseOrTimeOutConnection();
                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                }
            }
        };

        Handler cancelHandler = new Handler();
        cancelHandler.postDelayed(cancelDialog, 10000);
    }
}
