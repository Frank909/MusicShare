package com.sms.musicshare;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.Toolbar;

import android.util.DisplayMetrics;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sms.musicshare.adapters.MainPageAdapter;
import com.sms.musicshare.animation.ResizeAnimation;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.helper.ContextualMenu;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.MusicBackGroundService;
import com.sms.musicshare.helper.MusicLibraryScanner;
import com.sms.musicshare.helper.OnContextualMenuListener;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.fragment_drag_player) FrameLayout mDragPlayerContainer;
    @BindView(R.id.content_main) RelativeLayout mContentMain;
    @BindView(R.id.appBarLayout) AppBarLayout mAppBarLayout;

    private DragPlayerFragment mDragPlayerFragment;
    private int mSystemBarHeight;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("PlayerHeight", mDragPlayerContainer.getHeight());
        super.onSaveInstanceState(outState);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mSystemBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.colorWhite));

        RetainInfoFromTracksList retainInfoFromTracksList = new RetainInfoFromTracksList(RetainInfoFromTracksList.getStaticTracks());

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        ArrayList<InfoTrack> favorites_tracksList = dbHelper.getAllFavoritesTracks();
        if(!favorites_tracksList.isEmpty())
            retainInfoFromTracksList.retainFavoritesTracksList(favorites_tracksList);

        populateTabsLists();

        /**
         * Recupera lo stato del fragment del player quando il dispositivo viene girato.
         */

        if(getSupportFragmentManager().findFragmentByTag("DragPlayer") != null)
            mDragPlayerFragment = (DragPlayerFragment)getSupportFragmentManager().findFragmentByTag("DragPlayer");
        else{
            mDragPlayerFragment = new DragPlayerFragment();
            mDragPlayerFragment.setLayoutId(R.id.fragment_drag_player, R.id.content_main);
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.add(R.id.fragment_drag_player, mDragPlayerFragment, "DragPlayer");
            trans.commit();
        }
    }
        /*************************************************************************/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDragPlayerContainer.getHeight() > mDragPlayerFragment.getSnackBarHeight())
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int mResult = this.getWindow().getDecorView().getWidth()-(mSystemBarHeight);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mDragPlayerContainer.getLayoutParams();
            layoutParams.height = mResult;
            mDragPlayerContainer.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SearchActivity.class);

        switch (item.getItemId()){
            case R.id.action_search_track:
                intent.putExtra("searchForTrack", true);
                startActivity(intent);
                break;
            case R.id.action_search_artist:
                intent.putExtra("searchForArtist", true);
                startActivity(intent);
                break;
            case R.id.action_search_album:
                intent.putExtra("searchForAlbum", true);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()){
            case R.id.nav_favorites:
                Intent intent_favorites = new Intent(this, FavoritesActivity.class);
                startActivity(intent_favorites);
                break;

            case R.id.nav_info:
                Intent intent_info = new Intent(this, InfoActivity.class);
                startActivity(intent_info);
                break;

            case R.id.nav_playlists:
                Intent intent_playlists = new Intent(this, PlaylistActivity.class);
                startActivity(intent_playlists);
                break;

            case R.id.nav_search:
                Intent intent_search = new Intent(this, DevicesActivity.class);
                startActivity(intent_search);
                break;

            case R.id.nav_settings:
                Intent intent_settings = new Intent(this, SettingsActivity.class);
                startActivity(intent_settings);
                break;

            case R.id.nav_sharing_on:
                Intent intent_sharing_on = new Intent(this, MyMusicSharedActivity.class);
                startActivity(intent_sharing_on);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateTabsLists(){
        mViewPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), MainActivity.this));
        mTabLayout.setupWithViewPager(mViewPager);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MusicBackGroundService.class));
    }
}