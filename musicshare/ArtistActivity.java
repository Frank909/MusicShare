package com.sms.musicshare;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sms.musicshare.adapters.ArtistPageAdapter;
import com.sms.musicshare.animation.ResizeAnimation;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistActivity extends AppCompatActivity{

    @BindView(R.id.artist_toolbar) Toolbar toolbar;
    @BindView(R.id.artist_view_pager) ViewPager mViewPager;
    @BindView(R.id.artist_tab_layout) TabLayout mTabLayout;
    @BindView(R.id.artist_fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.activity_artist_content_main_layout) RelativeLayout mContentMain;
    @BindView(R.id.artist_appBarLayout) AppBarLayout mAppBarLayout;

    private int snackHeight;
    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight;
    private ArrayList<InfoTrack> tracks_artist;
    private Artist artist;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("PlayerHeight", mDragPlayerContainer.getHeight());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            tracks_artist = getIntent().getParcelableArrayListExtra("tracks_list_from_artist");
            artist = getIntent().getParcelableExtra("artist");

            getSupportActionBar().setTitle(artist.getName());

            RetainInfoFromTracksList retainInfoFromTracksList = new RetainInfoFromTracksList(tracks_artist);
            ArrayList<Album> albumArrayList = retainInfoFromTracksList.retainAlbumsListFromArtist(tracks_artist);

            mViewPager.setAdapter(new ArtistPageAdapter(getSupportFragmentManager(), this, tracks_artist,
                    albumArrayList, artist));
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.colorWhite));
            mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));

            /**
             *  Listener del TabLayout
             */
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */
        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.artist_fragment_drag_player, R.id.activity_artist_content_main_layout);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.artist_fragment_drag_player, mDragPlayerFragment, "DragPlayer");
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
