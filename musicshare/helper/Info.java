package com.sms.musicshare.helper;


import android.graphics.drawable.Drawable;

public class Info {
    private String title;
    private String subTitle;
    private Drawable image;
    private int id;

    public static final int ID_REVIEW = 0,
                            ID_FEEDBACK = 1,
                            ID_DEVELOPERS = 2,
                            ID_COPYRIGHT = 3;

    public Info(int id, String title, String subTitle, Drawable image){
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.image = image;
    }

    public void setTitle(String newTitle){
        this.title = newTitle;
    }

    public void setSubTitle(String newSubTitle){
        this.subTitle = newSubTitle;
    }

    public void setImage(Drawable newImage){
        this.image = newImage;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSubTitle(){
        return this.subTitle;
    }

    public Drawable getImage(){
        return this.image;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getID(){
        return this.id;
    }
}
