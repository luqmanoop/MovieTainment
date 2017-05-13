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

import com.soundwebcraft.movietainment.adapters.MovieReviewsAdapter;
import com.soundwebcraft.movietainment.adapters.MovieTrailersAdapter;
import com.soundwebcraft.movietainment.networking.data.remote.TmdbService;
import com.soundwebcraft.movietainment.networking.models.TMDb;
import com.soundwebcraft.movietainment.networking.models.TMDbResponse;
import com.soundwebcraft.movietainment.networking.utils.TmdbUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;

import static com.soundwebcraft.movietainment.utils.AppUtils.updateRecycler;
import static com.squareup.picasso.Picasso.with;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final String IMDB_MOVIE_PREVIEW = "http://www.imdb.com/title/";

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
    @BindView(R.id.rv_movie_trailers)
    RecyclerView trailersRecyclerView;

    private MovieTrailersAdapter mTrailersAdapter;
    private Picasso mPicasso;
    private MovieReviewsAdapter mReviewsAdapter;
    private TmdbService mService;
    private List<TMDb.Reviews> allReviews = new ArrayList<>();
    private List<TMDb.Trailers> allTrailers = new ArrayList<>();
    private Context mContext;

    private String imdb_id = null,
            posterLowRes = null,
            movieTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;
        // init ButterKnife
        ButterKnife.bind(this);
        Intent otherIntent = getIntent();

        mService = TmdbUtils.getTmdbService();

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


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);

        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        reviewsRecylcerView.setLayoutManager(linearLayoutManager2);
        reviewsRecylcerView.setHasFixedSize(true);
        reviewsRecylcerView.setNestedScrollingEnabled(false);

        trailersRecyclerView.setAdapter(mTrailersAdapter);
        reviewsRecylcerView.setAdapter(mReviewsAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slideAnimation();
        }

        if (otherIntent != null && otherIntent.hasExtra(Intent.EXTRA_TEXT)) {
            TMDb movie = Parcels.unwrap(otherIntent.getParcelableExtra(Intent.EXTRA_TEXT));
            movieTitle = movie.getOriginalTitle();
            posterLowRes = movie.getPoster();
            String posterHighRes = movie.getPoster(true);
            String movieReleased = movie.getFormattedReleaseDate();
            String movieRatings = movie.caculateRatings(movie.getVoteAverage());
            int movieid = 0;
            movieid = movie.getId();

            if (movieid > 0) fetchMovieDetails(movieid);

            // fetch movie poster
            loadMoviePoster(moviePoster);
            // fetch movie trailers
            fetchMovieTrailers(movieid);
            // fetch movie reviews
            fetchMovieReviews(movieid);

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
        mService.getMovie(String.valueOf(movieID)).enqueue(new retrofit2.Callback<TMDb.Movie>() {
            @Override
            public void onResponse(Call<TMDb.Movie> call, retrofit2.Response<TMDb.Movie> response) {
                if (response.isSuccessful()) {
                    String backdropPath = response.body().getBackdropPath();
                    imdb_id = response.body().getImdbId();
                    Integer runtime = response.body().getRuntime();
                    String genres = "";
                    for (TMDb.Genre genre :
                            response.body().getGenres()) {
                        genres += genre.getName() + ", ";

                    }
                    if (!TextUtils.isEmpty(genres)) mGenres.setText(genres);
                    else mGenres.setText(R.string.misc_not_available);

                    try {
                        int i = runtime / 60;
                        int remainder = runtime % 60;
                        String result = "";
                        if (i != 0) result += i + getString(R.string.movie_runtime_hour);
                        if (remainder != 0)
                            result += remainder + getString(R.string.movie_runtime_minute);
                        tvDuration.setText(result);
                    } catch (NumberFormatException e) {
                        tvDuration.setText(getString(R.string.misc_not_available));
                    }

                    loadMovieBackdrop(response.body().getBackdrop(backdropPath), movieBackdrop);
                } else {
                    int code = response.code();
                }
            }

            @Override
            public void onFailure(Call<TMDb.Movie> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchMovieTrailers(final int movieID) {
        if (TmdbUtils.connectionAvailable(mContext)) {
            mService.getMovieTrailers(String.valueOf(movieID)).enqueue(new retrofit2.Callback<TMDbResponse.Trailers>() {
                @Override
                public void onResponse(Call<TMDbResponse.Trailers> call, retrofit2.Response<TMDbResponse.Trailers> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getTrailers().size() > 0) {
                            allTrailers.addAll(response.body().getTrailers());
                            updateRecycler(mTrailersAdapter, allTrailers);
                        } else {
                            toggleViewVisibility(tvNoTrailerFound);
                        }
                    } else {
                        int code = response.code();
                    }
                }

                @Override
                public void onFailure(Call<TMDbResponse.Trailers> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            toggleViewVisibility(tvNoTrailerFound);
        }
    }

    private void fetchMovieReviews(final int movieID) {
        if (TmdbUtils.connectionAvailable(mContext)) {
            mService.getMovieReviews(String.valueOf(movieID)).enqueue(new retrofit2.Callback<TMDbResponse.Reviews>() {
                @Override
                public void onResponse(Call<TMDbResponse.Reviews> call, retrofit2.Response<TMDbResponse.Reviews> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getReviews().size() > 0) {
                            if (response.body().getReviews().size() > 3)
                                allReviews.addAll(response.body().getReviews().subList(0, 3));
                            else allReviews.addAll(response.body().getReviews());

                            updateRecycler(mReviewsAdapter, allReviews);
                            toggleViewVisibility(reviewResultContainer);

                        } else {
                            List<View> viewsToToggle = new ArrayList<View>();
                            viewsToToggle.add(reviewResultContainer);
                            viewsToToggle.add(dividerTop);
                            viewsToToggle.add(dividerBottom);
                            viewsToToggle.add(btnShowAllReviews);
                            viewsToToggle.add(tvNoReview);
                            toggleViewVisibility(viewsToToggle);
                        }
                    } else {
                        int code = response.code();
                    }
                }

                @Override
                public void onFailure(Call<TMDbResponse.Reviews> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            toggleViewVisibility(tvNoReview);
        }
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
    private void revealAnimation(ImageView imageView, long duration) {
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
    private void shareMovie() {
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
