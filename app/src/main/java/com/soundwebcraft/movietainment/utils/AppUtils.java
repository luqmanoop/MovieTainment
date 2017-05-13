package com.soundwebcraft.movietainment.utils;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class AppUtils {
    private static final String YOUTUBE_HOMEPAGE = "https://www.youtube.com/",
            YOUTUBE_WATCH_PATH = "watch",
            YOUTUBE_WATCH_QUERY_PARAM = "v";

    private AppUtils() {
    }

    public static void updateRecycler(RecyclerView.Adapter adapter, List data) {
        int size = adapter.getItemCount();
        adapter.notifyDataSetChanged();
        adapter.notifyItemRangeInserted(size, data.size() - 1);
    }

    public static String buildTrailerURL(String vidKey) {
        return Uri.parse(YOUTUBE_HOMEPAGE)
                .buildUpon()
                .appendPath(YOUTUBE_WATCH_PATH)
                .appendQueryParameter(YOUTUBE_WATCH_QUERY_PARAM, vidKey)
                .build()
                .toString();
    }
}
