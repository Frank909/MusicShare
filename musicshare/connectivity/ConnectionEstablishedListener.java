package com.sms.musicshare.connectivity;


public interface ConnectionEstablishedListener {
    void onEstablishedConnection();
    void onRefuseOrTimeOutConnection();
}
