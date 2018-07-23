package com.sms.musicshare.helper;

import android.icu.text.IDNA;

import java.util.ArrayList;
import java.util.Iterator;

public class RetainInfoFromTracksList {

    private ArrayList<InfoTrack> mTracks;
    private static ArrayList<InfoTrack> mStaticTracks = new ArrayList<>();
    private static ArrayList<Artist> mStaticArtists = new ArrayList<>();
    private static ArrayList<Album> mStaticAlbums = new ArrayList<>();
    private static ArrayList<Genre> mStaticGenre = new ArrayList<>();
    private static ArrayList<InfoTrack> mStaticPlayListTracks = new ArrayList<>();
    private ArrayList<Artist> mArtists = new ArrayList<>();
    private ArrayList<Album> mAlbums = new ArrayList<>();
    private ArrayList<Genre> mGenres = new ArrayList<>();
    private static ArrayList<InfoTrack> mQueue = new ArrayList<>();
    private static ArrayList<InfoTrack> mCurrentTracksArtist = new ArrayList<>();
    private static ArrayList<InfoTrack> mCurrentTracksGenre = new ArrayList<>();
    private static ArrayList<InfoTrack> mCurrentTracksAlbum = new ArrayList<>();

    public RetainInfoFromTracksList(ArrayList<InfoTrack> infoTracks){
        this.mTracks = infoTracks;
    }

    public ArrayList<Genre> retainGenresList(){
        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();
        do{
            boolean isInserted = false;
            Genre genre = infoTrackIterator.next().getGenre();

            if(this.mGenres.isEmpty())
                this.mGenres.add(genre);
            else {
                for (Genre mGenre : this.mGenres) {
                    if (genre.getName().equals(mGenre.getName()) && genre.getNTracks().equals(mGenre.getNTracks())) {
                        isInserted = true;
                        break;
                    }
                }
                if(!isInserted)
                    mGenres.add(genre);
            }
        }while (infoTrackIterator.hasNext());
        return this.mGenres;
    }

    public static void setStaticTracks(ArrayList<InfoTrack> mStaticTracks) {
        RetainInfoFromTracksList.mStaticTracks = mStaticTracks;
    }

    public static void setStaticArtists(ArrayList<Artist> mStaticArtists) {
        RetainInfoFromTracksList.mStaticArtists = mStaticArtists;
    }

    public static void setStaticAlbums(ArrayList<Album> mStaticAlbums) {
        RetainInfoFromTracksList.mStaticAlbums = mStaticAlbums;
    }

    public static void setStaticGenres(ArrayList<Genre> mStaticGenre) {
        RetainInfoFromTracksList.mStaticGenre = mStaticGenre;
    }

    public static void setStaticPlayListTracks(ArrayList<InfoTrack> mStaticPlayListTracks){
        RetainInfoFromTracksList.mStaticPlayListTracks = mStaticPlayListTracks;
    }

    public static ArrayList<InfoTrack> getStaticTracks() {
        return mStaticTracks;
    }

    public static ArrayList<Artist> getStaticArtists() {
        return mStaticArtists;
    }

    public static ArrayList<Album> getStaticAlbums() {
        return mStaticAlbums;
    }

    public static ArrayList<Genre> getStaticGenres() {
        return mStaticGenre;
    }

    public static ArrayList<InfoTrack> getStaticPlayListTracks(){
        return mStaticPlayListTracks;
    }

    public ArrayList<Artist> retainArtistsList(){
        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();
        do{
            boolean isInserted = false;
            Artist artist = infoTrackIterator.next().getArtist();

            if(this.mArtists.isEmpty())
                this.mArtists.add(artist);
            else {
                for (Artist mArtist : this.mArtists) {
                    if (artist.getName().equals(mArtist.getName())) {
                        isInserted = true;
                        break;
                    }
                }
                if(!isInserted)
                    this.mArtists.add(artist);
            }
        }while (infoTrackIterator.hasNext());
        return this.mArtists;
    }

    public ArrayList<Album> retainAlbumList(){

        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();

        do{
            boolean isInserted = false;
            Album album = infoTrackIterator.next().getAlbum();
            if(this.mAlbums.isEmpty())
                this.mAlbums.add(album);
            else {
                for (Album mAlbum : this.mAlbums) {
                    if (album.getName().equals(mAlbum.getName())){
                        isInserted = true;
                        break;
                    }
                }
                if(!isInserted)
                    this.mAlbums.add(album);
            }
        }while (infoTrackIterator.hasNext());

        return this.mAlbums;
    }

