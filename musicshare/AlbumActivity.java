package com.sms.musicshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
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
import com.sms.musicshare.helper.MusicBackGroundService;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumActivity extends AppCompatActivity{

    @BindView(R.id.album_toolbar) Toolbar toolbar;
    @BindView(R.id.album_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.content_album) RelativeLayout mContentMain;
    @BindView(R.id.track_album_list_recycler_view) RecyclerView rvTracksList;
    @BindView(R.id.album_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayoutAlbum;

    private String album_name;
    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight;
    private ArrayList<InfoTrack> mTracks;
    private TrackAdapter trackAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("PlayerHeight", mDragPlayerContainer.getHeight());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            album_name = getIntent().getStringExtra("album_name");
            mTracks = getIntent().getParcelableArrayListExtra("tracksFromAlbumsList");
            getSupportActionBar().setTitle(album_name);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if(sp.getBoolean("pref_show_image_album_summary", true)) {
                Drawable album_art = Drawable.createFromPath(mTracks.get(0).getAlbum().getAlbumArt());
                if (!mTracks.get(0).getAlbum().getAlbumArt().equals("<unknown>")) {
                    File imgFile = new File(mTracks.get(0).getAlbum().getAlbumArt());
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
                    Palette palette = Palette.from(bitmap).generate();
                    collapsingToolbarLayoutAlbum.setContentScrimColor(palette.getDominantColor(0));
                }
                collapsingToolbarLayoutAlbum.setBackground(album_art);
            }

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

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
            mDragPlayerFragment.setLayoutId(R.id.album_fragment_drag_player, R.id.content_album);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.album_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
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
