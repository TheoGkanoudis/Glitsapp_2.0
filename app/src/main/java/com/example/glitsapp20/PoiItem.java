package com.example.glitsapp20;

import android.graphics.drawable.Drawable;

public class PoiItem {
    private  String mTitle;
    private  String mDescription;
    private  String mInfo;
    private String mImage;
    private int mTrail;
    private boolean mFav;

    public PoiItem(String title, String description, String info, String image, int trail){
        mTitle = title;
        mDescription = description;
        mInfo = info;
        mImage = image;
        mTrail = trail;
        mFav = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public  String getDescription() {
        return mDescription;
    }

    public  String getInfo() {
        return mInfo;
    }

    public  String getImage(){
        return mImage;
    }

    public int getTrail(){
        return  mTrail;
    }

    public  boolean getFav() {
        return mFav;
    }

}
