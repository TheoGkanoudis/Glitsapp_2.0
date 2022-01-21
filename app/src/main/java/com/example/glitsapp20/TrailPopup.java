package com.example.glitsapp20;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class TrailPopup extends MapsActivity{

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

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
        int time = trail.getTime();
        boolean fav = trail.getFav();

        int resID = 0;

        TextView tvName = (TextView) myLayout.findViewById(R.id.trail_name);
        if(!name.equals("")){
            tvName.setText(name);
        }

        ImageView ivImage = (ImageView) myLayout.findViewById(R.id.trail_image);
        if(image!=null){
            resID = getResId(image, R.drawable.class);
            ivImage.setImageResource(resID);
        }

        TextView tvTime = (TextView) myLayout.findViewById(R.id.trail_time);
        int m = time%100;
        int h = (time-m)/100;
        String sTime = String.valueOf(h);
        if(h==1){sTime+=" ώρα";}
        else{sTime+=" ώρες";}
        if(m!=0){
            sTime+=", "+m+" λεπτά";
        }
        tvTime.setText(sTime);

        ImageView ivDifficulty = (ImageView) myLayout.findViewById(R.id.trail_difficulty);
        switch (difficulty){
            case 0:
                resID = getResId("diff0", R.drawable.class);
                break;
            case 1:
                resID = getResId("diff1", R.drawable.class);
                break;
            case 2:
                resID = getResId("diff2", R.drawable.class);
                break;
            case 3:
                resID = getResId("diff3", R.drawable.class);
                break;
        }
        ivDifficulty.setImageResource(resID);

        ImageView ivFav = (ImageView) myLayout.findViewById(R.id.trail_fav);
        if(image!=null && fav){
            resID = getResId("fav_filled", R.drawable.class);
            ivFav.setImageResource(resID);
        }

        RelativeLayout popup = myLayout.findViewById(R.id.trail_popup);
        popup.setVisibility(View.VISIBLE);
    }

}
