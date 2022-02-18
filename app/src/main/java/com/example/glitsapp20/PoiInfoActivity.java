package com.example.glitsapp20;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    MediaPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_info);
        myLayout = (ConstraintLayout) findViewById(R.id.poi_info_activity);
        initPoiInfo(MapsActivity.getPoi(), myLayout);
        initRVs(myLayout, this, MapsActivity.getPoi());
        MapsActivity.checkVisited();
    }



    public void initPoiInfo(Poi poi, View view){
        String title = poi.getTitle();
        String info = poi.getInfo();
        boolean fav = poi.getFav();
        String audio = poi.getImage();
        int rawID = MapsActivity.getResId(audio, R.raw.class);

        mPlayer = MediaPlayer.create(this, rawID);

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
            @Override
            public void onClick(View v) {finish();}
        });

        ImageView ivPlayPause = view.findViewById(R.id.play_pause);
        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPlayer.isPlaying()){
                    ivPlayPause.setImageResource(MapsActivity.getResId("play", R.drawable.class));
                    mPlayer.pause();
                }
                else{
                    ivPlayPause.setImageResource(MapsActivity.getResId("pause", R.drawable.class));
                    mPlayer.start();
                }
            }
        });

        ImageView ivAudio = view.findViewById(R.id.speaker);
        ivAudio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!mPlayer.isPlaying()&&ivPlayPause.getVisibility()==View.GONE){
                    ivAudio.setImageResource(MapsActivity.getResId("stop", R.drawable.class));
                    mPlayer.start();
                    ivPlayPause.setVisibility(View.VISIBLE);
                }
                else{
                    ivAudio.setImageResource(MapsActivity.getResId("speaker", R.drawable.class));
                    mPlayer.pause();
                    mPlayer.seekTo(0);
                    ivPlayPause.setVisibility(View.GONE);
                }
            }
        });

        int resID;
        ImageView ivFav = (ImageView) myLayout.findViewById(R.id.poi_fav);
        if(fav){
            resID = MapsActivity.getResId("fav_filled", R.drawable.class);
        }
        else{
            resID = MapsActivity.getResId("fav_empty", R.drawable.class);
        }
        ivFav.setImageResource(resID);

        ivFav.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                poi.changeFav();
                initPoiInfo(poi, view);
            }
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
