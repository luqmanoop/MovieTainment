package com.soundwebcraft.movietainment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.soundwebcraft.movietainment.adapters.MoviesAdapter;
import com.soundwebcraft.movietainment.networking.data.remote.TmdbService;
import com.soundwebcraft.movietainment.networking.models.TMDbResponse;
import com.soundwebcraft.movietainment.networking.models.TMDb;
import com.soundwebcraft.movietainment.networking.utils.TmdbUtils;
import com.soundwebcraft.movietainment.utils.EndlessRecyclerViewScrollListener;
import com.soundwebcraft.movietainment.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class MoviesActivity extends AppCompatActivity {

    public static final String TAG = MoviesActivity.class.getSimpleName();
    private final List<TMDb> mMovies = new ArrayList<>();
    private MoviesAdapter adapter;
    private TmdbService mService;

    // hold reference to scrollListener
    private EndlessRecyclerViewScrollListener scrollListener;
    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;

    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        mService = TmdbUtils.getTmdbService();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        adapter = new MoviesAdapter(mMovies, MoviesActivity.this);

        final StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loading = false;
                fetchMovies(page + 1);
            }
        };
        // fetch movies
        fetchMovies(1);
        // listen for scroll
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void fetchMovies(int page) {
        mService.getMovies(getMovieSort(), String.valueOf(page)).enqueue(new Callback<TMDbResponse.Movies>() {
            @Override
            public void onResponse(Call<TMDbResponse.Movies> call, retrofit2.Response<TMDbResponse.Movies> response) {
                if (response.isSuccessful()) {
                    if (loading) {
                        toggleLoadingPanelVisibility();
                    }
                    mMovies.addAll(response.body().getMovies());
                    AppUtils.updateRecycler(adapter, mMovies);

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
        }
        return super.onOptionsItemSelected(item);
    }

    // fetch movies by sort criteria
    private void fetchBySort(String string) {
        resetEndlessScroll();
        toggleLoadingPanelVisibility();
        saveMovieSort(string);
        fetchMovies(1);
    }

    private void resetEndlessScroll() {
        mMovies.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    private void toggleLoadingPanelVisibility() {
        if (loadingPanel.getVisibility() == View.VISIBLE) {
            loading = false;
            loadingPanel.setVisibility(View.INVISIBLE);
        } else {
            loading = true;
            loadingPanel.setVisibility(View.VISIBLE);
        }
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
}
