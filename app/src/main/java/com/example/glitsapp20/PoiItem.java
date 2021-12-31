package com.example.glitsapp20;

public class PoiItem {
    private int mImageResource;
    private int mFav;
    private int mMore;
    private String mTitle;
    private String mText;

    public PoiItem(int imageResource, int fav, int more, String title, String text){
        mImageResource = imageResource;
        mMore = more;
        mFav = fav;
        mTitle = title;
        mText = text;
    }

    public int getImageResource(){
        return mImageResource;
    }

    public int getFav() {
        return mFav;
    }

    public int getMore() {
        return mMore;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getText() {
        return mText;
    }
}
