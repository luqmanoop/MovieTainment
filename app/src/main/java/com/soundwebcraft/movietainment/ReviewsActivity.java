package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soundwebcraft.movietainment.adapters.MovieReviewsAdapter;
import com.soundwebcraft.movietainment.networking.data.remote.TmdbService;
import com.soundwebcraft.movietainment.networking.models.TMDb;
import com.soundwebcraft.movietainment.networking.models.TMDbResponse;
import com.soundwebcraft.movietainment.networking.utils.TmdbUtils;
import com.soundwebcraft.movietainment.utils.EmptyRecyclerView;
import com.soundwebcraft.movietainment.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.soundwebcraft.movietainment.networking.utils.TmdbUtils.emptyStateNoIntenet;
import static com.soundwebcraft.movietainment.utils.AppUtils.updateRecycler;

public class ReviewsActivity extends AppCompatActivity {
    public static final String TAG = ReviewsActivity.class.getSimpleName();
    EmptyRecyclerView reviewsRecycler;

    private MovieReviewsAdapter adapter;
    private Context mContext;
    private TmdbService mService;
    private List<TMDb.Reviews> allReviews = new ArrayList<>();

    @BindView(R.id.empty_view_tv)
    TextView emptyViewTextView;
    @BindView(R.id.empty_view_iv)
    ImageView emptyViewImageView;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        reviewsRecycler = (EmptyRecyclerView) findViewById(R.id.rv_reviews);
        final Intent otherIntent = getIntent();
        mContext = this;
        mService = TmdbUtils.getTmdbService();
        adapter = new MovieReviewsAdapter(mContext, allReviews);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        View emptyView = findViewById(R.id.empty_view);
        reviewsRecycler.setEmptyView(emptyView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        reviewsRecycler.setLayoutManager(linearLayoutManager);
        reviewsRecycler.setHasFixedSize(true);
        reviewsRecycler.setAdapter(adapter);


        if (otherIntent != null) {
            String title = getDataFromBundle(otherIntent, MovieDetailActivity.MOVIE_TITLE);
            if (actionBar != null) actionBar.setTitle(title);
            String movieId = getDataFromBundle(otherIntent, MovieDetailActivity.MOVIE_ID);
            if (TmdbUtils.connectionAvailable(this)) {
                fetchMovieReviews(movieId, 1);
            } else {
                emptyStateNoIntenet(emptyViewImageView, emptyViewTextView, getString(R.string.no_internet));
            }

        }

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                String movieId = getDataFromBundle(otherIntent, MovieDetailActivity.MOVIE_ID);
                fetchMovieReviews(movieId, page + 1);
            }
        };
        reviewsRecycler.addOnScrollListener(scrollListener);
    }

    private String getDataFromBundle(Intent intent, String key) {
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey(key)) {
            return String.valueOf(bundle.get(key));
        }
        return null;
    }

    private void fetchMovieReviews(final String movieID, int page) {
        if (TmdbUtils.connectionAvailable(mContext)) {
            mService.getMovieReviews(movieID, String.valueOf(page)).enqueue(new retrofit2.Callback<TMDbResponse.Reviews>() {
                @Override
                public void onResponse(Call<TMDbResponse.Reviews> call, retrofit2.Response<TMDbResponse.Reviews> response) {
                    if (response.isSuccessful()) {
                        if (isLoading) {
                            loadingIndicator.setVisibility(View.GONE);
                            isLoading = false;
                        }
                        allReviews.addAll(response.body().getReviews());
                        updateRecycler(adapter, allReviews);
                    } else {
                        int code = response.code();
                    }
                }

                @Override
                public void onFailure(Call<TMDbResponse.Reviews> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
