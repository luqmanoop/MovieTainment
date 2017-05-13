package com.soundwebcraft.movietainment.networking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    public class Movies {
        @SerializedName("results")
        @Expose
        private List<TmdbApi> results = null;

        public List<TmdbApi> getMovies() {
            return results;
        }
    }
    public class Trailers {
        @SerializedName("results")
        @Expose
        private List<TmdbApi.Trailers> trailers = null;

        public List<TmdbApi.Trailers> getTrailers() {
            return trailers;
        }
    }
    public class Reviews {
        @SerializedName("results")
        @Expose
        private List<TmdbApi.Reviews> reviews = null;

        public List<TmdbApi.Reviews> getReviews () {
            return reviews;
        }
    }
}
