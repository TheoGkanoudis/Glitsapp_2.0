package com.example.glitsapp20;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrailInfoActivity extends Activity{

    ConstraintLayout myLayout;
    rvRockAdapter rockAdapter;
    rvPoiAdapter poiAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trail_info);
        myLayout = (ConstraintLayout) findViewById(R.id.trail_info_activity);
        initTrailInfo(MapsActivity.getTrail(), myLayout);
        initRVs(myLayout, this, MapsActivity.getTrail());
    }

    public void initTrailInfo(Trail trail, View view) {
        String name = trail.getName();
        String image = trail.getImage();
        String info = trail.getInfo();
        String time = trail.getTime();
        int difficulty = trail.getDifficulty();



        int resID = 0;

        TextView tvName = view.findViewById(R.id.trail_name);
        if(name!=null){
            tvName.setText(name);
        }

        ImageView ivImage = view.findViewById(R.id.trail_image);
        if(image!=null){
            resID = MapsActivity.getResId(image, R.drawable.class);
            ivImage.setImageResource(resID);
        }

        TextView tvTime = view.findViewById(R.id.trail_time);
        if(time!=null){
            tvTime.setText(time);
        }

        TextView tvInfo = view.findViewById(R.id.trail_info);
        if(info!=null){
            tvInfo.setText(info);
        }

        ImageView ivDifficulty = view.findViewById(R.id.trail_difficulty);
        ivDifficulty.setImageResource(difficulty);

        ImageView ivBack = view.findViewById(R.id.back_button);
        ivBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initRVs(View view, Context context, Trail trail){

        char[] rocks = trail.getRocks();
        int[] rockImages = new int[rocks.length];
        ArrayList<String> mRocks = new ArrayList<>();
        for(int i =0; i<rocks.length; i++){
            if(rocks[i]=='g'){mRocks.add("Μετα-Γάββρος"); rockImages[i]=MapsActivity.getResId("metababbro", R.drawable.class);}
            else if(rocks[i]=='m'){mRocks.add("Μάρμαρο"); rockImages[i]=MapsActivity.getResId("marble", R.drawable.class);}
            else if(rocks[i]=='e'){mRocks.add("Εκλογίτης"); rockImages[i]=MapsActivity.getResId("eclogite", R.drawable.class);}
            else if(rocks[i]=='s'){mRocks.add("Σχιστόλιθος"); rockImages[i]=MapsActivity.getResId("schist", R.drawable.class);}
        }

        RecyclerView rocksRV = view.findViewById(R.id.rv_rocks);
        rocksRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rockAdapter = new rvRockAdapter(context, mRocks, rockImages);
        rocksRV.setAdapter(rockAdapter);

        ArrayList<String> poiTitles = MapsActivity.getPoiTitles(trail);
        int[] poiImages = new int[poiTitles.size()];
        ArrayList<String> poiInfo = new ArrayList<>();
        boolean[] poiFav = new boolean[poiTitles.size()];

        int counter = 0;
        for(int i = 0; i<MapsActivity.poiList.size(); i++){
            if(MapsActivity.poiList.get(i).getTrail()==trail.getId()){
                poiImages[counter] = MapsActivity.getResId(MapsActivity.poiList.get(i).getImage(), R.drawable.class);
                poiInfo.add(MapsActivity.poiList.get(i).getInfo());
                poiFav[counter] = MapsActivity.poiList.get(i).getFav();
                counter++;
            }
        }

        RecyclerView poiRV = view.findViewById(R.id.rv_pois);
        poiRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        poiAdapter = new rvPoiAdapter(context, poiTitles, poiImages, poiInfo, poiFav);
        poiRV.setAdapter(poiAdapter);
    }

}


