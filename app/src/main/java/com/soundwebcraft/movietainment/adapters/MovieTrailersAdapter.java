package com.soundwebcraft.movietainment.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.soundwebcraft.movietainment.R;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.TMDB;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerViewHolder> {
    private Context mContext;
    private List<Movie> mTrailers;

    public MovieTrailersAdapter(Context context, List<Movie> trailers) {
        this.mContext = context;
        mTrailers  = trailers;
    }

    @Override
    public MovieTrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_movie_trailer, parent, false);

        return (new TrailerViewHolder(view));
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapter.TrailerViewHolder holder, int position) {
        Movie movie = mTrailers.get(position);
        ImageView trailerIv = holder.trailerPoster;
        Picasso.with(mContext)
                .load(movie.getTrailerThumbnail())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_preview)
                .into(trailerIv);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        // get reference to views
        @BindView(R.id.movie_trailer_poster)
        ImageView trailerPoster;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedView = getAdapterPosition();
                    Movie movie = mTrailers.get(clickedView);
                    String videoKey = movie.getYoutubeKey();
                    // get the trailer Uri
                    Uri trailerUri = Uri.parse(TMDB.buildTrailerURL(videoKey));
                    Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
                    // start activity only if the activity resolves successfully
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
