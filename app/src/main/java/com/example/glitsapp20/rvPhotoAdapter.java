package com.example.glitsapp20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class rvPhotoAdapter extends RecyclerView.Adapter<rvPhotoAdapter.ViewHolder> {

    private int[] mResIDs;
    private LayoutInflater mInflater;

    rvPhotoAdapter(Context context, int[] resIDs){
        this.mInflater = LayoutInflater.from(context);
        this.mResIDs = resIDs;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int image = mResIDs[position];
        holder.image.setImageResource(image);
    }

    @Override
    public int getItemCount() {
        return mResIDs.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo);
        }

    }
}
