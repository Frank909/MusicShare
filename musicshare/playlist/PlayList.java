package com.sms.musicshare.playlist;


public class PlayList{
    private int id;
    private String playlist_name;

    public PlayList(int id, String playlist_name) {
        this.id = id;
        this.playlist_name = playlist_name;
    }

    public String getPlayListName() {
        return this.playlist_name;
    }

    public int getPlayListId() {
        return this.id;
    }

    public void setPlaylist_name(String string){ this.playlist_name = string;}
}