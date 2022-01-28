package com.example.glitsapp20;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import static android.view.View.VISIBLE;

public class PoiPopup extends MapsActivity{

    public static void showPoiPopup(int item, RelativeLayout mainLayout){
        Poi poi = poiList.get(item);
        initPoiPopup(poi, mainLayout);
    }

    public static void hidePoiPopup(RelativeLayout mainLayout){
        View myLayout = mainLayout.findViewById(R.id.poi_popup);
        RelativeLayout layout = myLayout.findViewById(R.id.poi_popup);
        layout.setVisibility(View.GONE);
    }

    private static void initPoiPopup(Poi item, RelativeLayout mainLayout){

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

        int resID;
        ImageView ivFav = (ImageView) myLayout.findViewById(R.id.poi_fav);
        if(fav){
            resID = getResId("fav_filled", R.drawable.class);
        }
        else{
            resID = getResId("fav_empty", R.drawable.class);
        }
        ivFav.setImageResource(resID);

        ivFav.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                item.changeFav();
                initPoiPopup(item, mainLayout);
            }
        });

        RelativeLayout popup = myLayout.findViewById(R.id.poi_popup);
        popup.setVisibility(VISIBLE);
    }

    public static void refreshPopup(RelativeLayout mainLayout){
        View myLayout = mainLayout.findViewById(R.id.poi_popup);
        RelativeLayout popup = myLayout.findViewById(R.id.poi_popup);
        if(popup.getVisibility()==VISIBLE) {
            Poi poi = MapsActivity.getPoi();
            initPoiPopup(poi, mainLayout);
        }
    }
}
