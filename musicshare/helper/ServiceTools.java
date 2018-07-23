package com.sms.musicshare.helper;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

/**
 * Created by Frank on 28/03/2017.
 */

public class ServiceTools {

    private Context context;

    public ServiceTools(Context context){
        this.context = context;
    }

    private Context getContext(){
        return this.context;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
