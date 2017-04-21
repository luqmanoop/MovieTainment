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
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.TMDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.soundwebcraft.movietainment.adapters.MoviesAdapter.HIGH_RES_POSTER;
import static com.soundwebcraft.movietainment.adapters.MoviesAdapter.MOVIE_ID;
import static com.soundwebcraft.movietainment.adapters.MoviesAdapter.MOVIE_TITLE;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName(),
            IMDB_MOVIE_PREVIEW = "http://www.imdb.com/title/";

    @BindView(R.id.ov_title) TextView ovTitle;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.releaseDate) TextView released;
    @BindView(R.id.ratings) TextView ratings;
    @BindView(R.id.movie_poster) ImageView posterImg;

    Context mContext;

    String imdb_id = null, moviePoster = null, movieTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;
        // init ButterKnife
        ButterKnife.bind(this);
        // show up button
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

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
        if (TMDB.isDeviceConnected(this)) {
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
                            if (moviePoster == null) Picasso.with(mContext)
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
        } else {
            Toast.makeText(this, "Internet apppears to be offline", Toast.LENGTH_LONG).show();
        }
    }

    public void shareMovie(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String caption = "Check out this awesome movie!";
        String title = "*" + movieTitle + "*";
        // if we cant get the movie imdb id for url preview then use movie overview
        String previewOrOverview = "";
        String movieReleasedDate = "";
        String movieRatings = "";
        if (imdb_id != null) {
            previewOrOverview = IMDB_MOVIE_PREVIEW + imdb_id;
        } else {
            previewOrOverview = overview.getText().toString();
            movieReleasedDate = "Released: " + released.getText().toString() + "\n";
            movieRatings = "Ratings: " + ratings.getText().toString() + "\n";
        }

        String hashTags = "*#Andela #Udacity #Google #MovieApp*";
        String withLove = "Made with love by *@luksy_breezy*";

        String shareBody = caption + "\n\n" +
                title + "\n" +
                previewOrOverview + "\n" +
                movieReleasedDate +
                movieRatings +
                hashTags + "\n" +
                withLove;
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // prevent activity we are sharing to from being placed onto the activity stack
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
