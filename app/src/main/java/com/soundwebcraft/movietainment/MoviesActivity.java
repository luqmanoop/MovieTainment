package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soundwebcraft.movietainment.adapters.MoviesAdapter;
import com.soundwebcraft.movietainment.networking.data.remote.TmdbService;
import com.soundwebcraft.movietainment.networking.models.TMDb;
import com.soundwebcraft.movietainment.networking.models.TMDbResponse;
import com.soundwebcraft.movietainment.networking.utils.TmdbUtils;
import com.soundwebcraft.movietainment.utils.AppUtils;
import com.soundwebcraft.movietainment.utils.EmptyRecyclerView;
import com.soundwebcraft.movietainment.utils.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.soundwebcraft.movietainment.networking.utils.TmdbUtils.emptyStateNoData;
import static com.soundwebcraft.movietainment.networking.utils.TmdbUtils.emptyStateNoIntenet;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Response<TMDbResponse.Movies>> {

    // public static final String TAG = MoviesActivity.class.getSimpleName();
    private final List<TMDb> mMovies = new ArrayList<>();
    private MoviesAdapter adapter;
    private TmdbService mService;
    public static final int LOADER_ID = 1;
    // hold reference to scrollListener
    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.empty_view_tv)
    TextView emptyViewTextView;
    @BindView(R.id.empty_view_iv)
    ImageView emptyViewImageView;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private boolean isLoading = true;
    public static final String TAG = "RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        mService = TmdbUtils.getTmdbService();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) findViewById(R.id.rv_movies);
        adapter = new MoviesAdapter(mMovies, MoviesActivity.this);

        View emptyView = findViewById(R.id.empty_view);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchMoreMovies(page + 1);
            }
        };
        // listen for scroll
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void fetchMoreMovies(int page) {
        mService.getMovies(getMovieSort(), String.valueOf(page)).enqueue(new Callback<TMDbResponse.Movies>() {
            @Override
            public void onResponse(Call<TMDbResponse.Movies> call, retrofit2.Response<TMDbResponse.Movies> response) {
                if (response.isSuccessful()) {
                    if (isLoading) {
                        loadingIndicator.setVisibility(View.GONE);
                        isLoading = false;
                    }
                    List<TMDb> result = response.body().getMovies();
                    if (result.size() > 0) {
                        mMovies.addAll(result);
                        int size = adapter.getItemCount();
                        adapter.notifyItemRangeInserted(size, mMovies.size() - 1);
                    }
                    if (mMovies.size() <= 0) {
                        emptyStateNoData(emptyViewImageView, emptyViewTextView, getString(R.string.no_movie_data));
                    }

                } else {
                    int code = response.code();
                }
            }

            @Override
            public void onFailure(Call<TMDbResponse.Movies> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public Response<TMDbResponse.Movies> fetchMovies(int page) {
        Log.d(TAG, "fetchMovies: ");
        Response<TMDbResponse.Movies> movies = null;
        // fetch movies
        if (TmdbUtils.connectionAvailable(this)) {
            try {
                movies = mService.getMovies(getMovieSort(), String.valueOf(page)).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            emptyStateNoIntenet(emptyViewImageView, emptyViewTextView, getString(R.string.no_internet));
        }
        return movies;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.by_popularity:
                if (getMovieSort().equals(getString(R.string.sort_by_popular))) {
                    return false;
                }
                fetchBySort(getString(R.string.sort_by_popular));
                return true;
            case R.id.by_ratings:
                if (getMovieSort().equals(getString(R.string.sort_by_top_rated))) {
                    return false;
                }
                fetchBySort(getString(R.string.sort_by_top_rated));
                return true;
            case R.id.by_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // fetch movies by sort criteria
    private void fetchBySort(String string) {
        resetEndlessScroll();
        saveMovieSort(string);
        getSupportLoaderManager().restartLoader(LOADER_ID, null, MoviesActivity.this);
    }

    private void resetEndlessScroll() {
        mMovies.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }


    private void saveMovieSort(String sort) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit()
                .putString(getString(R.string.shared_preference_key), sort)
                .apply();
    }

    private String getMovieSort() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(
                getString(R.string.shared_preference_key),
                getString(R.string.by_now_playing)
        );
    }

    @Override
    public Loader<Response<TMDbResponse.Movies>> onCreateLoader(int id, Bundle args) {
        return new MovieTaskLoader(this, MoviesActivity.this, 1);
    }

    @Override
    public void onLoadFinished(Loader<Response<TMDbResponse.Movies>> loader, Response<TMDbResponse.Movies> data) {
        List<TMDb> result = null;
        if (data.isSuccessful()) {
            if (isLoading) {
                loadingIndicator.setVisibility(View.GONE);
                isLoading = false;
            }
            result = data.body().getMovies();
            mMovies.addAll(result);
            AppUtils.updateRecycler(adapter, mMovies);
            if (mMovies.size() <= 0) {
                emptyStateNoData(emptyViewImageView, emptyViewTextView, getString(R.string.no_movie_data));
            }
        } else {
            int code = data.code();
        }
    }

    @Override
    public void onLoaderReset(Loader<Response<TMDbResponse.Movies>> loader) {
        mMovies.clear();
        adapter.updateViews(new ArrayList<TMDb>());
    }

}
