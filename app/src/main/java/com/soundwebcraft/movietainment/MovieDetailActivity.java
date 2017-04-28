package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.TMDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName(),
            IMDB_MOVIE_PREVIEW = "http://www.imdb.com/title/";

    @BindView(R.id.movie_title)
    TextView tvMovieTitle;
    @BindView(R.id.ov_title)
    TextView mTextViewOverviewTitle;
    @BindView(R.id.overview)
    TextView mTextViewOverview;
    @BindView(R.id.tv_genres)
    TextView mGenres;
    @BindView(R.id.releaseDate)
    TextView mTextViewReleasedDate;
    @BindView(R.id.ratings)
    TextView mTextViewRatings;
    @BindView(R.id.tv_vote_count)
    TextView mTextViewVoteCount;
    @BindView(R.id.movie_backdrop)
    ImageView movieBackdrop;
    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.movie_rating_bar)
    MaterialRatingBar mRatingBar;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slideAnimation();
        }

        if (otherIntent != null && otherIntent.hasExtra(Intent.EXTRA_TEXT)) {
            Movie movie = Parcels.unwrap(otherIntent.getParcelableExtra(Intent.EXTRA_TEXT));
            movieTitle = movie.getOriginalTitle();
            posterLowRes = movie.getPoster();
            posterHighRes = movie.getPoster(true);
            movieReleased = movie.getFormattedReleaseDate();
            movieRatings = movie.caculateRatings(movie.getVoteAverage());
            int movieid = 0;
            movieid = movie.getID();

            collapsingToolbar.setTitle(movieTitle);

            if (movieid > 0) fetchMovieDetails(movieid);

            // load movie poster
            loadMoviePoster(moviePoster);

            tvMovieTitle.setText(movieTitle);
            mTextViewOverviewTitle.setText(getString(R.string.overview));
            mTextViewOverview.setText(movie.getOverview());
            mTextViewReleasedDate.setText(movieReleased);
            mGenres.setText(R.string.misc_loading_text);
            mTextViewRatings.setText(movieRatings);
            mRatingBar.setRating(Float.parseFloat(movieRatings));
            mTextViewVoteCount.setText(movie.getFormattedVoteCount() + " " + getString(R.string.movie_ratings).toLowerCase());
        }
    }

    // run slide animation on movie sypnosis
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void slideAnimation() {
        Slide slide = new Slide();
        slide.addTarget(mTextViewOverview);
        slide.setInterpolator(
                AnimationUtils.loadInterpolator(
                        this,
                        android.R.interpolator.linear_out_slow_in
                )
        );
        getWindow().setEnterTransition(slide);
    }

    // load movie poster
    private void loadMoviePoster (final ImageView target) {
        if (posterLowRes != null) Picasso.with(this)
                .load(posterLowRes)
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_preview)
                .into(target);
    }

    // load movie backdrop
    private void loadMovieBackdrop(String url, final ImageView target) {
        Picasso.with(MovieDetailActivity.this)
                .load(url)
                .noPlaceholder()
                .into(target, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override // if error loading backdrop. fallback to original movie poster as backdrop
                    public void onError() {
                        loadMoviePoster(target);
                    }
                });
    }

    private void fetchMovieDetails(int movieID) {
        AndroidNetworking.get(TMDB.buildMovieURL(movieID))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String genres = "";
                        try {
                            imdb_id = response.getString(getString(R.string.json_response_imdb_id));
                            JSONArray genresArray = response.getJSONArray(getString(R.string.json_response_genres));
                            int genreLength = genresArray.length();
                            for (int i = 0; i < genreLength; i++) {
                                JSONObject genreObj = (JSONObject) genresArray.get(i);
                                genres += genreObj.getString(getString(R.string.json_response_genre_name));
                                if (genreLength - i != 1) genres += ", ";
                            }
                            String backdropPath = response.getString(getString(R.string.json_response_movie_backdrop));
                            Log.d(TAG, Movie.getBackdrop(backdropPath));
                            loadMovieBackdrop(Movie.getBackdrop(backdropPath), movieBackdrop);
                            if (!TextUtils.isEmpty(genres)) mGenres.setText(genres);
                            else mGenres.setText(R.string.misc_not_available);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
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

    // share movie with friends :)
    public void shareMovie(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(getString(R.string.mime_type_text_document));

        String caption = getString(R.string.share_movie_caption);
        String title = "*" + movieTitle + "*";
        // if we cant get the movie imdb id for url preview then use movie mTextViewOverview
        String previewOrOverview = "";
        String movieReleasedDate = "";
        String movieRatings = "";
        if (imdb_id != null) {
            previewOrOverview = IMDB_MOVIE_PREVIEW + imdb_id;
        } else {
            previewOrOverview = mTextViewOverview.getText().toString();
            movieReleasedDate = getString(R.string.movie_release_date) + mTextViewReleasedDate.getText().toString() + "\n";
            movieRatings = getString(R.string.movie_ratings) + mTextViewRatings.getText().toString() + "\n";
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
