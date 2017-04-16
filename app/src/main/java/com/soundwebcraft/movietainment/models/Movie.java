package com.soundwebcraft.movietainment.models;

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


    // constructors
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

    public String getImdb_id() {
        return imdb_id;
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
