package com.example.glitsapp20;

import com.google.android.gms.maps.model.LatLng;

public class Poi {
    private  String mTitle;
    private  String mDescription;
    private  String mInfo;
    private String mImage;
    private LatLng mCoords;
    private int mTrail;
    private boolean mFav;

    public Poi(String title, String description, String info, String image, int trail, LatLng coords){
        mTitle = title;
        mDescription = description;
        mInfo = info;
        mImage = image;
        mTrail = trail;
        mFav = false;
        mCoords = coords;
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

    public LatLng getCoords() {
        return mCoords;
    }

}
