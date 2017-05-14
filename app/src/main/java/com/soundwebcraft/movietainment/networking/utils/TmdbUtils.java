package com.soundwebcraft.movietainment.networking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soundwebcraft.movietainment.R;
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

    public static void emptyStateNoIntenet(ImageView imageView, TextView textView, String msg) {
        imageView.setImageResource(R.drawable.cloud_error);
        textView.setText(msg);
    }

    public static void emptyStateNoData (ImageView imageView, TextView textView, String msg) {
        imageView.setVisibility(View.GONE);
        textView.setText(msg);
    }
}
