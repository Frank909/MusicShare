package com.sms.musicshare.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class Album implements Parcelable {

    private String mName;
    private String mNTracks;
    private String mAlbumArt;
    private Artist mArtist;

    public Album(){
        super();
    }

    protected Album(Parcel in) {
        mName = in.readString();
        mNTracks = in.readString();
        mAlbumArt = in.readString();
        mArtist = in.readParcelable(Artist.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mNTracks);
        dest.writeString(mAlbumArt);
        dest.writeParcelable(mArtist, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public void setAlbumArt(String mAlbumArt) {
        this.mAlbumArt = mAlbumArt;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setNTracks(String mNTracks) {
        this.mNTracks = mNTracks;
    }

    public void setArtist(Artist mArtist) {
        this.mArtist = mArtist;
    }

    public String getAlbumArt() {
        return mAlbumArt;
    }

    public String getName() {
        return mName;
    }

    public String getNTracks() {
        return mNTracks;
    }

    public Artist getArtist() {
        return mArtist;
    }
}
