package com.sms.musicshare;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.musicshare.adapters.MyMusicSharedAdapter;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;
import com.sms.musicshare.adapters.TrackSelectionAdapter;
import com.sms.musicshare.database.DBHelper;
import com.sms.musicshare.database.JSONHelper;
import com.sms.musicshare.helper.RetainInfoFromTracksList;
import com.sms.musicshare.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackPlaylistSelectionActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    @BindView(R.id.id_toolbar_track_playlist_selection) Toolbar mToolBar;
    @BindView(R.id.id_counter_track_selected) TextView mCounterTextView;
    @BindView(R.id.id_search_track_selected) SearchView mSearchView;
    @BindString(R.string.no_track_selected) String noTrackSelected;

    int counter;
    private ArrayList<InfoTrack> mTrackList = new ArrayList<>();
    private ArrayList<Boolean> mItemsChecked = new ArrayList<>();
    private TrackSelectionAdapter adapter;
    private DBHelper mDBHelper;
    private int playlist_id;
    private String playlist_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_playlist_selection);

        ButterKnife.bind(this);

        counter = 0;
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHelper = new DBHelper(this);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setVisibility(View.GONE);

        playlist_id = getIntent().getIntExtra("playlist_id",-1);
        playlist_name = getIntent().getStringExtra("playlist_name");

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchView.setVisibility(View.GONE);
                mCounterTextView.setVisibility(View.VISIBLE);
                return true;
            }
        });

        final RecyclerView rvPlaylist = (RecyclerView) findViewById(R.id.id_track_playlist_selection_list);

        mTrackList.addAll(RetainInfoFromTracksList.getStaticTracks());

        ArrayList<InfoTrack> new_array = new ArrayList<>();
        JSONHelper mJSONHelper;
        if(getIntent().getExtras().containsKey("favorites_tracks")){
            mJSONHelper = new JSONHelper(getIntent().getStringExtra("favorites_tracks"));
            createArrayFromIntent(new_array, mJSONHelper);
        }
        else if(getIntent().getExtras().containsKey("playlist_tracks")){
            mJSONHelper = new JSONHelper(getIntent().getStringExtra("playlist_tracks"));
            createArrayFromIntent(new_array, mJSONHelper);
        }else if(getIntent().getExtras().containsKey("shared_tracks")){
            mJSONHelper = new JSONHelper(getIntent().getStringExtra("shared_tracks"));
            createArrayFromIntent(new_array, mJSONHelper);
        }

        for(int i = 1; i<= mTrackList.size(); i++)
            mItemsChecked.add(false);

        adapter = new TrackSelectionAdapter(this, mTrackList, mItemsChecked);
        // Attach the adapter to the recyclerview to populate items
        rvPlaylist.setAdapter(adapter);
        // Set layout manager to position the items
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

        rvPlaylist.addOnItemTouchListener(new RecyclerItemClickListener(this, rvPlaylist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if(getIntent().getExtras().containsKey("playlist_tracks"))
                    goToActivity(InsidePlaylistActivity.class);
                else if(getIntent().getExtras().containsKey("favorites_tracks"))
                    goToActivity(FavoritesActivity.class);
                else if(getIntent().getExtras().containsKey("shared_tracks"))
                    goToActivity(MyMusicSharedActivity.class);
                break;

            case R.id.item_track_playlist_selection_search:
                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.setIconified(false);
                mCounterTextView.setVisibility(View.GONE);
                break;

            case R.id.item_track_playlist_selection_done:
                ArrayList<InfoTrack> doInsertTrack = new ArrayList<>();
                ArrayList<Boolean> itemsChecked = (ArrayList<Boolean>) adapter.getListCheckedItems();
                Iterator<Boolean> booleanIterator = itemsChecked.iterator();
                boolean flag = false;
                int i = 0;

                while (booleanIterator.hasNext()) {
                    if (booleanIterator.next()) {
                        doInsertTrack.add(mTrackList.get(i));
                        if (getIntent().getExtras().containsKey("favorites_tracks"))
                            doInsertTrack.get(doInsertTrack.size()-1).setFavorite(true);
                        if (getIntent().getExtras().containsKey("shared_tracks"))
                            doInsertTrack.get(doInsertTrack.size()-1).setShared(true);
                        flag = true;
                    }
                    i++;
                }

                if (flag) {
                    try {
                        if (getIntent().getExtras().containsKey("favorites_tracks")) {
                            mDBHelper.insertTracksInFavorites(doInsertTrack);
                            for(InfoTrack insert_track : doInsertTrack)
                                for(InfoTrack track : RetainInfoFromTracksList.getStaticTracks())
                                    if(insert_track.getPath().equals(track.getPath()))
                                        track.setFavorite(true);
                            goToActivity(FavoritesActivity.class);
                        }else if (getIntent().getExtras().containsKey("shared_tracks")) {
                            mDBHelper.insertTracksInSharedMusic(doInsertTrack);
                            for(InfoTrack insert_track : doInsertTrack)
                                for(InfoTrack track : RetainInfoFromTracksList.getStaticTracks())
                                    if(insert_track.getPath().equals(track.getPath()))
                                        track.setShared(true);
                            goToActivity(MyMusicSharedActivity.class);
                        }else{
                            mDBHelper.insertTracksInPlayList(doInsertTrack, playlist_id);
                            goToActivity(InsidePlaylistActivity.class);
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(this, noTrackSelected, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_track_playlist_selection, menu);
        return true;
    }

    public void prepareSelection(View v, int position) {
        if(((CheckBox)v).isChecked()) {
            counter++;
            updateCounter(counter);
        }else{
            counter--;
            updateCounter(counter);
        }
    }

    public void updateCounter(int counter){
            Resources res = getResources();
            String string = res.getQuantityString(R.plurals.number_item_track_playlist_selection_activity, counter);
            mCounterTextView.setText(counter + " " + string);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<InfoTrack> newList = new ArrayList<>();
        ArrayList<Boolean> newBoolean = new ArrayList<>();
        int i = 0;
        for (InfoTrack item : mTrackList) {
            String name = item.getTitle().toLowerCase();
            if(name.contains(newText)){
                newList.add(item);
                newBoolean.add(mItemsChecked.get(i));
            }
            i++;
        }
        adapter.setFilter(newList, newBoolean);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getExtras().containsKey("playlist_tracks"))
            goToActivity(InsidePlaylistActivity.class);
        else if(getIntent().getExtras().containsKey("favorites_tracks"))
            goToActivity(FavoritesActivity.class);
        else if(getIntent().getExtras().containsKey("shared_tracks"))
            goToActivity(MyMusicSharedActivity.class);
    }

    private void goToActivity(Class<?> cls){
        Intent intent = new Intent(this, cls);
        if(!getIntent().getExtras().containsKey("favorites_tracks")){
            intent.putExtra("playlist_id",playlist_id);
            intent.putExtra("playlist_name",playlist_name);
        }
        startActivity(intent);
        finish();
    }

    private void createArrayFromIntent(ArrayList<InfoTrack> array, JSONHelper jsonHelper) {
        array = jsonHelper.createArrayListFromJSON();
        Iterator<InfoTrack> tracks_iterator = mTrackList.iterator();
        while(tracks_iterator.hasNext()) {
            String path_iterator = tracks_iterator.next().getPath();
            for(int i=0;i< array.size();i++) {
                String path_new_array = array.get(i).getPath();
                if (path_iterator.equals(path_new_array))
                    tracks_iterator.remove();
            }
        }
    }
}