    public ArrayList<InfoTrack> retainTracksListFromArtist(Artist artist){
        ArrayList<InfoTrack> arrayList = new ArrayList<>();

        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();
        do{
            InfoTrack item = infoTrackIterator.next();

            if(artist.getName().equals(item.getArtist().getName()) && artist.getCountAlbums().equals(item.getArtist().getCountAlbums()) && artist.getCountTracks().equals(item.getArtist().getCountTracks()))
                arrayList.add(item);

        }while (infoTrackIterator.hasNext());
        if(!mCurrentTracksArtist.isEmpty())
            mCurrentTracksArtist.clear();
        mCurrentTracksArtist.addAll(arrayList);
        return arrayList;
    }

    public ArrayList<Album> retainAlbumsListFromArtist(ArrayList<InfoTrack> infoTracks){
        ArrayList<Album> arrayList = new ArrayList<>();
        Iterator<InfoTrack> infoTrackIterator = infoTracks.iterator();

        do{
            boolean isInserted = false;
            InfoTrack trackItem = infoTrackIterator.next();
            Album album = trackItem.getAlbum();

            if(arrayList.isEmpty())
                arrayList.add(trackItem.getAlbum());
            else {
                for (Album mAlbum : arrayList) {
                    if (album.getName().equals(mAlbum.getName())){
                        isInserted = true;
                        break;
                    }
                }
                if(!isInserted)
                    arrayList.add(album);
            }
        }while (infoTrackIterator.hasNext());
        return arrayList;
    }

    public ArrayList<InfoTrack> retainTracksListFromAlbum(Album album) {
        ArrayList<InfoTrack> arrayList = new ArrayList<>();

        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();
        do{
            InfoTrack item = infoTrackIterator.next();

            if(album.getName().equals(item.getAlbum().getName()) && album.getNTracks().equals(item.getAlbum().getNTracks()) && album.getAlbumArt().equals(item.getAlbum().getAlbumArt()))
                arrayList.add(item);

        }while (infoTrackIterator.hasNext());

        if(!mCurrentTracksAlbum.isEmpty())
            mCurrentTracksAlbum.clear();
        mCurrentTracksAlbum.addAll(arrayList);
        return arrayList;
    }

    public ArrayList<InfoTrack> retainTracksListFromGenre(Genre genre) {
        ArrayList<InfoTrack> arrayList = new ArrayList<>();

        Iterator<InfoTrack> infoTrackIterator = mTracks.iterator();
        do{
            InfoTrack item = infoTrackIterator.next();

            if(genre.getName().equals(item.getGenre().getName()) && genre.getNTracks().equals(item.getGenre().getNTracks()))
                arrayList.add(item);

        }while (infoTrackIterator.hasNext());

        if(!mCurrentTracksGenre.isEmpty())
            mCurrentTracksGenre.clear();
        mCurrentTracksGenre.addAll(arrayList);
        return arrayList;
    }

    public void retainFavoritesTracksList(ArrayList<InfoTrack> favorites_tracks) {
        for(InfoTrack static_track : mStaticTracks)
            for(InfoTrack favorites : favorites_tracks){
                if(static_track.getPath().equals(favorites.getPath()))
                    static_track.setFavorite(true);
                break;
        }
    }


    public static void refreshFavoritesLists(ArrayList<InfoTrack> favorites){
        for (InfoTrack track : mCurrentTracksAlbum) {
            for(InfoTrack favorites_track : favorites)
                if(track.getPath().equals(favorites_track.getPath()))
                    track.setFavorite(true);
                else
                    track.setFavorite(false);
        }

        for (InfoTrack track : mCurrentTracksGenre) {
            for(InfoTrack favorites_track : favorites)
                if(track.getPath().equals(favorites_track.getPath()))
                    track.setFavorite(true);
                else
                    track.setFavorite(false);
        }

        for (InfoTrack track : mCurrentTracksArtist) {
            for(InfoTrack favorites_track : favorites)
                if(track.getPath().equals(favorites_track.getPath()))
                    track.setFavorite(true);
                else
                    track.setFavorite(false);
        }
    }

    public static void setStaticQueue(ArrayList<InfoTrack> Queue){
        mQueue.addAll(Queue);
    }

    public static ArrayList<InfoTrack> retainCurrentQueueList(){ return mQueue; }

    public static ArrayList<InfoTrack> retainStaticTracksArtist(){
        return mCurrentTracksArtist;
    }

    public static ArrayList<InfoTrack> retainStaticTracksGenre(){
        return mCurrentTracksGenre;
    }

    public static ArrayList<InfoTrack> retainStaticTracksAlbum(){
        return mCurrentTracksAlbum;
    }

}
