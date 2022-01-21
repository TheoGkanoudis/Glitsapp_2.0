package com.example.glitsapp20;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    public static void showPoiPopup(int item, RelativeLayout mainLayout){
        Poi poi = poiList.get(item);
        renderPopup(poi, mainLayout);
    }

    public static void hidePoiPopup(RelativeLayout mainLayout){
        View myLayout = mainLayout.findViewById(R.id.poi_popup);
        RelativeLayout layout = myLayout.findViewById(R.id.poi_popup);
        layout.setVisibility(View.GONE);
    }

    private static void renderPopup(Poi item, RelativeLayout mainLayout){

        View myLayout = mainLayout.findViewById(R.id.poi_popup);

        String title = item.getTitle();
        String description = item.getDescription();
        String image = item.getImage();
        boolean fav = item.getFav();

        TextView tvTitle = (TextView) myLayout.findViewById(R.id.poi_title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }

        TextView tvDescription = (TextView) myLayout.findViewById(R.id.poi_description);
        if(!description.equals("")){
            tvDescription.setText(description);
        }

        ImageView ivImage = (ImageView) myLayout.findViewById(R.id.poi_image);
        if(image!=null){
            int resID = getResId(image, R.drawable.class);
            ivImage.setImageResource(resID);
        }

        ImageView ivFav = (ImageView) myLayout.findViewById(R.id.poi_fav);
        if(image!=null && fav){
            int resID = getResId("fav_filled", R.drawable.class);
            ivFav.setImageResource(resID);
        }

        RelativeLayout popup = myLayout.findViewById(R.id.poi_popup);
        popup.setVisibility(View.VISIBLE);
    }

}
