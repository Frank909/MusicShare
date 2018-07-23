package com.sms.musicshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.animation.ResizeAnimation;
import com.sms.musicshare.animation.ShowHideAnimation;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;
import com.sms.musicshare.helper.OnBackPressedListener;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.helper.ServiceTools;
import com.sms.musicshare.helper.TimeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DragPlayerFragment extends Fragment implements View.OnClickListener
        /*OnContextualMenuListener*/, QueueListActivity.OnCloseQueueListListener{

    @BindView(R.id.snack_bar_player_layout) LinearLayout mSnackPlayer;
    @BindView(R.id.snack_bar_image_view_album) ImageView snackImage;
    @BindView(R.id.snack_bar_track_text) TextView snackTrackText;
    @BindView(R.id.snack_bar_artist_text) TextView snackArtistText;
    @BindView(R.id.snack_bar_media_play) ImageView snackImagePausePlay;
    @BindView(R.id.cardview_snack_bar_player_layout) CardView mCardViewSnackPlayer;

    private boolean isServiceRegistered = false;
    private RelativeLayout mContentMain;
    private FrameLayout mFragmentDragPlayer;
    private DBHelper mDBHelper;
    private int snackHeight;
    private static Drawable drawable_pause, drawable_play;
    //private static InfoTrack currentTrack;
    private static OnContextualMenuListener onContextualMenuListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<InfoTrack> currentQueue = new ArrayList<>(), modifiedQueue = new ArrayList<>();

    private int id_fragment_drag_player = 0,
            id_content_main = 0,
            mSystemBarHeight;

    @OnClick(R.id.snack_bar_media_play)
    void onCompatPlayClick(View view) {
        if(new ServiceTools(getContext()).isServiceRunning(MusicBackGroundService.class)) {
            Intent intent = new Intent(getContext(), MusicBackGroundService.class);
            if(snackImagePausePlay.getDrawable().equals(drawable_pause)){
                snackImagePausePlay.setImageDrawable(drawable_play);
                intent.setAction(MusicBackGroundService.ACTION_PAUSE);
            }else {
                snackImagePausePlay.setImageDrawable(drawable_pause);
                intent.setAction(MusicBackGroundService.ACTION_PLAY);
            }
            getContext().startService(intent);
        }
    }

    @OnClick(R.id.snack_bar_media_ff)
    void onCompatNextClick(View view) {
        if(new ServiceTools(getContext()).isServiceRunning(MusicBackGroundService.class)) {
            Intent intent = new Intent(getContext(), MusicBackGroundService.class);
            intent.setAction(MusicBackGroundService.ACTION_NEXT);
            getContext().startService(intent);
        }
    }

    @OnClick(R.id.snack_bar_media_rew)
    void onCompatPreviousClick(View view) {
        if(new ServiceTools(getContext()).isServiceRunning(MusicBackGroundService.class)) {
            Intent intent = new Intent(getContext(), MusicBackGroundService.class);
            intent.setAction(MusicBackGroundService.ACTION_PREVIOUS);
            getContext().startService(intent);
        }
    }

    public DragPlayerFragment() {
        // Required empty public constructor
    }

    public void setLayoutId(int id_fragment_drag_player, int id_content_main) {
        this.id_fragment_drag_player = id_fragment_drag_player;
        this.id_content_main = id_content_main;
    }

    // TODO: Rename and change types and number of parameters
    public static DragPlayerFragment newInstance(String param1, String param2) {
        DragPlayerFragment fragment = new DragPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void setInformationPlayer(InfoTrack track){
        String albumArt = track.getAlbum().getAlbumArt(),
                artist = track.getArtist().getName(),
                title = track.getTitle();

        snackArtistText.setText(artist);

        snackTrackText.setText(title);

        snackTrackText.setSelected(true);

        if(!albumArt.equals("<unknown>"))
            snackImage.setImageDrawable(Drawable.createFromPath(albumArt));
        else
            snackImage.setImageResource(R.drawable.cd);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getStringExtra("action");
                InfoTrack track = intent.getParcelableExtra("track");

                switch (action) {
                    case MusicBackGroundService.ACTION_PLAY:
                        if(intent.getIntExtra("action_same", -1) != 1) {
                            setInformationPlayer(track);
                            snackImagePausePlay.setImageDrawable(drawable_pause);
                        }
                        break;

                    case MusicBackGroundService.ACTION_REPEAT:
                        setInformationPlayer(track);
                        snackImagePausePlay.setImageDrawable(drawable_pause);
                        break;

                    case MusicBackGroundService.ACTION_SHUFFLE:
                        setInformationPlayer(track);
                        snackImagePausePlay.setImageDrawable(drawable_pause);
                        break;

                    case MusicBackGroundService.ACTION_UPDATE_QUEUE:

                        if(intent.<InfoTrack>getParcelableArrayListExtra("queue") != null) {
                            currentQueue.clear();
                            currentQueue.addAll(intent.<InfoTrack>getParcelableArrayListExtra("queue"));
                            setInformationPlayer(track);
                        }

                        break;

                    case MusicBackGroundService.ACTION_RETRIEVE_INFO:
                        setInformationPlayer(track);
                        String state = intent.getStringExtra("state");
                        //int relativePosition = intent.getIntExtra("position", 0);
                        switch (state){
                            case MusicBackGroundService.PLAYER_PAUSED:
                                //setCurrentPosition(utils.getProgressPercentage(relativePosition ,getTotalDuration()));
                                //seekBar.setProgress(getCurrentPosition());
                                snackImagePausePlay.setImageDrawable(drawable_play);
                          //      dragPlayerPlay.setImageDrawable(drawable_play);
                                break;
                            case MusicBackGroundService.PLAYER_PLAYING:
                                snackImagePausePlay.setImageDrawable(drawable_pause);
                                break;
                            case MusicBackGroundService.PLAYER_STOPPED:
                                snackImagePausePlay.setImageDrawable(drawable_play);
                                break;
                        }
                        break;

                    case MusicBackGroundService.ACTION_NEXT:
                        setInformationPlayer(track);
                        snackImagePausePlay.setImageDrawable(drawable_pause);
                        break;

                    case MusicBackGroundService.ACTION_PREVIOUS:
                        setInformationPlayer(track);
                        snackImagePausePlay.setImageDrawable(drawable_pause);
                        break;

                    case MusicBackGroundService.ACTION_STOP:
                        snackImagePausePlay.setImageDrawable(drawable_play);
                        break;

                    case MusicBackGroundService.ACTION_AUTO_NEXT:
                        setInformationPlayer(track);
                        snackImagePausePlay.setImageDrawable(drawable_pause);
                        break;
                }
            }catch (Exception e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        QueueListActivity.setOnCloseQueueListListener(this);

        mDBHelper = new DBHelper(getContext());

        RetainInfoFromTracksList retainInfoFromTracksList = new RetainInfoFromTracksList(RetainInfoFromTracksList.getStaticTracks());
        ArrayList<InfoTrack> favoritesTracks = mDBHelper.getAllFavoritesTracks();

        if(!favoritesTracks.isEmpty())
            retainInfoFromTracksList.retainFavoritesTracksList(favoritesTracks);

        RetainInfoFromTracksList.refreshFavoritesLists(favoritesTracks);

        if(modifiedQueue.isEmpty()) {
            if (RetainInfoFromTracksList.retainCurrentQueueList().isEmpty()) {
                if (getActivity() instanceof MainActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(RetainInfoFromTracksList.getStaticTracks());
                } else if (getActivity() instanceof ArtistActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(RetainInfoFromTracksList.retainStaticTracksArtist());
                } else if (getActivity() instanceof GenreActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(RetainInfoFromTracksList.retainStaticTracksGenre());
                } else if (getActivity() instanceof AlbumActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(RetainInfoFromTracksList.retainStaticTracksAlbum());
                } else if (getActivity() instanceof InsidePlaylistActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(RetainInfoFromTracksList.getStaticPlayListTracks());
                } else if (getActivity() instanceof FavoritesActivity) {
                    this.currentQueue.clear();
                    currentQueue.addAll(favoritesTracks);
                }
            } else {
                if (getActivity() instanceof MainActivity)
                    updateCurrentList(RetainInfoFromTracksList.getStaticTracks());
                else if (getActivity() instanceof ArtistActivity)
                    updateCurrentList(RetainInfoFromTracksList.retainStaticTracksArtist());
                else if (getActivity() instanceof GenreActivity)
                    updateCurrentList(RetainInfoFromTracksList.retainStaticTracksGenre());
                else if (getActivity() instanceof AlbumActivity)
                    updateCurrentList(RetainInfoFromTracksList.retainStaticTracksAlbum());
                else if (getActivity() instanceof InsidePlaylistActivity)
                    updateCurrentList(RetainInfoFromTracksList.getStaticPlayListTracks());
                else if (getActivity() instanceof FavoritesActivity)
                    updateCurrentList(favoritesTracks);
            }
        }else{
            currentQueue.clear();
            currentQueue.addAll(modifiedQueue);
        }

        if(!isServiceRegistered) {
            isServiceRegistered = true;
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("current_song"));
        }

        Intent intent = new Intent(getContext(), MusicBackGroundService.class);
        intent.setAction(MusicBackGroundService.ACTION_UPDATE_QUEUE);
        intent.putParcelableArrayListExtra("queue", currentQueue);
        getContext().startService(intent);

        drawable_pause = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_pause);
        drawable_play = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_play);


        if(new ServiceTools(getContext()).isServiceRunning(MusicBackGroundService.class)) {
            Intent in = new Intent(getContext(), MusicBackGroundService.class);
            in.setAction(MusicBackGroundService.ACTION_RETRIEVE_INFO);
            getContext().startService(in);
        }else
            snackImagePausePlay.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_play));

    }

    @Override
    public void onPause() {
        super.onPause();
        if(isServiceRegistered) {
            isServiceRegistered = false;
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        }
    }

    private void updateCurrentList(ArrayList<InfoTrack> currentList){
        this.currentQueue.clear();

        ArrayList<String> temp = new ArrayList<>();
        Iterator<InfoTrack> trackIterator = currentList.iterator();

        while (trackIterator.hasNext()){
            temp.add(trackIterator.next().getPath());
        }
        int currentTrackIndex = temp.indexOf(MusicBackGroundService.getCurrentTrack().getPath());
        currentQueue.addAll(currentList);
        currentQueue.addAll(currentTrackIndex + 1, RetainInfoFromTracksList.retainCurrentQueueList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView =  (ViewGroup) inflater.inflate(R.layout.snack_bar_player_layout, container, false);

        mContentMain = (RelativeLayout) getActivity().findViewById(this.id_content_main);
        mFragmentDragPlayer = (FrameLayout) getActivity().findViewById(this.id_fragment_drag_player);
        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        ButterKnife.bind(this, rootView);
        rootView.setOnClickListener(this);
        mCardViewSnackPlayer.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("currentTrack", (Parcelable) MusicBackGroundService.getCurrentTrack());
        intent.putExtra("currentQueue", currentQueue);
        getContext().startActivity(intent);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public int getSnackBarHeight() {
        return mSnackPlayer.getHeight();
    }

    @Override
    public void onCloseQueueList(ArrayList<InfoTrack> arrayList) {
        boolean isEqualArrayList = true;

        if(currentQueue.size() != arrayList.size())
            isEqualArrayList = false;
        else{
            for(int i = 0; i< currentQueue.size(); i++){
                if(!currentQueue.get(i).getPath().equals(arrayList.get(i).getPath())){
                    isEqualArrayList = false;
                    break;
                }
            }
        }

        modifiedQueue.clear();
        if(!isEqualArrayList){
            modifiedQueue.addAll(arrayList);
        }
    }
}
