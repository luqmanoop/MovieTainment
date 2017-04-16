package com.soundwebcraft.movietainment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class MoviesActivity extends AppCompatActivity {
    public static final String TAG = MoviesActivity.class.getSimpleName();
    final List<Movie> allMovies = new ArrayList<>();
    private MoviesAdapter adapter;
    // hold reference to scrollListener
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

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

    void fetchMovies(int page, String sort) {
        if (isConnected()) {
            AndroidNetworking.get(TMDB.buildMoviesURL(sort))
                    .setPriority(Priority.HIGH)
                    .addQueryParameter("page", String.valueOf(page))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<Movie> movieList = new ArrayList<Movie>();
                            try {
                                JSONArray res = response.getJSONArray("results");
                                int total = res.length();
                                Log.d(TAG, "" + total);
                                for (int i = 0; i < total; i++) {
                                    JSONObject movieObj = (JSONObject) res.get(i);
                                    movieList.add(new Movie(
                                            movieObj.getString("original_title"),
                                            movieObj.getString("poster_path"),
                                            movieObj.getInt("id")
                                    ));
                                    Log.d(TAG, movieObj.getString("poster_path"));
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
            Toast.makeText(this, "Internet apppears to be offline", Toast.LENGTH_LONG).show();
        }
    }

    // check if connection is available
    boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
