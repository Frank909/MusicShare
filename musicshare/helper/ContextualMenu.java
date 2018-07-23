package com.sms.musicshare.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sms.musicshare.AlbumActivity;
import com.sms.musicshare.ArtistActivity;
import com.sms.musicshare.ContextualPlaylistActivity;
import com.sms.musicshare.FavoritesActivity;
import com.sms.musicshare.InsidePlaylistActivity;
import com.sms.musicshare.R;
import com.sms.musicshare.database.DBHelper;
import java.util.ArrayList;


public class ContextualMenu{

    private Context mContext;
    private AlertDialog dialog;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private DBHelper mDBHelper;
    private String string_removeFavorites,
            string_addFavorites,
            string_addPlaylist,
            string_addToPlayQueue,
            string_goToAlbum,
            string_goToArtist,
            string_removeShared,
            string_addShared;

    public static String
            ADD_IN_FAVORITES = "",
            REMOVE_FROM_FAVORITES = "",
            ADD_IN_PLAYLIST = "",
            ADD_TO_PLAY_QUEUE = "",
            GO_TO_ALBUM = "",
            GO_TO_ARTIST = "",
            ADD_TO_SHARED = "",
            REMOVE_FROM_SHARED = "";


    public ContextualMenu(Context context){
        this.mContext = context;
        mDBHelper = new DBHelper(context);
    }

    public Context getContext(){return this.mContext;}

    public void createContextualMenu(final InfoTrack track) {
        stringArrayList.clear();
        if(!(getContext() instanceof InsidePlaylistActivity) &&
                !(getContext() instanceof FavoritesActivity)){

            initStrings();

            if(track.isFavorite())
                stringArrayList.add(string_removeFavorites);

            else
                stringArrayList.add(string_addFavorites);

            if(track.isShared())
                stringArrayList.add(string_removeShared);
            else
                stringArrayList.add(string_addShared);

            stringArrayList.add(string_addPlaylist);
            stringArrayList.add(string_addToPlayQueue);

            if(!(getContext() instanceof AlbumActivity) &&
                    !(getContext() instanceof ArtistActivity)) {
                stringArrayList.add(string_goToAlbum);
                stringArrayList.add(string_goToArtist);
            }

            ListView listView = new ListView(getContext());

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.contextual_item_menu,
                    R.id.txt_item, stringArrayList.toArray(new String[stringArrayList.size()]));
            listView.setAdapter(arrayAdapter);


            ViewGroup v = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.header_contextual_menu, listView, false);

            ImageView albumImage = (ImageView) v.findViewById(R.id.album_img);
            TextView titleText = (TextView) v.findViewById(R.id.txtTitle);
            TextView artistText = (TextView) v.findViewById(R.id.txtArtist);

            if(track.getAlbum().getAlbumArt().equals("<unknown>"))
                albumImage.setImageResource(R.drawable.cd);
            else
                albumImage.setImageDrawable(Drawable.createFromPath(track.getAlbum().getAlbumArt()));
            
            titleText.setText(track.getTitle());
            artistText.setText(track.getArtist().getName());

            listView.addHeaderView(v, null, false);
            listView.setHeaderDividersEnabled(true);
            listView.setOnItemClickListener(itemClickListener(track));
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setCancelable(true);
            builder.setView(listView);
            dialog = builder.create();
            dialog.show();
        }
    }

    private void initStrings(){
        string_addFavorites = getContext().getResources().getString(R.string.text_favorites_popup_menu);
        string_removeFavorites = getContext().getResources().getString(R.string.text_favorites_popup_menu_remove);
        string_addPlaylist = getContext().getResources().getString(R.string.text_playlist_popup_menu);
        string_addToPlayQueue = getContext().getResources().getString(R.string.text_codeplay_popup_menu);
        string_goToAlbum = getContext().getResources().getString(R.string.text_album_popup_menu);
        string_goToArtist = getContext().getResources().getString(R.string.text_artist_popup_menu);
        string_addShared = getContext().getResources().getString(R.string.set_shared_popup_menu);
        string_removeShared = getContext().getResources().getString(R.string.remove_shared_popup_menu);

        ADD_IN_FAVORITES = string_addFavorites;
        REMOVE_FROM_FAVORITES = string_removeFavorites;
        ADD_IN_PLAYLIST = string_addPlaylist;
        ADD_TO_PLAY_QUEUE = string_addToPlayQueue;
        GO_TO_ALBUM = string_goToAlbum;
        GO_TO_ARTIST = string_goToArtist;
        ADD_TO_SHARED = string_addShared;
        REMOVE_FROM_SHARED = string_removeShared;
    }

    private AdapterView.OnItemClickListener itemClickListener(final InfoTrack track){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;

                String item = adapterView.getAdapter().getItem(i).toString();

                if(item.equals(ADD_IN_FAVORITES)){
                    track.setFavorite(true);
                    mDBHelper.insertSingleTrackInFavorites(track);
                }else if(item.equals(REMOVE_FROM_FAVORITES)) {
                    track.setFavorite(false);
                    mDBHelper.removeTrackFromFavorites(track.getPath());
                }else if(item.equals(ADD_TO_SHARED)) {
                    track.setShared(true);
                    mDBHelper.insertSingleTrackInSharedMusic(track);
                }else if(item.equals(REMOVE_FROM_SHARED)){
                    track.setShared(false);
                    mDBHelper.removeTrackFromSharedMusic(track.getPath());
                }else if(item.equals(ADD_IN_PLAYLIST)){
                    intent = new Intent(getContext(), ContextualPlaylistActivity.class);
                    intent.putExtra("TrackForPlaylist", (Parcelable) track);
                    getContext().startActivity(intent);
                }else if(item.equals(ADD_TO_PLAY_QUEUE))
                    RetainInfoFromTracksList.retainCurrentQueueList().add(track);
                else if(item.equals(GO_TO_ALBUM)){
                    ArrayList<InfoTrack> tracksFromAlbumsList = new RetainInfoFromTracksList(RetainInfoFromTracksList.getStaticTracks()).retainTracksListFromAlbum(track.getAlbum());
                    intent = new Intent(getContext(), AlbumActivity.class);
                    intent.putExtra("album_name", track.getAlbum().getName());
                    intent.putParcelableArrayListExtra("tracksFromAlbumsList", tracksFromAlbumsList);
                    getContext().startActivity(intent);
                }else if(item.equals(GO_TO_ARTIST)){
                    ArrayList<InfoTrack> infoTracks =  new RetainInfoFromTracksList(RetainInfoFromTracksList.getStaticTracks()).retainTracksListFromArtist(track.getArtist());
                    intent = new Intent(getContext(), ArtistActivity.class);
                    intent.putParcelableArrayListExtra("tracks_list_from_artist", infoTracks);
                    intent.putExtra("artist", track.getArtist());
                    getContext().startActivity(intent);
                }

                dialog.dismiss();
            }
        };
    }
}
