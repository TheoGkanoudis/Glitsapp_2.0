package com.example.glitsapp20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AccountFragment extends Fragment implements rvTrailAdapter.ItemClickListener, rvFavPoiAdapter.ItemClickListener {

    static rvTrailAdapter trailAdapter;
    static ConstraintLayout myLayout;
    static rvFavPoiAdapter favPoiAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);
        myLayout = (ConstraintLayout) view.findViewById(R.id.account_fragment);
        initRVs(myLayout, getContext());
        return view;
    }

    @Override
    public void onTrailItemClick(View view, int position) {
        TextView tvTrail = (TextView) view.findViewById(R.id.trail_name);
        String trail = tvTrail.getText().toString();
        for (Trail t : MapsActivity.trailList) {
            if(t.getName()==trail) MapsActivity.trailToPass = t.getId();
        }
        Intent i = new Intent(getContext(), TrailInfoActivity.class);
        startActivity(i);
    }

    public void onPoiItemClick(View view, int position) {
        TextView tv = view.findViewById(R.id.poi_title);
        String name = tv.getText().toString();

        for (int i = 0; i < MapsActivity.poiList.size(); i++) {
            if (MapsActivity.poiList.get(i).getTitle() == name)
                MapsActivity.poiToPass = MapsActivity.poiList.get(i).getId();
        }

        Intent pi = new Intent(getContext(), PoiInfoActivity.class);
        startActivity(pi);
    }

    @Override
    public void onResume() {
        initRVs(myLayout, getContext());
        super.onResume();
    }

    public void initRVs(View view, Context context) {

        //for the poi rv
        ArrayList<String> poiTitles = new ArrayList<>();
        for (Poi poi : MapsActivity.poiList) {
            if(poi.getFav()) poiTitles.add(poi.getTitle());
        }
        int[] poiImages = new int[poiTitles.size()];
        ArrayList<String> poiInfo = new ArrayList<>();
        boolean[] poiFav = new boolean[poiTitles.size()];

        int counter = 0;
        for (Poi poi : MapsActivity.poiList) {
            if (poi.getFav()){
                poiImages[counter] = MapsActivity.getResId(poi.getImage(), R.drawable.class);
                poiInfo.add(poi.getInfo());
                poiFav[counter] = poi.getFav();
                counter++;
            }
        }
        if(counter!=0) view.findViewById(R.id.pois_empty).setVisibility(View.GONE);
        else view.findViewById(R.id.pois_empty).setVisibility(View.VISIBLE);

        RecyclerView poiRV = view.findViewById(R.id.rv_pois);
        poiRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        favPoiAdapter = new rvFavPoiAdapter(context, poiTitles, poiImages, poiInfo, poiFav);
        favPoiAdapter.setClickListener(this);
        poiRV.setAdapter(favPoiAdapter);

        //for the trail rv
        ArrayList<String> trailNames = new ArrayList<>();
        for(Trail trail : MapsActivity.trailList){
            if(trail.getFav())trailNames.add(trail.getName());
        }
        int[] trailImages = new int[trailNames.size()];
        boolean[] trailFav = new boolean[trailNames.size()];

        counter = 0;

        for (Trail trail : MapsActivity.trailList) {
            if(trail.getFav()){
                trailImages[counter] = MapsActivity.getResId(trail.getImage(), R.drawable.class);
                trailFav[counter] = trail.getFav();
                counter ++;
            }
        }
        if(counter!=0) view.findViewById(R.id.trails_empty).setVisibility(View.GONE);
        else view.findViewById(R.id.trails_empty).setVisibility(View.VISIBLE);

        RecyclerView trailsRV = view.findViewById(R.id.rv_trails);
        trailsRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        trailAdapter = new rvTrailAdapter(context, trailNames, trailImages, trailFav);
        trailAdapter.setClickListener(this);
        trailsRV.setAdapter(trailAdapter);
    }

    public static void changeTrailFav(String trailName, int position) {
        boolean empty = true;
        for (Trail trail : MapsActivity.trailList) {
            if (trail.getName().equals(trailName)) {
                if(trail.getFav())trailAdapter.notifyItemRemoved(position);
                else trailAdapter.notifyItemChanged(position);
                trail.changeFav();
            }
            else if(trail.getFav())empty=false;
        }
        if(empty)myLayout.findViewById(R.id.trails_empty).setVisibility(View.VISIBLE);
    }

    public static void changeFavPoiFav(String poiTitle, int position){
        boolean empty = true;
        for (Poi poi : MapsActivity.poiList) {
            if(poi.getTitle()==poiTitle){
                favPoiAdapter.notifyItemRemoved(position);
                poi.changeFav();
            }
            else if(poi.getFav()) empty = false;

        }
        if(empty)myLayout.findViewById(R.id.pois_empty).setVisibility(View.VISIBLE);
    }

}