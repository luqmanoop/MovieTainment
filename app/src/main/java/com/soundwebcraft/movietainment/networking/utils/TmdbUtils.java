package com.soundwebcraft.movietainment.networking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.soundwebcraft.movietainment.networking.data.remote.RetrofitClient;
import com.soundwebcraft.movietainment.networking.data.remote.TmdbService;

public class TmdbUtils {
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static TmdbService getTmdbService () {
        return RetrofitClient.getClient().create(TmdbService.class);
    }

    public static boolean connectionAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
