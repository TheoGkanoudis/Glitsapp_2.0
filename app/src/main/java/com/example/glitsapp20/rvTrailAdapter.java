package com.example.glitsapp20;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class rvTrailAdapter extends RecyclerView.Adapter<rvTrailAdapter.ViewHolder> {

    private List<String> mTrails;
    private LayoutInflater mInflater;
    private int[] mResIDs;
    private ItemClickListener mClickListener;

    rvTrailAdapter(Context context, List<String> trails, int[] resIDs) {
        this.mInflater = LayoutInflater.from(context);
        this.mTrails = trails;
        this.mResIDs = resIDs;
    }

    // inflates the row layout from xml when needed
    @Override
    public rvTrailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trail_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = mTrails.get(position);
        holder.trailText.setText(text);
        int image = mResIDs[position];
        holder.trailImage.setImageResource(image);
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

        ViewHolder(View itemView) {
            super(itemView);
            trailText = itemView.findViewById(R.id.trail_name);
            trailImage = itemView.findViewById(R.id.trail_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mClickListener!=null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

}
