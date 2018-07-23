package com.sms.musicshare.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.PlaybackParams;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;


import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sms.musicshare.MainActivity;
import com.sms.musicshare.PlayerActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.database.DBHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MusicBackGroundService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    public static final String
            ACTION_AUTO_NEXT = "action_auto_next",
            ACTION_UPDATE_QUEUE = "action_update_queue",
            ACTION_PLAY = "action_play",
            ACTION_PAUSE = "action_pause" ,
            ACTION_NEXT = "action_next",
            ACTION_PREVIOUS = "action_previous",
            ACTION_STOP = "action_stop",
            ACTION_SHUFFLE = "action_shuffle",
            ACTION_REPEAT = "action_repeat",
            ACTION_RETRIEVE_INFO = "action_retrieve_info",
            PLAYER_PAUSED = "paused", PLAYER_PLAYING = "playing",
            PLAYER_STOPPED = "stopped", PLAYER_DEFAULT = "default";

    private static int
            CURRENT_SONG_INDEX = 0,
            NOTIFICATION_ID = 10,
            previous_index,
            next_index,
            currentPosition,
            temp;

    public enum PlayerOptions {
        SHUFFLE, REPEAT, DEFAULT
    }

    private static MediaPlayer mMediaPlayer;

    private static ArrayList<InfoTrack> mQueue = new ArrayList<>();
    private static InfoTrack currentTrack;
    private static PlayerOptions playerOptions = PlayerOptions.DEFAULT;
    private static String playerState = PLAYER_DEFAULT;

    public MusicBackGroundService(){
        setMediaPlayer(new MediaPlayer());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Intent in = new Intent("current_song");

            switch (intent.getAction()) {
                case ACTION_UPDATE_QUEUE:
                    setQueue(intent.<InfoTrack>getParcelableArrayListExtra("queue"));
                    if(getCurrentTrack() != null) {
                        Iterator<InfoTrack> trackIterator = getQueue().iterator();
                        ArrayList<String> arrayList = new ArrayList<>();
                        while (trackIterator.hasNext()) {
                            arrayList.add(trackIterator.next().getPath());
                        }

                        CURRENT_SONG_INDEX = arrayList.indexOf(getCurrentTrack().getPath());

                        if (CURRENT_SONG_INDEX !=-1 && !getQueue().isEmpty())
                            setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
                        in.putExtra("action", ACTION_UPDATE_QUEUE);
                        in.putExtra("track", (Parcelable) getCurrentTrack());
                        in.putExtra("queue", getQueue());
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                        break;
                    }
                case ACTION_RETRIEVE_INFO:
                    if (CURRENT_SONG_INDEX !=-1 && !getQueue().isEmpty())
                        setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));

                    in.putExtra("track", (Parcelable) getCurrentTrack());
                    in.putExtra("state", playerState);
                    in.putExtra("action", ACTION_RETRIEVE_INFO);
                    if(getMediaPlayer() != null)
                        in.putExtra("position", getMediaPlayer().getCurrentPosition());

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    boolean pref_notification = sp.getBoolean("pref_notification", true);

                    if(!pref_notification)
                        this.stopForeground(true);

                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                    break;

                case ACTION_PLAY:
                    if (intent.getParcelableExtra("track") != null) {
                        if (intent.getParcelableExtra("track").equals(getCurrentTrack()))
                            in.putExtra("action_same", 1);
                        else
                            currentPosition = 0;
                        CURRENT_SONG_INDEX = intent.getIntExtra("index", -1);
                        if(CURRENT_SONG_INDEX != -1) {
                            setCurrentTrack(intent.getParcelableExtra("track"));
                        }
                    }
                    in.putExtra("action", ACTION_PLAY);
                    in.putExtra("track", (Parcelable) getCurrentTrack());

                    if(playerState.equals(PLAYER_PAUSED))
                        in.putExtra("position", getMediaPlayer().getCurrentPosition());

                    temp = intent.getIntExtra("position", 0);

                    if(temp != 0)
                        currentPosition = temp;
                    temp = 0;

                    playPlayer(getCurrentTrack());
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                    break;

                case ACTION_NEXT:
                    currentPosition = 0;
                    if(!getQueue().isEmpty()) {
                        if (getMediaPlayer() == null)
                            setMediaPlayer(new MediaPlayer());
                        switch (playerOptions) {
                            case DEFAULT:
                                nextTrack();
                                break;
                            case SHUFFLE:
                                CURRENT_SONG_INDEX = new Random().nextInt((getQueue().size() - 1) + 1);
                                setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
                                restartPlayer(getMediaPlayer(), getCurrentTrack());
                                break;
                            case REPEAT:
                                restartPlayer(getMediaPlayer(), getCurrentTrack());
                                break;
                        }
                        in.putExtra("action", ACTION_NEXT);
                        in.putExtra("track", (Parcelable) getCurrentTrack());
                        createNotification(getCurrentTrack());

                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                    }
                    break;

                case ACTION_PREVIOUS:
                    currentPosition = 0;
                    if(!getQueue().isEmpty()) {
                        if (getMediaPlayer() == null)
                            setMediaPlayer(new MediaPlayer());
                        switch (playerOptions) {
                            case DEFAULT:
                                previousTrack();
                                break;
                            case SHUFFLE:
                                CURRENT_SONG_INDEX = new Random().nextInt((getQueue().size() - 1) + 1);
                                setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
                                restartPlayer(getMediaPlayer(), getCurrentTrack());
                                break;
                            case REPEAT:
                                restartPlayer(getMediaPlayer(), getCurrentTrack());
                                break;
                        }
                        in.putExtra("action", ACTION_PREVIOUS);
                        in.putExtra("track", (Parcelable) getCurrentTrack());
                        createNotification(getCurrentTrack());
                        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                    }
                    break;

                case ACTION_PAUSE:
                    pausePlayer(getMediaPlayer());
                    break;

                case ACTION_STOP:
                    if (intent.getParcelableExtra("track") != null) {
                        if (intent.getParcelableExtra("track").equals(getCurrentTrack()))
                            in.putExtra("action_same", 1);
                        setCurrentTrack(intent.getParcelableExtra("track"));
                    }
                    in.putExtra("action", ACTION_STOP);
                    in.putExtra("track", (Parcelable) getCurrentTrack());
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(in);
                    stopPlayer(getMediaPlayer());
                    break;

                case ACTION_REPEAT:
                    playerOptions = playerOptions.equals(PlayerOptions.REPEAT) ? PlayerOptions.DEFAULT : PlayerOptions.REPEAT;
                    break;

                case ACTION_SHUFFLE:
                    playerOptions = playerOptions.equals(PlayerOptions.SHUFFLE) ? PlayerOptions.DEFAULT : PlayerOptions.SHUFFLE;
                    break;
            }

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        deleteNotification();
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(temp != 0)
            mp.seekTo(temp);
        else if(currentPosition != 0)
            mp.seekTo(currentPosition);
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent in = new Intent("current_song");

        switch (playerOptions) {
            case REPEAT:
                restartPlayer(mp, getCurrentTrack());
                in.putExtra("action", ACTION_REPEAT);
                in.putExtra("track", (Parcelable) getCurrentTrack());
                createNotification(getCurrentTrack());
                break;

            case SHUFFLE:
                CURRENT_SONG_INDEX = new Random().nextInt((getQueue().size() - 1) + 1);
                setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
                restartPlayer(mp, getCurrentTrack());
                in.putExtra("action", ACTION_SHUFFLE);
                in.putExtra("track", (Parcelable) getCurrentTrack());
                createNotification(getCurrentTrack());
                break;

            case DEFAULT:
                CURRENT_SONG_INDEX++;
                if (CURRENT_SONG_INDEX < getQueue().size()) {
                    setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
                    restartPlayer(mp, getCurrentTrack());
                    in.putExtra("action", ACTION_AUTO_NEXT);
                    in.putExtra("track", (Parcelable) getCurrentTrack());
                    createNotification(getCurrentTrack());
                } else
                    stopSelf();
                break;
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    /**
     **NOTIFICATION METHODS****************************************/

    private Notification buildNotification(String contentTitle, String contentArtist, String contentAlbum){
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("currentQueue", getQueue());

        PendingIntent playerIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX) // da fare con le shared preferences
                .setLargeIcon(BitmapFactory.decodeFile(getCurrentTrack().getAlbum().getAlbumArt()))
                .setAutoCancel(true)
                .setShowWhen(false)
                .setContentIntent(playerIntent)
                .setContentTitle(contentTitle)
                .setContentText(contentArtist)
                .setContentInfo(contentAlbum);

        return builder.build();
    }

    private void createNotification(InfoTrack track){
        String title = track.getTitle(),
                artist = track.getArtist().getName(),
                album = track.getAlbum().getName();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean pref_notification = sp.getBoolean("pref_notification", true);
        if(pref_notification)
            this.startForeground(NOTIFICATION_ID, buildNotification(title, artist, album));
    }

    private void deleteNotification(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean pref_notification = sp.getBoolean("pref_notification", true);
        if(pref_notification)
            this.stopForeground(true);
    }

    /****************************************************************/
    /**RETRIEVE TRACK AND CURRENT SONG QUEUE METHODS*****************/


    private void setCurrentTrack(Parcelable currentTrack){
        MusicBackGroundService.currentTrack = (InfoTrack) currentTrack;
    }

    private void setCurrentTrack(InfoTrack currentTrack){
        MusicBackGroundService.currentTrack = currentTrack;
    }

    public static InfoTrack getCurrentTrack() {
        return currentTrack;
    }

    private static void setQueue(ArrayList<InfoTrack> mQueue) {
        MusicBackGroundService.mQueue = mQueue;
    }

    private static ArrayList<InfoTrack> getQueue() {
        return mQueue;
    }

    /**************************************************************/
    /**MEDIAPLAYER METHODS*****************************************/

    public static MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }

    private void setMediaPlayer(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    private void restartPlayer(MediaPlayer mp, InfoTrack track){
        try {
            mp.reset();
            mp.setDataSource(track.getPath());
            mp.prepareAsync();
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlayer(MediaPlayer mp){
        if(mp.isPlaying()) {
            playerState = PLAYER_STOPPED;
            deleteNotification();
            mp.stop();
            stopSelf();
        }
    }

    private void pausePlayer(MediaPlayer mp){
        if (mp.isPlaying()){
            mp.pause();
            currentPosition = mp.getCurrentPosition();
            playerState = PLAYER_PAUSED;
        }
    }

    private void nextTrack(){
        try {
            previous_index = CURRENT_SONG_INDEX;
            CURRENT_SONG_INDEX = previous_index + 1;

            if(CURRENT_SONG_INDEX == getQueue().size())
                CURRENT_SONG_INDEX = 0;
            currentPosition = 0;
            temp = 0;
            setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
            getMediaPlayer().reset();
            getMediaPlayer().setOnPreparedListener(this);
            getMediaPlayer().setOnCompletionListener(this);
            getMediaPlayer().setDataSource(getCurrentTrack().getPath());
            getMediaPlayer().prepareAsync();
            createNotification(getCurrentTrack());
            playerState = PLAYER_PLAYING;

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void previousTrack(){
        try {
            next_index = CURRENT_SONG_INDEX;
            CURRENT_SONG_INDEX = next_index -  1;

            if(CURRENT_SONG_INDEX < 0)
                CURRENT_SONG_INDEX = getQueue().size() - 1;

            currentPosition = 0;
            temp = 0;
            setCurrentTrack(getQueue().get(CURRENT_SONG_INDEX));
            getMediaPlayer().reset();
            getMediaPlayer().setOnPreparedListener(this);
            getMediaPlayer().setOnCompletionListener(this);
            getMediaPlayer().setDataSource(getCurrentTrack().getPath());
            getMediaPlayer().prepareAsync();
            createNotification(getCurrentTrack());
            playerState = PLAYER_PLAYING;

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void playPlayer(InfoTrack track){
        try {
            if (getMediaPlayer() != null)
                getMediaPlayer().release();
            setMediaPlayer(new MediaPlayer());

            getMediaPlayer().setWakeMode(getBaseContext(), PowerManager.PARTIAL_WAKE_LOCK);
            getMediaPlayer().setScreenOnWhilePlaying(false);
            getMediaPlayer().setDataSource(track.getPath());
            getMediaPlayer().setOnPreparedListener(this);
            getMediaPlayer().setOnCompletionListener(this);
            getMediaPlayer().prepareAsync();
            createNotification(track);
            playerState = PLAYER_PLAYING;
        }catch (Exception e){
            Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}