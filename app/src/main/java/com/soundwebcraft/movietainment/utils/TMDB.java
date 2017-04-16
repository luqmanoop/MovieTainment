package com.soundwebcraft.movietainment.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.soundwebcraft.movietainment.BuildConfig;

public abstract class TMDB {
    // TmDB api base url
    private static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    // TmDB API KEY
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_KEY_QUERY_PARAM = "api_key";

    private static final String SORT_BY_QUERY_PARAM = "sort_by";
    // sort movies by popularity
    private static final String POPULARITY = "popularity.desc";
    // sort movies by user ratings
    private static final String RATINGS = "vote_average.desc";

    private static final String DISCOVER ="discover";
    private static final String MOVIE ="movie";

    // get movies url
    public static String buildMoviesURL() {
        return latestMovies().toString();
    }

    private static Uri latestMovies () {
        return Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(DISCOVER)
                .appendPath(MOVIE)
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .build();
    }
    public static String buildMoviesURL(String sortBy) {
        Uri uri = null;
        sortBy = sortBy == null ? "" : sortBy;
        switch (sortBy) {
            case "popularity":
                uri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(DISCOVER)
                        .appendPath(MOVIE)
                        .appendQueryParameter(SORT_BY_QUERY_PARAM, POPULARITY)
                        .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();
                break;
            case "ratings":
                uri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(DISCOVER)
                        .appendPath(MOVIE)
                        .appendQueryParameter(SORT_BY_QUERY_PARAM, RATINGS)
                        .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();
                break;
            default:
                uri = latestMovies();
                break;
        }
        return uri.toString();
    }

    // get movie url
    public static String buildMovieURL (int movieID) {
        return Uri.parse(API_BASE_URL)
                .buildUpon()
                .appendPath(MOVIE)
                .appendPath(String.valueOf(movieID))
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .build()
                .toString();

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
