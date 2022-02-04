package com.example.glitsapp20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class rvTrailAdapter extends RecyclerView.Adapter<rvTrailAdapter.ViewHolder> {

        private List<String> mTrails;
    private LayoutInflater mInflater;
    private boolean[] mFavs;
    private int[] mResIDs;
    private ItemClickListener mClickListener;

    rvTrailAdapter(Context context, List<String> trails, int[] resIDs, @Nullable boolean[] favs) {
        this.mInflater = LayoutInflater.from(context);
        this.mTrails = trails;
        this.mResIDs = resIDs;
        this.mFavs = favs;
    }

    // inflates the row layout from xml when needed
    @Override
    public rvTrailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(mFavs==null)view = mInflater.inflate(R.layout.trail_item, parent, false);
        else view = mInflater.inflate(R.layout.fav_trail_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = mTrails.get(position);
        holder.trailText.setText(text);
        int image = mResIDs[position];
        holder.trailImage.setImageResource(image);
        if(mFavs!=null) {
            boolean fav = mFavs[position];
            holder.trailFav.setImageResource(fav?R.drawable.fav_filled:R.drawable.fav_empty);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTrails.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView trailText;
        ImageView trailImage;
        ImageView trailFav;

        ViewHolder(View itemView) {
            super(itemView);
            trailText = itemView.findViewById(R.id.trail_name);
            trailImage = itemView.findViewById(R.id.trail_image);
            if(mFavs!=null) {
                trailFav = itemView.findViewById(R.id.trail_fav);
                trailFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTrails.remove(getAdapterPosition());
                        for(int i = getAdapterPosition(); i<mTrails.size(); i++){
                            mFavs[i]=mFavs[i+1];
                            mResIDs[i]=mResIDs[i+1];
                        }
                        int[] newResIDs = new int[mTrails.size()];
                        boolean[] newFavs = new boolean[mTrails.size()];
                        for(int i = 0; i<mTrails.size(); i++){
                            newResIDs[i]=mResIDs[i];
                            newFavs[i]=mFavs[i];
                        }
                        mResIDs=newResIDs;
                        mFavs=newFavs;
                        AccountFragment.changeTrailFav(trailText.getText().toString(), getAdapterPosition());
                    }
                });
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener!=null) mClickListener.onTrailItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onTrailItemClick(View view, int position);
    }

}
