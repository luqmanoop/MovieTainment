package com.soundwebcraft.movietainment;

import android.content.Context;

import com.soundwebcraft.movietainment.networking.models.TMDbResponse;

import retrofit2.Response;

public class MovieTaskLoader extends android.support.v4.content.AsyncTaskLoader<Response<TMDbResponse.Movies>> {

    private MoviesActivity mMoviesActivity;
    private Response<TMDbResponse.Movies> mData;
    private int page;

    public MovieTaskLoader(MoviesActivity moviesActivity, Context context, int page) {
        super(context);
        mMoviesActivity = moviesActivity;
        this.page = page;
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public Response<TMDbResponse.Movies> loadInBackground() {
        return mMoviesActivity.fetchMovies(page);
    }

    @Override
    public void deliverResult(Response<TMDbResponse.Movies> data) {
        mData = data;
        super.deliverResult(data);
    }
}
