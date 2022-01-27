package com.example.glitsapp20;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CpuUsageInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PoiInfoActivity extends Activity {

    ConstraintLayout myLayout;
    rvPhotoAdapter photoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_info);
        myLayout = (ConstraintLayout) findViewById(R.id.poi_info_activity);
        initPoiInfo(MapsActivity.getPoi(), myLayout);
        initRVs(myLayout, this, MapsActivity.getPoi());
    }

    public void initPoiInfo(Poi poi, View view){
        String title = poi.getTitle();
        String info = poi.getInfo();

        TextView tvTitle = view.findViewById(R.id.poi_title);
        if(title!=null){
            tvTitle.setText(title);
        }

        TextView tvInfo = view.findViewById(R.id.poi_info);
        if(info!=null){
            tvInfo.setText(info);
        }

        ImageView ivBack = view.findViewById(R.id.back_button);
        ivBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {finish();}
        });
    }

    public void initRVs(View view, Context context, Poi poi){
        //for the photos recyclerView
        int[] mPhotos = new int[poi.getImages().size()];

        for(int i=0; i<poi.getImages().size(); i++){
            mPhotos[i]=poi.getImages().get(i);
        }

        RecyclerView photosRV = view.findViewById(R.id.rv_photos);
        photosRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        photoAdapter = new rvPhotoAdapter(context, mPhotos);
        photosRV.setAdapter(photoAdapter);
    }
}
