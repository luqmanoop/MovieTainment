package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.TMDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final String IMDB_MOVIE_PREVIEW = "http://www.imdb.com/title/";
    private static final String HIGH_RES_POSTER = "HIGH_RES_POSTER";
    private static final String MOVIE_TITLE = "MOVIE_TITLE";
    private static final String MOVIE_ID = "MOVIE_ID";

    TextView ovTitle;
    TextView overview;
    TextView released;
    TextView ratings;
    ImageView posterImg;
    Context context;
    String imdb_id = null;
    String moviePoster = null;
    String movieTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        // show up button

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        context = this;
        ovTitle = (TextView) findViewById(R.id.ov_title);
        overview = (TextView) findViewById(R.id.overview);
        released = (TextView) findViewById(R.id.releaseDate);
        ratings = (TextView) findViewById(R.id.ratings);
        posterImg = (ImageView) findViewById(R.id.movie_poster);

        Intent otherIntent = getIntent();
        if (otherIntent != null && otherIntent.hasExtra(MOVIE_ID)) {
            int movieid = otherIntent.getIntExtra(MOVIE_ID, 0);
            if (otherIntent.hasExtra(HIGH_RES_POSTER))
                moviePoster = otherIntent.getStringExtra(HIGH_RES_POSTER);
            if (otherIntent.hasExtra(MOVIE_TITLE)) {
                movieTitle = otherIntent.getStringExtra(MOVIE_TITLE);
                collapsingToolbar.setTitle(movieTitle);
            }


            if (moviePoster != null) Picasso.with(this)
                    .load(moviePoster)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.no_preview)
                    .into(posterImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            scheduleStartPostponedTransition(posterImg);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            fetchMovie(TMDB.buildMovieURL(movieid));
        }
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                        return true;
                    }
                });
    }

    void fetchMovie(String url) {
        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObject(Movie.class, new ParsedRequestListener<Movie>() {
                    @Override
                    public void onResponse(Movie response) {
                        ovTitle.setText(getString(R.string.overview));
                        overview.setText(response.getOverview());
                        String rd = "Released: " + response.getReleaseDate();
                        released.setText(rd);
                        String rt = "Ratings: " + response.getVoteAverage() + "/10";
                        ratings.setText(rt);

                        // if for some weird reason we didn't get movie poster url from intent
                        // use url from response
                        if (moviePoster == null) Picasso.with(context)
                                .load(response.getPoster(true))
                                .error(R.drawable.no_preview)
                                .into(posterImg);

                        imdb_id = response.getImdb_id();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, anError.getMessage());
                    }
                });
    }

}
