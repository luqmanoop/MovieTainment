package com.soundwebcraft.movietainment.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.soundwebcraft.movietainment.BuildConfig;

public abstract class TMDB {
    // TmDB api base url
    private static final String API_BASE_URL = "https://api.themoviedb.org/3/",
            API_KEY = BuildConfig.API_KEY, // TmDB API KEY
            API_KEY_QUERY_PARAM = "api_key",
            POPULARITY = "popular", // sort movies by popularity
            RATINGS = "top_rated", // sort movies by user ratings
            DISCOVER = "discover",
            MOVIE = "movie";
    public static final String PAGE_QUERY_PARAM = "page";

    // get movies url
    public static String buildMoviesURL() {
        return latestMovies().toString();
    }

    private static Uri latestMovies() {
        return Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(MOVIE)
                .appendPath("now_playing")
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .build();
    }

    public static String buildMoviesURL(String sortBy) {
        Uri uri = null;
        sortBy = sortBy == null ? "" : sortBy;
        switch (sortBy) {
            case "popularity":
                uri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(MOVIE)
                        .appendPath(POPULARITY)
                        .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                        .build();
                break;
            case "ratings":
                uri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendPath(MOVIE)
                        .appendPath(RATINGS)
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
    public static String buildMovieURL(int movieID) {
        return Uri.parse(API_BASE_URL)
                .buildUpon()
                .appendPath(MOVIE)
                .appendPath(String.valueOf(movieID))
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY)
                .build()
                .toString();

    }

    public static boolean isDeviceConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
