package com.soundwebcraft.movietainment.networking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TMDbResponse {
    public class Movies {
        @SerializedName("results")
        @Expose
        private List<TMDb> results = null;

        public List<TMDb> getMovies() {
            return results;
        }
    }
    public class Trailers {
        @SerializedName("results")
        @Expose
        private List<TMDb.Trailers> trailers = null;

        public List<TMDb.Trailers> getTrailers() {
            return trailers;
        }
    }
    public class Reviews {
        @SerializedName("results")
        @Expose
        private List<TMDb.Reviews> reviews = null;

        public List<TMDb.Reviews> getReviews () {
            return reviews;
        }
    }
}
