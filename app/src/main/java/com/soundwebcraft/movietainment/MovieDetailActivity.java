package com.soundwebcraft.movietainment;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.soundwebcraft.movietainment.adapters.MovieReviewsAdapter;
import com.soundwebcraft.movietainment.adapters.MovieTrailersAdapter;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.TMDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.squareup.picasso.Picasso.with;

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
    @BindView(R.id.tv_no_trailer)
    TextView tvNoTrailerFound;
    @BindView(R.id.tv_duration)
    TextView tvDuration;

    @BindView(R.id.rv_movie_trailers)
    RecyclerView trailersRecyclerView;

    private MovieTrailersAdapter mTrailersAdapter;
    private List<Movie> allTrailers = new ArrayList<>();
    private Picasso mPicasso;

    Context mContext;

    String imdb_id = null,
            posterLowRes = null,
            posterHighRes = null,
            movieTitle = null,
            movieReleased = null,
            movieRatings = null;
    private List<Movie.MovieReviews> allReviews = new ArrayList<>();
    @BindView(R.id.rv_reviews)
    RecyclerView reviewsRecylcerView;
    @BindView(R.id.review_result_container)
    LinearLayout reviewResultContainer;
    @BindView(R.id.view5)
    View dividerTop;
    @BindView(R.id.view6)
    View dividerBottom;
    @BindView(R.id.tv_no_review)
    TextView tvNoReview;
    @BindView(R.id.bt_show_all_reviews)
    Button btnShowAllReviews;

    private MovieReviewsAdapter mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;
        // init ButterKnife
        ButterKnife.bind(this);

        mPicasso = with(mContext);
        mPicasso.setIndicatorsEnabled(false);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mTrailersAdapter = new MovieTrailersAdapter(mContext, allTrailers);
        mReviewsAdapter = new MovieReviewsAdapter(mContext, allReviews);

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

            if (movieid > 0) fetchMovieDetails(movieid);

            // load movie poster
            loadMoviePoster(moviePoster);
            // load trailers
            fetchMovieTrailers(movieid);
            // fetch reviews
            fetchMovieReviews(movieid);

            tvMovieTitle.setText(movieTitle);
            mTextViewOverviewTitle.setText(getString(R.string.overview));
            mTextViewOverview.setText(movie.getOverview());
            mTextViewReleasedDate.setText(movieReleased);
            mGenres.setText(R.string.misc_loading_text);
            mTextViewRatings.setText(movieRatings);
            mRatingBar.setRating(Float.parseFloat(movieRatings));
            mTextViewVoteCount.setText(movie.getFormattedVoteCount() + " " + getString(R.string.movie_ratings).toLowerCase());

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            trailersRecyclerView.setLayoutManager(linearLayoutManager);
            trailersRecyclerView.setAdapter(mTrailersAdapter);

            final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            reviewsRecylcerView.setLayoutManager(linearLayoutManager2);
            reviewsRecylcerView.setHasFixedSize(true);
            reviewsRecylcerView.setNestedScrollingEnabled(false);
            reviewsRecylcerView.setAdapter(mReviewsAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.menu_share:
                shareMovie();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    private void loadMoviePoster(final ImageView target) {
        if (posterLowRes != null)
            mPicasso.load(posterLowRes)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.no_preview)
                    .into(target, new Callback() {
                        @Override
                        public void onSuccess() {
                            target.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    revealAnimation(target, 600);
                                }
                            }, 100);
                        }

                        @Override
                        public void onError() {

                        }
                    });
    }

    // load movie backdrop
    private void loadMovieBackdrop(String url, final ImageView target) {
        mPicasso.load(url)
                .noPlaceholder()
                .into(target, new Callback() {
                    @Override
                    public void onSuccess() {
                        target.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                revealAnimation(target, 1000);
                            }
                        }, 100);
                    }

                    @Override
                    // if error loading backdrop. fallback to original movie poster as backdrop
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
                            String runtime = response.getString(getString(R.string.json_response_runtime));
                            try {
                                int i = Integer.parseInt(runtime) / 60;
                                int remainder = Integer.parseInt(runtime) % 60;
                                String result = "";
                                if (i != 0) result += i + getString(R.string.movie_runtime_hour);
                                if (remainder != 0)
                                    result += remainder + getString(R.string.movie_runtime_minute);
                                tvDuration.setText(result);
                            } catch (NumberFormatException e) {
                                tvDuration.setText(getString(R.string.misc_not_available));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void fetchMovieTrailers(final int movieID) {
        if (TMDB.isDeviceConnected(mContext)) {
            AndroidNetworking.get(TMDB.getMovieTrailers(movieID))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<Movie> trailersList = new ArrayList<Movie>();
                            try {
                                JSONArray res = response.getJSONArray(getString(R.string.json_response_results));
                                int length = res.length();
                                if (length > 0) {
                                    for (int i = 0; i < length; i++) {
                                        JSONObject trailerObj = (JSONObject) res.get(i);
                                        trailersList.add(
                                                new Movie(
                                                        movieID,
                                                        trailerObj.getString("key")
                                                )
                                        );
                                    }
                                    allTrailers.addAll(trailersList);
                                    trailersRecyclerView.setAdapter(mTrailersAdapter);
                                    mTrailersAdapter.notifyItemRangeInserted(mTrailersAdapter.getItemCount(), allTrailers.size() - 1);
                                } else {
                                    toggleViewVisibility(tvNoTrailerFound);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, anError.getMessage());
                        }
                    });
        } else {
            toggleViewVisibility(tvNoTrailerFound);
        }
    }

    private void fetchMovieReviews(final int movieID) {
        AndroidNetworking.get(TMDB.getMovieReviews(movieID))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalPages = Integer.parseInt(response.getString(getString(R.string.json_response_review_total_page)));
                            List<Movie.MovieReviews> movieReviewsList = new ArrayList<Movie.MovieReviews>();
                            JSONArray res = response.getJSONArray(getString(R.string.json_response_results));
                            int length = res.length();
                            if (length > 0) {
                                if (length > 3) length = 3;
                                for (int i = 0; i < length; i++) {
                                    JSONObject reviewObj = (JSONObject) res.get(i);
                                    movieReviewsList.add(new Movie.MovieReviews(
                                            reviewObj.getString(getString(R.string.json_response_review_author)),
                                            reviewObj.getString(getString(R.string.json_response_review_content))
                                    ));
                                    Log.d(TAG, movieReviewsList.get(i).toString());
                                }
                                allReviews.addAll(movieReviewsList);
                                reviewsRecylcerView.setAdapter(mReviewsAdapter);
                                mReviewsAdapter.notifyItemRangeInserted(mReviewsAdapter.getItemCount(), allReviews.size() - 1);
                                reviewResultContainer.setVisibility(View.VISIBLE);
                            } else {
                                List<View> viewsToToggle = new ArrayList<View>();
                                viewsToToggle.add(reviewResultContainer);
                                viewsToToggle.add(dividerTop);
                                viewsToToggle.add(dividerBottom);
                                viewsToToggle.add(btnShowAllReviews);
                                viewsToToggle.add(tvNoReview);

                                toggleViewVisibility(viewsToToggle);

                                tvNoReview.setText(R.string.misc_no_review);
                                tvNoReview.setGravity(Gravity.NO_GRAVITY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, anError.getMessage());
                    }
                });
    }

    private void toggleViewVisibility(View target) {
        if (target.getVisibility() == View.GONE || target.getVisibility() == View.INVISIBLE)
            target.setVisibility(View.VISIBLE);
        else target.setVisibility(View.GONE);
    }

    private void toggleViewVisibility(List<View> target) {
        for (View view :
                target) {
            if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE)
                view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.GONE);
        }
    }

    // reveal animation
    void revealAnimation(ImageView imageView, long duration) {
        if (Build.VERSION.SDK_INT > 20) {
            int cx = (imageView.getLeft() + imageView.getRight()) / 2;
            int cy = (imageView.getTop() + imageView.getBottom()) / 2;
            int finalRad = (int) Math.hypot(imageView.getWidth(), imageView.getHeight());
            Animator reveal = ViewAnimationUtils.createCircularReveal(
                    imageView,
                    cx,
                    cy,
                    0,
                    finalRad
            );
            reveal.setDuration(duration);
            reveal.setInterpolator(new DecelerateInterpolator());
            imageView.setVisibility(View.VISIBLE);
            reveal.start();
        }
    }

    // share movie with friends :)
    public void shareMovie() {
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
