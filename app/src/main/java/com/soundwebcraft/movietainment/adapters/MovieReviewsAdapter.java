package com.soundwebcraft.movietainment.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewsViewHolder> {
    @Override
    public MovieReviewsAdapter.ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ReviewsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{

        public ReviewsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
