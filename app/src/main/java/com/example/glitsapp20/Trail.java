package com.example.glitsapp20;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;

public class Trail {
    private int mId;
    private String mName;
    private String mInfo;
    private char[] mRocks;
    private String mImage;
    private int mDifficulty;
    private String mTime;
    private LatLng[] mCoords;
    private boolean mFav;

    public Trail(String name, String info, String image, int difficulty, int time, char[] rocks, LatLng[] coords, int id){
        mName = name;
        mInfo = info;
        mImage = image;
        mFav = false;
        mCoords = coords;
        mRocks = rocks;
        mId = id;

        switch (difficulty){
            case 0:
                difficulty = MapsActivity.getResId("diff0", R.drawable.class);
                break;
            case 1:
                difficulty = MapsActivity.getResId("diff1", R.drawable.class);
                break;
            case 2:
                difficulty = MapsActivity.getResId("diff2", R.drawable.class);
                break;
            case 3:
                difficulty = MapsActivity.getResId("diff3", R.drawable.class);
                break;
        }
        mDifficulty = difficulty;


        int m = time%100;
        int h = (time-m)/100;
        String sTime = "";
        if(h!=0){
            sTime += String.valueOf(h);
            if(h==1){sTime+=" ώρα";}
            else{sTime+=" ώρες";}
            if(m!=0){
                sTime += ", ";
            }
        }
        if(m!=0){
            sTime+= m+" λεπτά";
        }
        mTime = sTime;
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

    public String getTime() {
        return mTime;
    }

    public LatLng[] getCoords() {
        return mCoords;
    }

    public  boolean getFav() {
        return mFav;
    }

    public int getId() {
        return  mId;
    }

    public void changeFav(){
        mFav = !mFav;
    }

}
