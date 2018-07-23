package com.sms.musicshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;
import com.sms.musicshare.helper.ServiceTools;
import com.sms.musicshare.helper.TimeUtilities;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    @BindView(R.id.drag_player) LinearLayout playerLayout;
    @BindView(R.id.drag_player_artist_text) TextView artistText;
    @BindView(R.id.drag_player_track_text) TextView trackText;
    @BindView(R.id.drag_player_media_play_button) ImageView dragPlayerPlay;
    @BindView(R.id.drag_player_image_view_album_layout) ImageView dragPlayerAlbum;
    @BindView(R.id.drag_player_seek_bar) SeekBar seekBar;
    @BindView(R.id.txt_current_duration) TextView txtCurrentDuration;
    @BindView(R.id.txt_total_duration) TextView txtTotalDuration;
    @BindView(R.id.drag_player_favorites) ImageView favoritesImage;
    @BindView(R.id.drag_player_list_music) ImageView queueImage;


    private boolean isServiceRegistered = false;
    private ArrayList<InfoTrack> currentQueue =  new ArrayList<>();
    private TimeUtilities utils;
    private Handler mHandler = new Handler();
    private int currentPosition = 0, totalDuration = 0;
    private DBHelper mDBHelper;
    private static Drawable drawable_pause, drawable_play;

    @OnClick(R.id.drag_player_list_music)
    void onPlayerQueueListClick(View view){
        Intent intent = new Intent(this, QueueListActivity.class);
        intent.putExtra("queue", currentQueue);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.drag_player_favorites)
    void onPlayerFavoriteClick(View view){
        boolean isFavorite = true;
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                if (MusicBackGroundService.getCurrentTrack().isFavorite()) {
                    isFavorite = false;
                    mDBHelper.removeTrackFromFavorites(MusicBackGroundService.getCurrentTrack().getPath());
                    favoritesImage.setColorFilter(Color.WHITE);
                    MusicBackGroundService.getCurrentTrack().setFavorite(isFavorite);
                } else {
                    isFavorite = true;
                    mDBHelper.insertSingleTrackInFavorites(MusicBackGroundService.getCurrentTrack());
                    favoritesImage.setColorFilter(Color.YELLOW);
                }
                for(InfoTrack track1 : currentQueue){
                    if(track1.getPath().equals(MusicBackGroundService.getCurrentTrack().getPath())){
                        track1.setFavorite(isFavorite);
                        break;
                    }
                }

                Intent intent = new Intent(this, MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_UPDATE_QUEUE);
                intent.putParcelableArrayListExtra("queue", currentQueue);
                startService(intent);
            }
    }

    @OnClick(R.id.drag_player_media_ff_button)
    void onPlayerNextClick(View view) {
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                Intent intent = new Intent(this, MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_NEXT);
                setCurrentPosition(0);
                setInformationPlayer(MusicBackGroundService.getCurrentTrack());
                updateProgressBar();
                startService(intent);
            }
    }

    @OnClick(R.id.drag_player_media_play_button)
    void onPlayerPlayClick(View view) {
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                Intent intent = new Intent(this, MusicBackGroundService.class);
                if(dragPlayerPlay.getDrawable().equals(drawable_pause)){
                    dragPlayerPlay.setImageDrawable(drawable_play);
                    intent.setAction(MusicBackGroundService.ACTION_PAUSE);
                }else {
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    intent.setAction(MusicBackGroundService.ACTION_PLAY);
                    intent.putExtra("position", utils.progressToTimer(seekBar.getProgress(), getTotalDuration()));
                    updateProgressBar();
                }
                startService(intent);
            }
    }

    @OnClick(R.id.drag_player_media_rew_button)
    void onPlayerPreviousClick(View view) {
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                Intent intent = new Intent(this, MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_PREVIOUS);
                setCurrentPosition(0);
                setInformationPlayer(MusicBackGroundService.getCurrentTrack());
                updateProgressBar();
                startService(intent);
            }
    }

    @OnClick(R.id.drag_player_repeat_button)
    void onPlayerRepeatClick(View view) {
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                Intent intent = new Intent(this, MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_REPEAT);
                startService(intent);
            }
    }

    @OnClick(R.id.drag_player_shuffle_button)
    void onPlayerShuffleButton(View view) {
            if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
                Intent intent = new Intent(this, MusicBackGroundService.class);
                intent.setAction(MusicBackGroundService.ACTION_SHUFFLE);
                startService(intent);
            }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            InfoTrack track = intent.getParcelableExtra("track");

            switch (action){
                case MusicBackGroundService.ACTION_PLAY:
                    if(intent.getIntExtra("action_same", -1) != 1) {
                        setInformationPlayer(track);
                        dragPlayerPlay.setImageDrawable(drawable_pause);
                    }
                    updateProgressBar();
                    break;

                case MusicBackGroundService.ACTION_REPEAT:
                    setCurrentPosition(0);
                    updateProgressBar();
                    setInformationPlayer(track);
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    break;

                case MusicBackGroundService.ACTION_SHUFFLE:
                    setCurrentPosition(0);
                    updateProgressBar();
                    setInformationPlayer(track);
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    break;

                case MusicBackGroundService.ACTION_UPDATE_QUEUE:
                    if(intent.<InfoTrack>getParcelableArrayListExtra("queue") != null) {
                        currentQueue.clear();
                        currentQueue.addAll(intent.<InfoTrack>getParcelableArrayListExtra("queue"));
                    }
                    break;

                case MusicBackGroundService.ACTION_RETRIEVE_INFO:
                    setInformationPlayer(track);
                    String state = intent.getStringExtra("state");
                    int relativePosition = intent.getIntExtra("position", 0);
                    switch (state){
                        case MusicBackGroundService.PLAYER_PAUSED:
                            setCurrentPosition(utils.getProgressPercentage(relativePosition ,getTotalDuration()));
                            seekBar.setProgress(getCurrentPosition());
                            txtCurrentDuration.setText(utils.milliSecondsToTimer(relativePosition));
                            dragPlayerPlay.setImageDrawable(drawable_play);
                            break;
                        case MusicBackGroundService.PLAYER_PLAYING:
                            setCurrentPosition(utils.getProgressPercentage(relativePosition ,getTotalDuration()));
                            seekBar.setProgress(getCurrentPosition());
                            updateProgressBar();
                            dragPlayerPlay.setImageDrawable(drawable_pause);
                            break;
                        case MusicBackGroundService.PLAYER_STOPPED:
                            setCurrentPosition(0);
                            dragPlayerPlay.setImageDrawable(drawable_play);
                            break;
                    }
                    break;

                case MusicBackGroundService.ACTION_NEXT:
                    setCurrentPosition(0);
                    updateProgressBar();
                    setInformationPlayer(track);
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    break;

                case MusicBackGroundService.ACTION_PREVIOUS:
                    setCurrentPosition(0);
                    updateProgressBar();
                    setInformationPlayer(track);
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    break;

                case MusicBackGroundService.ACTION_STOP:
                    setCurrentPosition(0);
                    updateProgressBar();
                    dragPlayerPlay.setImageDrawable(drawable_play);
                    break;

                case MusicBackGroundService.ACTION_AUTO_NEXT:
                    setCurrentPosition(0);
                    updateProgressBar();
                    setInformationPlayer(track);
                    dragPlayerPlay.setImageDrawable(drawable_pause);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isServiceRegistered) {
            isServiceRegistered = false;
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void setInformationPlayer(InfoTrack track){
        artistText.setText(track.getArtist().getName());
        trackText.setText(track.getTitle());

        setTotalDuration((int) track.getDuration());

        if(track.getAlbum().getAlbumArt().equals("<unknown>"))
            dragPlayerAlbum.setImageResource(R.drawable.cd);
        else
            dragPlayerAlbum.setImageDrawable(Drawable.createFromPath(track.getAlbum().getAlbumArt()));

        if(track.isFavorite())
            favoritesImage.setColorFilter(Color.YELLOW);
        else
            favoritesImage.setColorFilter(Color.WHITE);

        txtTotalDuration.setText(utils.milliSecondsToTimer(getTotalDuration()));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        switch (newConfig.orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                playerLayout.setOrientation(LinearLayout.HORIZONTAL);
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                playerLayout.setOrientation(LinearLayout.VERTICAL);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDBHelper = new DBHelper(this);

        drawable_pause = ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause);
        drawable_play = ContextCompat.getDrawable(this, android.R.drawable.ic_media_play);

        this.currentQueue.addAll(getIntent().<InfoTrack>getParcelableArrayListExtra("currentQueue"));
        this.utils = new TimeUtilities();

        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(100);

        if(!isServiceRegistered) {
            isServiceRegistered = true;
            LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("current_song"));
        }

        if(new ServiceTools(this).isServiceRunning(MusicBackGroundService.class)) {
            Intent in = new Intent(this, MusicBackGroundService.class);
            in.setAction(MusicBackGroundService.ACTION_RETRIEVE_INFO);
            startService(in);
            dragPlayerPlay.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), getTotalDuration());

        // forward or backward to certain seconds
        if(MusicBackGroundService.getMediaPlayer() != null)
            MusicBackGroundService.getMediaPlayer().seekTo(currentPosition);

        setCurrentPosition(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    //RUNNABLE : task for updating time on the SeekBar
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if(MusicBackGroundService.getMediaPlayer() != null)
                    setCurrentPosition(MusicBackGroundService.getMediaPlayer().getCurrentPosition());

                // Displaying time completed playing
                txtCurrentDuration.setText(utils.milliSecondsToTimer(getCurrentPosition()));

                // Updating progress bar
                int progress = (utils.getProgressPercentage(getCurrentPosition(), getTotalDuration()));
                seekBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void setCurrentPosition(int position){
        this.currentPosition = position;
    }

    public  int getCurrentPosition() {
        return this.currentPosition;
    }

    private void setTotalDuration(int duration){
        this.totalDuration = duration;
    }

    private int getTotalDuration(){
        return this.totalDuration;
    }
}
