package com.soundwebcraft.movietainment.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerViewHolder> {

    @Override
    public MovieTrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapter.TrailerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        public TrailerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
