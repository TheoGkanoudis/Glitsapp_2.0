package com.example.glitsapp20;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class TrailPopup extends MapsActivity{


    public static void showTrailPopup(int item, RelativeLayout mainLayout){
        Trail trail = trailList.get(item);
        renderPopup(trail, mainLayout);
    }

    public static void hideTrailPopup(RelativeLayout mainLayout){
        View myLayout = mainLayout.findViewById(R.id.trail_popup);
        RelativeLayout layout = myLayout.findViewById(R.id.trail_popup);
        layout.setVisibility(View.GONE);
    }

    private static void renderPopup(Trail trail, RelativeLayout mainLayout){

        View myLayout = mainLayout.findViewById(R.id.trail_popup);

        String name = trail.getName();
        String image = trail.getImage();
        int difficulty = trail.getDifficulty();
        String time = trail.getTime();

        boolean fav = trail.getFav();

        int resID = 0;

        TextView tvName = (TextView) myLayout.findViewById(R.id.trail_name);
        if(!name.equals("")){
            tvName.setText(name);
        }

        ImageView ivImage = (ImageView) myLayout.findViewById(R.id.trail_image);
        if(image!=null){
            resID = getResId(image+"_thumb", R.drawable.class);
            ivImage.setImageResource(resID);
        }

        TextView tvTime = (TextView) myLayout.findViewById(R.id.trail_time);
        if(time!=null){
            tvTime.setText(time);
        }

        ImageView ivDifficulty = (ImageView) myLayout.findViewById(R.id.trail_difficulty);
        ivDifficulty.setImageResource(difficulty);

        ImageView ivFav = (ImageView) myLayout.findViewById(R.id.trail_fav);
        if(image!=null && fav){
            resID = getResId("fav_filled", R.drawable.class);
            ivFav.setImageResource(resID);
        }

        RelativeLayout popup = myLayout.findViewById(R.id.trail_popup);
        popup.setVisibility(View.VISIBLE);
    }

}
