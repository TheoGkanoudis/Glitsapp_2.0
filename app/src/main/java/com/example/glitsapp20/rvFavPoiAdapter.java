package com.example.glitsapp20;

import static android.content.Context.ACTIVITY_SERVICE;


import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class rvFavPoiAdapter extends RecyclerView.Adapter<rvFavPoiAdapter.ViewHolder> {

    private int[] mResIDs;
    private List<String> mTitles;
    private List<String> mInfo;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean[] mFav;


    // data is passed into the constructor
    rvFavPoiAdapter(Context context, List<String> titles, int[] resIDs, List<String> info, boolean[] fav) {
        this.mInflater = LayoutInflater.from(context);
        this.mTitles = titles;
        this.mResIDs = resIDs;
        this.mFav = fav;
        this.mInfo = info;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.poi_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int image = mResIDs[position];
        holder.poiImage.setImageResource(image);
        String text = mTitles.get(position);
        holder.poiTitle.setText(text);
        String info = mInfo.get(position);
        holder.poiInfo.setText(info);
        boolean fav = mFav[position];
        if(fav)holder.poiFav.setImageResource(R.drawable.fav_filled);
        else holder.poiFav.setImageResource(R.drawable.fav_empty);
            }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView poiTitle;
        ImageView poiImage;
        TextView poiInfo;
        ImageView poiFav;



        ViewHolder(View itemView) {
            super(itemView);
            poiTitle = itemView.findViewById(R.id.poi_title);
            poiImage = itemView.findViewById(R.id.poi_image);
            poiInfo = itemView.findViewById(R.id.poi_info);
            poiFav = itemView.findViewById(R.id.poi_fav);

            poiFav.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    mTitles.remove(getAbsoluteAdapterPosition());
                    mInfo.remove(getAbsoluteAdapterPosition());
                    for(int i = getAbsoluteAdapterPosition(); i<mTitles.size(); i++){
                        mFav[i]=mFav[i+1];
                        mResIDs[i]=mResIDs[i+1];
                    }
                    int[] newResIDs = new int[mTitles.size()];
                    boolean[] newFav = new boolean[mTitles.size()];
                    for(int i = 0; i <mTitles.size(); i++){
                        newResIDs[i] = mResIDs[i];
                        newFav[i] = mFav[i];
                    }
                    mResIDs = newResIDs;
                    mFav = newFav;
                    AccountFragment.changeFavPoiFav(poiTitle.getText().toString(), getAbsoluteAdapterPosition());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener!=null) mClickListener.onPoiItemClick(view, getAbsoluteAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onPoiItemClick(View view, int position);
    }



}

