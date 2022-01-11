package com.example.glitsapp20;

import android.graphics.drawable.Drawable;

public class PoiItem {
    private static String mTitle;
    private static String mDescription;
    private static String mInfo;
    private static Drawable mImage;
    private static boolean mFav;
    private static int mId;

    public PoiItem(String title, String description, String info, Drawable image, boolean fav, int id){
        mTitle = title;
        mDescription = description;
        mInfo = info;
        mImage = image;
        mFav = fav;
        mId = id;
    }

    public static String getTitle() {
        return mTitle;
    }

    public static String getDescription() {
        return mDescription;
    }

    public static String getInfo() {
        return mInfo;
    }

    public static Drawable getImage(){
        return mImage;
    }

    public static boolean getFav() {
        return mFav;
    }

}
