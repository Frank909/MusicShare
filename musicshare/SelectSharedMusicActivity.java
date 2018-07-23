package com.sms.musicshare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sms.musicshare.adapters.AlbumSearchAdapter;
import com.sms.musicshare.adapters.ArtistAdapter;
import com.sms.musicshare.adapters.TrackAdapter;
import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.RetainInfoFromTracksList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectSharedMusicActivity extends AppCompatActivity {

    private ArrayList<InfoTrack> tracks_list = new ArrayList<>();
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

        trackAdapter = new TrackAdapter(SelectSharedMusicActivity.this, tracks_list);
        rvSearch.setAdapter(trackAdapter);
        searchView.setOnQueryTextListener(getInfoTrackQueryListener());

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

}
