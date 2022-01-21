package com.example.glitsapp20;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

public class Trail {
    private String mName;
    private String mInfo;
    private char[] mRocks;
    private String mImage;
    private int mDifficulty;
    private int mTime;
    private LatLng[] mCoords;
    private boolean mFav;

    public Trail(String name, String info, String image, int difficulty, int time, char[] rocks, LatLng[] coords){
        mName = name;
        mInfo = info;
        mRocks = rocks;
        mImage = image;
        mDifficulty = difficulty;
        mTime = time;
        mFav = false;
        mCoords = coords;
    }

    public String getName() {
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

    public int getDifficulty() {
        return mDifficulty;
    }

    public int getTime() {
        return mTime;
    }

    public LatLng[] getCoords() {
        return mCoords;
    }

    public  boolean getFav() {
        return mFav;
    }

}
