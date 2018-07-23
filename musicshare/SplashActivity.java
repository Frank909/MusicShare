package com.sms.musicshare;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicLibraryScanner;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.helper.ServiceTools;

import org.w3c.dom.Text;

import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadFactory;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private ArrayList<InfoTrack> tracksList = new ArrayList<>();
    private MusicLibraryScanner musicLibraryScanner;
    private static final int PERMISSION_ALL = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    @BindView(R.id.progressBar1) ProgressBar spinner;
    @BindView(R.id.textView) TextView txt;
    @BindString(R.string.button_allow) String allow_button;
    @BindString(R.string.cancel_button) String cancel_button;
    @BindString(R.string.permission_required) String perms_required;
    @BindString(R.string.splash_txt_retrieve) String txt_retrieving;
    @BindString(R.string.splash_txt_loading) String txt_loading;
    @BindString(R.string.splash_not_found) String txt_not_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        try {
            if (hasPermission(this, PERMISSIONS)) {
                proceed();
            }
        }catch (Exception e){
            txt.setText(e.toString());
        }
    }

    private void proceed(){
        musicLibraryScanner = new MusicLibraryScanner(this);
        musicLibraryScanner.setInjectOnPreExecute(injectOnPreExecute());
        musicLibraryScanner.setInjectOnDoInBackground(injectOnDoInBackground());
        musicLibraryScanner.setInjectOnFinishTask(injectOnFinishTask());
        musicLibraryScanner.retrieveTracksList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == PERMISSION_ALL) {
                Map<String, Integer> perms = new HashMap<>();

                for(String p : permissions)
                    perms.put(p, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    ArrayList<Integer> list_permissions = new ArrayList<>();

                    for(int i = 0; i < permissions.length; i++)
                         list_permissions.add(perms.get(permissions[i]));

                    if(!list_permissions.contains(PackageManager.PERMISSION_DENIED)){
                        proceed();
                    }else{
                        ArrayList<Boolean> shouldShow = new ArrayList<>();
                        for(int i = 0; i < permissions.length; i++){
                            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]))
                                shouldShow.add(true);
                            else
                                shouldShow.add(false);
                        }

                        if(shouldShow.contains(true)) {
                            String PERMS = "\n";
                            for(String string : permissions)
                                PERMS += "\n" + string;
                            showDialogOK(perms_required + PERMS, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            hasPermission(SplashActivity.this, PERMISSIONS);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            finish();
                                            break;
                                    }
                                }
                            });
                        }else {
                            /*
                            String PERMS = "\n";
                            for(String string : permissions)
                                PERMS += "\n" + string;
                            showDialogOK(perms_required + PERMS, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            grantUriPermission();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            finish();
                                            break;
                                    }
                                }
                            });
                            */
                            finish();
                        }
                    }
                }
            }

        }catch (Exception e){
            txt.setText(e.toString());
        }
    }

    private MusicLibraryScanner.InjectOnPreExecute injectOnPreExecute(){
        return new MusicLibraryScanner.InjectOnPreExecute() {
            @Override
            public void onPreExecuteTask() {
                txt.setText(txt_retrieving);
                spinner.setVisibility(View.VISIBLE);
                spinner.setMax(100);
            }
        };
    }

    private MusicLibraryScanner.InjectOnDoInBackground injectOnDoInBackground(){
        return new MusicLibraryScanner.InjectOnDoInBackground() {
            @Override
            public void onDoInBackground(Object... params) {
                try {
                    spinner.setIndeterminate(false);
                    int progress = 0;
                    while (progress <= 100) {
                        int temp = new Random().nextInt(10-1)+1;
                        progress = spinner.getProgress() + temp;
                        spinner.setProgress(progress);
                        Thread.sleep(100);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private MusicLibraryScanner.InjectOnFinishTask injectOnFinishTask() {
        return new MusicLibraryScanner.InjectOnFinishTask() {
            @Override
            public void onFinishTask(List<InfoTrack> infoTrackList) {
                try {
                    Thread.sleep(1000);
                    if (!infoTrackList.isEmpty()) {
                        txt.setText(txt_loading);
                        tracksList.addAll(infoTrackList);
                        RetainInfoFromTracksList retainInfoFromTracksList = new RetainInfoFromTracksList(tracksList);
                        RetainInfoFromTracksList.setStaticGenres(retainInfoFromTracksList.retainGenresList());
                        RetainInfoFromTracksList.setStaticArtists(retainInfoFromTracksList.retainArtistsList());
                        RetainInfoFromTracksList.setStaticAlbums(retainInfoFromTracksList.retainAlbumList());
                        RetainInfoFromTracksList.setStaticTracks(tracksList);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else
                        txt.setText(txt_not_found);
                }catch (Exception e){
                    txt.setText(e.toString());
                }
            }

            @Override
            public void onFinishTask(Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                finish();
            }
        };
    }

    private boolean hasPermission(Context context, String... permissions){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                int[] _permissions = new int[permissions.length];

                for (int i = 0; i < permissions.length; i++) {
                    _permissions[i] = ContextCompat.checkSelfPermission(context, permissions[i]);
                }

                ArrayList<String> listPermissionsNeeded = new ArrayList<>();

                for (int i = 0; i < _permissions.length; i++) {
                    if (_permissions[i] != PackageManager.PERMISSION_GRANTED)
                        listPermissionsNeeded.add(permissions[i]);
                }

                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                            PERMISSION_ALL);
                    return false;
                }
            }
        }catch (Exception e){
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
        }

        return true;

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(allow_button, okListener)
                .setNegativeButton(cancel_button, okListener)
                .create()
                .show();
    }
}
