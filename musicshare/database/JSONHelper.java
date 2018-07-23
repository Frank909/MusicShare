package com.sms.musicshare.database;

import com.sms.musicshare.helper.Album;
import com.sms.musicshare.helper.Artist;
import com.sms.musicshare.helper.Genre;
import com.sms.musicshare.helper.InfoTrack;
import com.sms.musicshare.helper.Prova;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONHelper {

    private JSONObject json_mTrackList;

    public JSONHelper(){
        json_mTrackList = new JSONObject();
    }

    public JSONHelper(String string){
        try{
            json_mTrackList = new JSONObject(string);
        }catch (JSONException e){

        }
    }

    public JSONObject createJSONObject(ArrayList<InfoTrack> mTrackList) {

        try {
            int i=0;
            for (InfoTrack track : mTrackList) {
                JSONObject json_mTrack = new JSONObject();
                json_mTrack.put("path", track.getPath());
                json_mTrack.put("track", track.getTitle());
                    JSONObject json_mArtist = new JSONObject();
                    json_mArtist.put("name", track.getArtist().getName());
                    json_mArtist.put("n_tracks", track.getArtist().getCountTracks());
                    json_mArtist.put("n_album", track.getArtist().getCountAlbums());
                json_mTrack.put("artist", json_mArtist);
                    JSONObject json_mAlbum = new JSONObject();
                    json_mAlbum.put("name", track.getAlbum().getName());
                    if(track.getAlbum().getAlbumArt() == null)
                        json_mAlbum.put("album_art", "unknow");
                    else
                        json_mAlbum.put("album_art", track.getAlbum().getAlbumArt());
                    json_mAlbum.put("artist", track.getAlbum().getArtist());
                    json_mAlbum.put("n_tracks", track.getAlbum().getNTracks());
                json_mTrack.put("album", json_mAlbum);
                    JSONObject json_mGenre = new JSONObject();
                    json_mGenre.put("name", track.getGenre().getName());
                    json_mGenre.put("n_tracks", track.getGenre().getNTracks());
                json_mTrack.put("genre", json_mGenre);
                json_mTrack.put("favorites", track.isFavorite());
                json_mTrack.put("shared", track.isShared());
                json_mTrack.put("duration", track.getDuration());
                json_mTrackList.put("track_" + i, json_mTrack);
                i++;
            }
        }
        catch (JSONException e) {
        }

        return json_mTrackList;
    }

    public ArrayList<InfoTrack> createArrayListFromJSON(){

        ArrayList<InfoTrack> mTrackList = new ArrayList<>();
        Artist mArtist = new Artist();
        Album mAlbum = new Album();
        Genre mGenre = new Genre();

        try{
            for(int i=0; i<getJSONSize(); i++) {
                JSONObject jsonTrack = json_mTrackList.getJSONObject("track_" + i);
                JSONObject jsonArtist = jsonTrack.getJSONObject("artist");
                mArtist.setName(jsonArtist.getString("name"));
                mArtist.setCountAlbums(jsonArtist.getString("n_album"));
                mArtist.setCountTracks(jsonArtist.getString("n_tracks"));
                JSONObject jsonAlbum = jsonTrack.getJSONObject("album");
                    mAlbum.setName(jsonAlbum.getString("name"));
                    mAlbum.setAlbumArt(jsonAlbum.getString("album_art"));
                    mAlbum.setNTracks(jsonAlbum.getString("n_tracks"));
                    mAlbum.setArtist(mArtist);
                JSONObject jsonGenre = jsonTrack.getJSONObject("genre");
                    mGenre.setName(jsonGenre.getString("name"));
                    mGenre.setNTracks(jsonGenre.getString("n_tracks"));

                mTrackList.add(new InfoTrack(jsonTrack.getString("path"),
                        jsonTrack.getString("track"),
                        mArtist, mAlbum, mGenre,
                        jsonTrack.getBoolean("favorites"),
                        jsonTrack.getBoolean("shared"),
                        jsonTrack.getLong("duration")));
            }
        }catch (JSONException e){
        }

        return  mTrackList;
    }

    private int getJSONSize(){
        return json_mTrackList.length();
    }

    public JSONObject returnJSONObject() {
        return json_mTrackList;
    }
}
