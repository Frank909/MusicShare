package com.sms.musicshare.helper;

import android.os.Parcel;
import android.os.Parcelable;


public class Artist implements Parcelable{

    private String name;
    private String nAlbums;
    private String nTracks;

    public Artist(){
        super();
    }

    protected Artist(Parcel in) {
        name = in.readString();
        nAlbums = in.readString();
        nTracks = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public void setCountAlbums(String nAlbums) {
        this.nAlbums = nAlbums;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountTracks(String nTracks) {
        this.nTracks = nTracks;
    }

    public String getCountAlbums() {
        return nAlbums;
    }

    public String getCountTracks() {
        return nTracks;
    }

    public String getName() {
        return name;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(nAlbums);
        dest.writeString(nTracks);
    }
}
