package com.sms.musicshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Toast;


import com.sms.musicshare.adapters.AlbumSearchAdapter;
import com.sms.musicshare.adapters.ArtistAdapter;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity{

    private ArrayList<InfoTrack> tracks_list = new ArrayList<>();
    private ArrayList<Artist> artist_list = new ArrayList<>();
    private ArrayList<Album> album_list = new ArrayList<>();
    private ArtistAdapter artistAdapter;
    private AlbumSearchAdapter albumSearchAdapter;
    private TrackAdapter trackAdapter;

    @BindView(R.id.search_recycler_view) RecyclerView rvSearch;
    @BindView(R.id.searchView) SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        searchView.setIconified(false);

        tracks_list.addAll(RetainInfoFromTracksList.getStaticTracks());
        if(getIntent().getBooleanExtra("searchForTrack", false)){

            trackAdapter = new TrackAdapter(SearchActivity.this, tracks_list);
            rvSearch.setAdapter(trackAdapter);
            searchView.setOnQueryTextListener(getInfoTrackQueryListener());

        }else if(getIntent().getBooleanExtra("searchForArtist", false)) {
            artist_list.addAll(RetainInfoFromTracksList.getStaticArtists());
            artistAdapter = new ArtistAdapter(this, artist_list, tracks_list);
            rvSearch.setAdapter(artistAdapter);
            searchView.setOnQueryTextListener(getArtistQueryListener());

        }else if(getIntent().getBooleanExtra("searchForAlbum", false)) {
            album_list.addAll(RetainInfoFromTracksList.getStaticAlbums());
            albumSearchAdapter = new AlbumSearchAdapter(this, album_list);
            rvSearch.setAdapter(albumSearchAdapter);
            searchView.setOnQueryTextListener(getAlbumQueryListener());
        }


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        rvSearch.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressWarnings("unchecked")
    private SearchView.OnQueryTextListener getInfoTrackQueryListener(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<InfoTrack> newListInfoTrack = new ArrayList<>();
                String txt = newText.toLowerCase();
                for (InfoTrack item : tracks_list) {
                    String title = item.getTitle().toLowerCase();
                    String artist = item.getArtist().getName().toLowerCase();
                    String album = item.getAlbum().getName().toLowerCase();
                    if(title.contains(txt) || artist.contains(txt) || album.contains(txt))
                        newListInfoTrack.add(item);
                }

                trackAdapter.setFilter(newListInfoTrack);

                return true;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private SearchView.OnQueryTextListener getArtistQueryListener(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Artist> newListArtist = new ArrayList<>();
                String txt = newText.toLowerCase();
                for (Artist item : artist_list) {
                    String name = item.getName().toLowerCase();
                    if(name.contains(txt))
                        newListArtist.add(item);
                }

                artistAdapter.setFilter(newListArtist);

                return true;
            }
        };
    }


    @SuppressWarnings("unchecked")
    private SearchView.OnQueryTextListener getAlbumQueryListener(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Album> newListAlbum = new ArrayList<>();
                String txt = newText.toLowerCase();
                for (Album item : album_list) {
                    String name = item.getName().toLowerCase();
                    String artist = item.getArtist().getName().toLowerCase();
                    if(name.contains(txt) || artist.contains(txt))
                        newListAlbum.add(item);
                }
                albumSearchAdapter.setFilter(newListAlbum);
                return true;
            }
        };
    }

}
