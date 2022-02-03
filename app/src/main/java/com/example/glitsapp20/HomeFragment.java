package com.example.glitsapp20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements rvTrailAdapter.ItemClickListener{

    rvTrailAdapter trailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ConstraintLayout myLayout = (ConstraintLayout) view.findViewById(R.id.home_fragment);
        initRVs(myLayout, getContext());
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {

        MapsActivity.trailToPass = MapsActivity.trailList.get(position).getId();
        Intent i = new Intent(getContext(), TrailInfoActivity.class);
        startActivity(i);
    }

    public void initRVs(View view, Context context){

        RecyclerView trailsRV = view.findViewById(R.id.rv_trails);
        trailsRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<String> trailNames = new ArrayList<String>();
        int[] trailImages = new int[MapsActivity.trailList.size()];
        for(int i = 0; i < MapsActivity.trailList.size(); i++){
            trailNames.add(MapsActivity.trailList.get(i).getName());
            trailImages[i] = MapsActivity.getResId(MapsActivity.trailList.get(i).getImage(),R.drawable.class);
        }
        trailAdapter = new rvTrailAdapter(context, trailNames, trailImages);
        trailAdapter.setClickListener(this);
        trailsRV.setAdapter(trailAdapter);
    }

}
