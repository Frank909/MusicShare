package com.sms.musicshare.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sms.musicshare.AlbumFragment;
import com.sms.musicshare.ArtistFragment;
import com.sms.musicshare.GenreFragment;
import com.sms.musicshare.R;
import com.sms.musicshare.TrackFragment;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;

import java.util.ArrayList;

public class MainPageAdapter extends FragmentPagerAdapter {

    private String[] fragments;
    private Bundle bundle;

    public MainPageAdapter(FragmentManager supportFragmentManager, Context applicationContext){
        super(supportFragmentManager);
        fragments = new String[] {
                applicationContext.getResources().getString(R.string.dialog_track),
                applicationContext.getResources().getString(R.string.dialog_Artist),
                applicationContext.getResources().getString(R.string.dialog_Album),
                applicationContext.getResources().getString(R.string.dialog_Genre)
        };

        bundle = new Bundle();
        bundle.putBoolean("staticInformation", true);

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                TrackFragment trackFragment = new TrackFragment();
                trackFragment.setArguments(bundle);
                return trackFragment;
            case 1:
                ArtistFragment artistFragment = new ArtistFragment();
                artistFragment.setArguments(bundle);
                return artistFragment;
            case 2:
                AlbumFragment albumFragment = new AlbumFragment();
                albumFragment.setArguments(bundle);
                return albumFragment;
            case 3:
                GenreFragment genreFragment = new GenreFragment();
                genreFragment.setArguments(bundle);
                return genreFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragments[position];
    }
}
