package com.sms.musicshare.connectivity;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.List;


public interface P2pPeersChangedListener{
    void onPeersChanged(List<WifiP2pDevice> deviceList);
}
