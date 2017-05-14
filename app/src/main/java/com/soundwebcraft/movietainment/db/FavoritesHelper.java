package com.soundwebcraft.movietainment.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.soundwebcraft.movietainment.db.FavoritesContract.FavoritesEntry.*;

public class FavoritesHelper extends SQLiteOpenHelper {
    private static final String DATABAES_NAME = "FavoriteMovies.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "CREATE TABLE "  + TABLE_NAME + " (" +
                    _ID + " INTEGER NOT NULL PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DATE_ADDED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    public static final String[] ALL_COLUMNS =
            {_ID, COLUMN_TITLE, COLUMN_DATE_ADDED};

    public FavoritesHelper(Context context) {
        super(context, DATABAES_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
