package com.sms.musicshare.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class InfoTrack implements Parcelable, Serializable {

    private String mPath;
    private String mTitle;
    private Artist mArtist;
    private Album mAlbum;
    private Genre mGenre;
    private long mDuration;
    private boolean mFavorite, mShared;

    /**
     * Constructors
     * **/

    public InfoTrack(){
        super();
    }

    public InfoTrack(String path, String title, Artist artist, Album album, Genre genre, boolean favorite, boolean shared, long duration) {
        super();
        this.mPath = path;
        this.mTitle = title;
        this.mArtist = artist;
        this.mAlbum = album;
        this.mGenre = genre;
        this.mFavorite = favorite;
        this.mShared = shared;
        this.mDuration = duration;
    }


    protected InfoTrack(Parcel in) {
        mPath = in.readString();
        mTitle = in.readString();
        mArtist = in.readParcelable(Artist.class.getClassLoader());
        mAlbum = in.readParcelable(Album.class.getClassLoader());
        mGenre = in.readParcelable(Genre.class.getClassLoader());
        mDuration = in.readLong();
        mFavorite = in.readByte() != 0;
        mShared = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mTitle);
        dest.writeParcelable(mArtist, flags);
        dest.writeParcelable(mAlbum, flags);
        dest.writeParcelable(mGenre, flags);
        dest.writeLong(mDuration);
        dest.writeByte((byte) (mFavorite ? 1 : 0));
        dest.writeByte((byte) (mShared ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InfoTrack> CREATOR = new Creator<InfoTrack>() {
        @Override
        public InfoTrack createFromParcel(Parcel in) {
            return new InfoTrack(in);
        }

        @Override
        public InfoTrack[] newArray(int size) {
            return new InfoTrack[size];
        }
    };

    /**
     * Setters
     * **/

    public void setAlbum(Album mAlbum) {
        this.mAlbum = mAlbum;
    }

    public void setArtist(Artist mArtist) {
        this.mArtist = mArtist;
    }

    public void setGenre(Genre mGenre) {
        this.mGenre = mGenre;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }

    public void setShared(boolean mShared){ this.mShared = mShared; }

    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    /**
     * Getters
     * **/

    public String getTitle() { return mTitle; }

    public Artist getArtist() {
        return mArtist;
    }

    public Album getAlbum() {
        return mAlbum;
    }

    public Genre getGenre() {
        return mGenre;
    }

    public String getPath() {
        return mPath;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public long getDuration() {return mDuration; }

    public boolean isShared() { return mShared; }

}
