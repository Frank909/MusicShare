package com.sms.musicshare;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.animation.ResizeAnimation;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenreActivity extends AppCompatActivity{

    @BindView(R.id.genre_toolbar) Toolbar toolbar;
    @BindView(R.id.genre_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.content_genre) RelativeLayout mContentMain;
    @BindView(R.id.track_genre_list_recycler_view) RecyclerView rvTracksList;

    private int snackHeight;
    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight;
    private TrackAdapter trackAdapter;
    private ArrayList<InfoTrack> mTracks;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("PlayerHeight", mDragPlayerContainer.getHeight());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        ButterKnife.bind(this);

        String genre_name = getIntent().getStringExtra("genre_name");
        mTracks = getIntent().getParcelableArrayListExtra("tracksFromGenresList");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(genre_name);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trackAdapter = new TrackAdapter(this, mTracks);
        rvTracksList.setAdapter(trackAdapter);
        rvTracksList.setLayoutManager(new LinearLayoutManager(this));

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.genre_fragment_drag_player, R.id.content_genre);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.genre_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
        /*************************************************************************/

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDragPlayerContainer.getHeight() > mDragPlayerFragment.getSnackBarHeight())
        {
            //int mResult = this.getWindow().getDecorView().getWidth();
            int mResult = this.getWindow().getDecorView().getWidth()-(mSystemBarHeight);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();
            layoutParams.height = mResult;
            mDragPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
