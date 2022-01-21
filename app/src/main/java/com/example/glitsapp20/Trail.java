package com.example.glitsapp20;

import com.google.android.gms.maps.model.LatLng;

public class Trail {
    private String mName;
    private String mInfo;
    private char[] mRocks;
    private String mImage;
    private LatLng[] mCoords;
    private boolean mFav;

    public Trail(String name, String info, char[] rocks, String image, LatLng[] coords){
        mName = name;
        mInfo = info;
        mRocks = rocks;
        mImage = image;
        mFav = false;
        mCoords = coords;
    }

    public String getTitle() {
        return mName;
    }

    public  String getInfo() {
        return mInfo;
    }

    public  char[] getRocks() {
        return mRocks;
    }

    public  String getImage(){
        return mImage;
    }

    public  boolean getFav() {
        return mFav;
    }

    public LatLng[] getCoords() {
        return mCoords;
    }

}
