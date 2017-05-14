package com.soundwebcraft.movietainment.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.soundwebcraft.movietainment.db.FavoritesContract.AUTHORITY;
import static com.soundwebcraft.movietainment.db.FavoritesContract.FavoritesEntry;
import static com.soundwebcraft.movietainment.db.FavoritesContract.FavoritesEntry.TABLE_NAME;
import static com.soundwebcraft.movietainment.db.FavoritesContract.PATH_FAVORITES;
import static com.soundwebcraft.movietainment.db.FavoritesHelper.ALL_COLUMNS;


public class FavoritesProvider extends ContentProvider {

    private SQLiteDatabase db;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int FAVORITES_LIST = 1;
    public static final int FAVORITES_ITEM = 2;

    static {
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITES, FAVORITES_LIST);
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITES + "/#", FAVORITES_ITEM);
    }

    @Override
    public boolean onCreate() {
        FavoritesHelper helper = new FavoritesHelper(getContext());
        db = helper.getWritableDatabase();

        if (db == null) return false;
        if (db.isReadOnly()) {
            db.close();
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case FAVORITES_LIST:
                break;
            case FAVORITES_ITEM:
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        return queryBuilder.query(db, ALL_COLUMNS, selection, selectionArgs, null, null, FavoritesEntry.SORT_ORDER_DEFAULT);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FAVORITES_LIST:
                return FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_ITEM:
                return FavoritesEntry.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != FAVORITES_LIST) {
            throw new IllegalArgumentException("Invalid Uri: " + uri);
        }

        long id = db.insert(TABLE_NAME, null, values);
        return Uri.parse(PATH_FAVORITES + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int delete = 0;
        switch (uriMatcher.match(uri)) {
            case FAVORITES_LIST:
                db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_ITEM:
                String where = FavoritesEntry._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    where += " AND " + selection;
                }
                delete = db.delete(TABLE_NAME, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updated = 0;
        switch (uriMatcher.match(uri)) {
            case FAVORITES_LIST:
                db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case FAVORITES_ITEM:
                String where = FavoritesEntry._ID + " = " + uri.getLastPathSegment();
                assert selection != null;
                if (!selection.isEmpty()) {
                    where += " AND " + selection;
                }
                updated = db.update(TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
        return updated;
    }
}
