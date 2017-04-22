package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName(),
            IMDB_MOVIE_PREVIEW = "http://www.imdb.com/title/";

    @BindView(R.id.ov_title)
    TextView ovTitle;
    @BindView(R.id.overview)
    TextView overview;
    @BindView(R.id.releaseDate)
    TextView released;
    @BindView(R.id.ratings)
    TextView ratings;
    @BindView(R.id.movie_poster)
    ImageView posterImgView;

    Context mContext;

    String imdb_id = null,
            posterLowRes = null,
            posterHighRes = null,
            movieTitle = null,
            movieReleased = null,
            movieRatings = null;

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

        if (otherIntent != null && otherIntent.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movie = Parcels.unwrap(otherIntent.getParcelableExtra(Intent.EXTRA_TEXT));
            movieTitle = movie.getOriginalTitle();
            posterLowRes = movie.getPoster();
            posterHighRes = movie.getPoster(true);
            movieReleased = getString(R.string.released) + movie.getReleaseDate();
            movieRatings = getString(R.string.ratings) + movie.getVoteAverage();
            int movieid = movie.getID();

            collapsingToolbar.setTitle(movieTitle);

            overview.setText(movie.getOverview());
            released.setText(movieReleased);
            ratings.setText(movieRatings);

            if (posterLowRes != null) Picasso.with(this)
                    .load(posterLowRes)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.no_preview)
                    .into(posterImgView, new Callback() {
                        @Override
                        public void onSuccess() {
                            scheduleStartPostponedTransition(posterImgView);
                            if (posterHighRes != null) new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadHighResMoviePoster(posterHighRes);
                                }
                            }, 1000);
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, "Error loading image");
                        }
                    });

            //fetchMovie(TMDB.buildMovieURL(movieid));
        }
    }

    private void loadHighResMoviePoster(String url) {
        Picasso.with(this)
                .load(url)
                .noPlaceholder()
                .into(posterImgView);
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
                            String rd = getString(R.string.released) + response.getReleaseDate();
                            released.setText(rd);
                            String rt = getString(R.string.ratings) + response.getVoteAverage() + "/10";
                            ratings.setText(rt);

                            // if for some weird reason we didn't get movie poster url from intent
                            // use url from response
                            if (posterLowRes == null) Picasso.with(mContext)
                                    .load(response.getPoster(true))
                                    .error(R.drawable.no_preview)
                                    .into(posterImgView);

                            imdb_id = response.getImdb_id();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getMessage());
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.no_connection_msg), Toast.LENGTH_LONG).show();
        }
    }

    public void shareMovie(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String caption = getString(R.string.share_movie_caption);
        String title = "*" + movieTitle + "*";
        // if we cant get the movie imdb id for url preview then use movie overview
        String previewOrOverview = "";
        String movieReleasedDate = "";
        String movieRatings = "";
        if (imdb_id != null) {
            previewOrOverview = IMDB_MOVIE_PREVIEW + imdb_id;
        } else {
            previewOrOverview = overview.getText().toString();
            movieReleasedDate = getString(R.string.released) + released.getText().toString() + "\n";
            movieRatings = getString(R.string.ratings) + ratings.getText().toString() + "\n";
        }

        String hashTags = getString(R.string.share_movie_hashtags);
        String withLove = getString(R.string.share_movie_developer);

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
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_intent_title)));
    }
}
