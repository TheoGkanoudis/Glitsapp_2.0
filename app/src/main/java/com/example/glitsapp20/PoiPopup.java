package com.example.glitsapp20;

import static com.example.glitsapp20.MapsActivity.poiItems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PoiPopup {

    private static View mWindow;



    private static void renderWindow(PoiItem item, View view, Context mContext){


        mWindow = LayoutInflater.from(mContext).inflate(R.layout.poi_item, null);

        String title = item.getTitle();
        String description = item.getDescription();
        String info = item.getInfo();
        Drawable image = item.getImage();
        boolean fav = item.getFav();

        TextView tvTitle = (TextView) view.findViewById(R.id.poi_title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }

        TextView tvDescription = (TextView) view.findViewById(R.id.poi_description);
        if(!description.equals("")){
            tvDescription.setText(description);
        }

        TextView tvInfo = (TextView) view.findViewById(R.id.poi_info);
        if(!info.equals("")){
            tvInfo.setText(info);
        }

        ImageView tvImage = (ImageView) view.findViewById(R.id.poi_image);
        if(image!=null){
            tvImage.setBackground(image);
        }

       //TODO - change heart if fav

    }

    public static void showPoiInfo(Marker marker, Context mContext){
        renderWindow(poiItems.get(Integer.valueOf(marker.getTitle())),mWindow, mContext);
    }



}
