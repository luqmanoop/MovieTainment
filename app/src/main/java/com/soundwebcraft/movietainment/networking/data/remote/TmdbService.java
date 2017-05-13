package com.soundwebcraft.movietainment.networking.data.remote;

import com.soundwebcraft.movietainment.BuildConfig;
import com.soundwebcraft.movietainment.networking.models.TMDbResponse;
import com.soundwebcraft.movietainment.networking.models.TMDb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {
    @GET("{sort}?api_key=" + BuildConfig.API_KEY)
    Call<TMDbResponse.Movies> getMovies(@Path("sort") String sort, @Query("page") String key);

    @GET("{id}?api_key=" + BuildConfig.API_KEY)
    Call<TMDb.Movie> getMovie(@Path("id") String id);

    @GET("{id}/reviews?api_key=" + BuildConfig.API_KEY)
    Call<TMDbResponse.Reviews> getMovieReviews(@Path("id") String id);

    @GET("{id}/videos?api_key=" + BuildConfig.API_KEY)
    Call<TMDbResponse.Trailers> getMovieTrailers(@Path("id") String id);
}
