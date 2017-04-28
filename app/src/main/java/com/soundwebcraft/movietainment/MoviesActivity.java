package com.soundwebcraft.movietainment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.soundwebcraft.movietainment.adapters.MoviesAdapter;
import com.soundwebcraft.movietainment.models.Movie;
import com.soundwebcraft.movietainment.utils.EndlessRecyclerViewScrollListener;
import com.soundwebcraft.movietainment.utils.TMDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesActivity extends AppCompatActivity {
    public static final String TAG = MoviesActivity.class.getSimpleName();
    final List<Movie> allMovies = new ArrayList<>();
    private MoviesAdapter adapter;
    // hold reference to scrollListener
    EndlessRecyclerViewScrollListener scrollListener;
    @BindView(R.id.loadingPanel)
    RelativeLayout loadingPanel;
    boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        adapter = new MoviesAdapter(MoviesActivity.this, allMovies);

        final StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        fetchMovies(1, null);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loading = false;
                fetchMovies(page + 1, null);
            }
        };
        // listen for scroll
        recyclerView.addOnScrollListener(scrollListener);
    }

    // add movies menu to activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // load movies by sort criteria
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.by_popularity:
                resetEndlessScroll();
                toggleLoadingPanelVisibility();
                fetchMovies(1, getString(R.string.by_popularity));
                return true;
            case R.id.by_ratings:
                resetEndlessScroll();
                toggleLoadingPanelVisibility();
                fetchMovies(1, getString(R.string.by_ratings));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetEndlessScroll() {
        allMovies.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }

    void toggleLoadingPanelVisibility() {
        if (loadingPanel.getVisibility() == View.VISIBLE) {
            loading = false;
            loadingPanel.setVisibility(View.INVISIBLE);
        } else {
            loading = true;
            loadingPanel.setVisibility(View.VISIBLE);
        }
    }

    void fetchMovies(int page, String sort) {
        if (TMDB.isDeviceConnected(this)) {
            AndroidNetworking.get(TMDB.buildMoviesURL(sort))
                    .setPriority(Priority.HIGH)
                    .addQueryParameter(TMDB.PAGE_QUERY_PARAM, String.valueOf(page))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (loading) {
                                toggleLoadingPanelVisibility();
                            }
                            List<Movie> movieList = new ArrayList<Movie>();
                            try {
                                JSONArray res = response.getJSONArray(getString(R.string.json_response_results));
                                int total = res.length();
                                for (int i = 0; i < total; i++) {
                                    JSONObject movieObj = (JSONObject) res.get(i);
                                    movieList.add(new Movie(
                                            movieObj.getString(getString(R.string.json_response_original_title)),
                                            movieObj.getString(getString(R.string.json_response_poster_path)),
                                            movieObj.getInt(getString(R.string.json_response_movie_id)),
                                            movieObj.getString(getString(R.string.json_response_overview)),
                                            movieObj.getDouble(getString(R.string.json_response_vote_average)),
                                            movieObj.getDouble("vote_count"),
                                            movieObj.getString(getString(R.string.json_response_released_date))
                                    ));
                                    //Log.d(TAG, "HERE " + movieList.get(i).toString());
                                }
                                allMovies.addAll(movieList);
                                int size = adapter.getItemCount();
                                Log.d(TAG, allMovies.size() + "");
                                adapter.notifyItemRangeInserted(size, allMovies.size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.getMessage();
                        }
                    });
        } else {
            Toast.makeText(this, R.string.misc_no_connection, Toast.LENGTH_LONG).show();
        }
    }
}
