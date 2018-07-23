package com.sms.musicshare.helper;

import java.util.ArrayList;

public class Prova {
    private String mPath;
    private String mTrack;
    private String mArtist;
    private String mAlbums;
    private String mGenre;


    public Prova( String path, String track, String artist, String albums, String genre) {
        mPath = path;
        mTrack = track;
        mArtist = artist;
        mAlbums = albums;
        mGenre = genre;
    }

    public String getTrack() {
        return mTrack;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbums() {
        return mAlbums;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getPath() {
        return mPath;
    }

    public static ArrayList<Prova> createContactsList(int numContacts) {
        ArrayList<Prova> contacts = new ArrayList<Prova>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Prova("Path" + i, "Track" + i, "Artista" + i, "Albums" + i, "Genre" + i));
        }

        return contacts;
    }
}
