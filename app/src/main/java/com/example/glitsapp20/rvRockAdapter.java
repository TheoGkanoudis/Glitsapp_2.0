package com.example.glitsapp20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class rvRockAdapter extends RecyclerView.Adapter<rvRockAdapter.ViewHolder> {

    private int[] mResIDs;
    private List<String> mRocks;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    rvRockAdapter(Context context, List<String> rocks, int[] resIDs) {
        this.mInflater = LayoutInflater.from(context);
        this.mRocks = rocks;
        this.mResIDs = resIDs;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rock_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int image = mResIDs[position];
        holder.rockImage.setImageResource(image);
        String text = mRocks.get(position);
        holder.rockText.setText(text);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mRocks.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rockText;
        ImageView rockImage;

        ViewHolder(View itemView) {
            super(itemView);
            rockText = itemView.findViewById(R.id.rock_text);
            rockImage = itemView.findViewById(R.id.rock_image);
        }

    }

}
