package com.soundwebcraft.movietainment.models;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    // fields
    private String original_title;
    private String poster_path;
    private String overview;
    private double vote_average;
    private String release_date;
    private float mRatings;
    private int id;
    private String imdb_id;

    // base url for loading tmdb images
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/",
            POSTER_SIZE_SM = "w185",
            POSTER_SIZE_BG = "w342";

    // constructors
    public Movie(String title) { // useful for testing model with dummy data
        original_title = title;
    }

    public Movie(String original_title, String poster_path, int id) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.id = id;
    }

    public Movie(String original_title, String poster_path, String overview, double vote_average, String release_date) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public Movie(String original_title, String poster_path, String overview, double vote_average, String release_date, float ratings, int id, String imdb_id) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        mRatings = ratings;
        this.id = id;
        this.imdb_id = imdb_id;
    }

    // getters
    public String getOriginalTitle() {
        return original_title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public int getID() {
        return id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public static List<Movie> loadDummyMoviesData() {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            movies.add(new Movie("TBT Season " + (i + 1)));
        }
        return movies;
    }

    // get movie poster: low res default
    public String getPoster() {
        return MOVIE_POSTER_BASE_URL + POSTER_SIZE_SM + getPosterPath();
    }

    // get movie poster: true = high res
    public String getPoster(boolean highRes) {
        String url = null;
        if (highRes) {
            url = MOVIE_POSTER_BASE_URL + POSTER_SIZE_BG + getPosterPath();
        } else {
            url = MOVIE_POSTER_BASE_URL + POSTER_SIZE_SM + getPosterPath();
        }
        return url;
    }

    // toString override
    @Override
    public String toString() {
        return "Title: " +
                this.getOriginalTitle() +
                "\n" +
                "Released: " +
                this.getReleaseDate() +
                "\n" +
                "Poster URL: " +
                this.getPosterPath() +
                "\n" + "Ratings: " +
                this.getVoteAverage() +
                "\n";
    }
}
