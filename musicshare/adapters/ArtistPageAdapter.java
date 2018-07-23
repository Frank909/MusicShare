package com.sms.musicshare.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sms.musicshare.AlbumFragment;
import com.sms.musicshare.R;
import com.sms.musicshare.TrackFragment;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.InfoTrack;

import java.util.ArrayList;

public class ArtistPageAdapter extends FragmentPagerAdapter {

    private Bundle bundle;
    private String[] fragments;

    public ArtistPageAdapter(FragmentManager supportFragmentManager, Context applicationContext, ArrayList<InfoTrack> tracks_artist,
                             ArrayList<Album> albums_artist, Artist artist){


        super(supportFragmentManager);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        if(sp.getBoolean("pref_show_count_albums_tracks", true)) {
            fragments = new String[]{
                    applicationContext.getResources().getQuantityString(R.plurals.number_of_playlist_tracks, Integer.parseInt(artist.getCountTracks()), Integer.parseInt(artist.getCountTracks())) + "(" + artist.getCountTracks() + ")",
                    applicationContext.getResources().getQuantityString(R.plurals.number_of_playlist_albums, Integer.parseInt(artist.getCountAlbums()), Integer.parseInt(artist.getCountAlbums())) + "(" + artist.getCountAlbums() + ")"
            };
        }else
            fragments = new String[]{
                    applicationContext.getResources().getQuantityString(R.plurals.number_of_playlist_tracks, Integer.parseInt(artist.getCountTracks()), Integer.parseInt(artist.getCountTracks())),
                    applicationContext.getResources().getQuantityString(R.plurals.number_of_playlist_albums, Integer.parseInt(artist.getCountAlbums()), Integer.parseInt(artist.getCountAlbums()))
            };

        bundle = new Bundle();
        bundle.putParcelableArrayList("album_artist_list", albums_artist);
        bundle.putParcelableArrayList("tracks_artist_list", tracks_artist);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                TrackFragment trackFragment = new TrackFragment();
                trackFragment.setArguments(bundle);
                return trackFragment;
            case 1:
                AlbumFragment albumFragment = new AlbumFragment();
                albumFragment.setArguments(bundle);
                return albumFragment;
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
