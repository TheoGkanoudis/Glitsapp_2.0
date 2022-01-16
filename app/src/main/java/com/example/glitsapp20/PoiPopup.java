package com.example.glitsapp20;

import static com.example.glitsapp20.MapsActivity.poiItems;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Field;

public class PoiPopup extends MapsActivity{

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void showPoiInfo(int marker, RelativeLayout mainLayout){
        PoiItem item = poiItems.get(marker);
        renderWindow(item, mainLayout);
    }

    public static void hidePoiInfo(RelativeLayout mainLayout){
        View myLayout = mainLayout.findViewById(R.id.poi_popup);
        RelativeLayout layout = myLayout.findViewById(R.id.poi_popup);
        layout.setVisibility(View.GONE);
    }


    private static void renderWindow(PoiItem item, RelativeLayout mainLayout){

        //View myLayout = inflater.inflate(R.layout.poi_item, mainLayout, false);
        View myLayout = mainLayout.findViewById(R.id.poi_popup);

        String title = item.getTitle();
        String description = item.getDescription();
        String info = item.getInfo();
        String image = item.getImage();
        boolean fav = item.getFav();
        int trail = item.getTrail();

        TextView tvTitle = (TextView) myLayout.findViewById(R.id.poi_title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }

        TextView tvDescription = (TextView) myLayout.findViewById(R.id.poi_description);
        if(!description.equals("")){
            tvDescription.setText(description);
        }

        TextView tvInfo = (TextView) myLayout.findViewById(R.id.poi_info);
        if(!info.equals("")){
            tvInfo.setText(info);
        }

        ImageView tvImage = (ImageView) myLayout.findViewById(R.id.poi_image);
        if(image!=null){
            int resID = getResId(image, R.drawable.class);
            tvImage.setImageResource(resID);
        }

        RelativeLayout layout = myLayout.findViewById(R.id.poi_popup);
        layout.setVisibility(View.VISIBLE);
    }




}
