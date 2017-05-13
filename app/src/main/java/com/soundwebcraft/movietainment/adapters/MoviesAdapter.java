package com.soundwebcraft.movietainment.adapters;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.soundwebcraft.movietainment.MovieDetailActivity;
import com.soundwebcraft.movietainment.MoviesActivity;
import com.soundwebcraft.movietainment.R;
import com.soundwebcraft.movietainment.networking.models.TMDb;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    public static final String TAG = MoviesAdapter.class.getSimpleName();

    private final List<TMDb> mResults;
    private final Context mContext;

    public MoviesAdapter(List<TMDb> results, Context context) {
        mResults = results;
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);

        return new MovieViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        TMDb result = mResults.get(position);

        TextView movieTitle = holder.titleTextView,
                movieIDTextView = holder.movieIDTextView,
                movieOverview = holder.overview,
                movieRatings = holder.voteAverage,
                movieReleasedDate = holder.releasedDate;
        movieTitle.setText(result.getOriginalTitle());

        movieIDTextView.setText(String.valueOf(result.getId()));
        movieOverview.setText(result.getOverview());
        movieRatings.setText(String.valueOf(result.getVoteAverage()));
        movieReleasedDate.setText(result.getReleaseDate());

        ImageView imageView = holder.posterImageView;
        // set the movie poster content description
        String movieContentDescription = result.getOriginalTitle();
        imageView.setContentDescription(movieContentDescription);
        // load the movie poster into the imageview
        Picasso.with(mContext)
                .load(result.getPoster())
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_preview)
                .into(imageView);

        // add animations to item {row}
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void updateViews(List<TMDb> results) {
        mResults.addAll(results);
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        // hold reference to each views in row for caching
        @BindView(R.id.tv_item_movie)
        TextView titleTextView;
        @BindView(R.id.movie_poster)
        ImageView posterImageView;
        @BindView(R.id.movie_id)
        TextView movieIDTextView;
        @BindView(R.id.movie_overview)
        TextView overview;
        @BindView(R.id.movie_vote_average)
        TextView voteAverage;
        @BindView(R.id.movie_vote_count)
        TextView voteCount;
        @BindView(R.id.movie_released_date)
        TextView releasedDate;

        public MovieViewHolder(View itemView) {
            super(itemView);
            // simplify viewholder findViewById using butterknife
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    TMDb clickedMovie = mResults.get(position);
                    int movieId = clickedMovie.getId();
                    String highResPoster = clickedMovie.getPoster(),
                            movieTitle = clickedMovie.getOriginalTitle(),
                            movieOv = clickedMovie.getOverview(),
                            movieRD = clickedMovie.getReleaseDate();

                    Intent intent = new Intent(mContext, MovieDetailActivity.class);

                    TMDb movie = new TMDb(
                            movieTitle,
                            mResults.get(position).getPosterPath(),
                            movieId,
                            movieOv,
                            mResults.get(position).getVoteAverage(),
                            mResults.get(position).getVoteCount(),
                            movieRD
                    );
                    intent.putExtra(Intent.EXTRA_TEXT, Parcels.wrap(movie));

                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                            (MoviesActivity) mContext
                            ).toBundle();
                    mContext.startActivity(intent, bundle);
                }
            });
        }
    }
}
