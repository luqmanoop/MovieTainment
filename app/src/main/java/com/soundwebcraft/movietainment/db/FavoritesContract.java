package com.soundwebcraft.movietainment.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final String AUTHORITY = "com.soundwebcraft.movietainment.db.favoritesprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = getTableName();



    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri BASE_CONTENT_URI =
                FavoritesContract.CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_FAVORITES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_FAVORITES;


        public static final String TABLE_NAME = getTableName();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE_ADDED = "date_added";

        public static final String[] PROJECTION_ALL =
                {_ID, COLUMN_TITLE, COLUMN_DATE_ADDED};

        public static final String SORT_ORDER_DEFAULT =
                COLUMN_DATE_ADDED + " DESC";

        public static Uri buildFavoritesUri (int id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }
    }

    private static String getTableName () {
        return "favorites";
    }
}
