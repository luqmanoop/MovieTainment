package com.soundwebcraft.movietainment.models;

import android.net.Uri;
import android.text.TextUtils;

import org.parceler.Parcel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@Parcel
public class Movie {

    // fields
    String original_title;
    String poster_path;
    String overview;
    double vote_average;
    double vote_count;
    String release_date;
    float mRatings;
    int id;
    String imdb_id;

    public Movie() {
    }

    // base url for loading tmdb images
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/",
            POSTER_SIZE_SM = "w185",
            POSTER_SIZE_BG = "w500";

    // constructors
    public Movie(String title) { // useful for testing model with dummy data
        original_title = title;
    }

    public Movie(String original_title, String poster_path, int id) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.id = id;
    }

    public Movie(String original_title, String poster_path, int id, String overview, double vote_average, String release_date) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.id = id;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public Movie(String original_title, String poster_path, int id, String overview, double vote_average, double vote_count, String release_date) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.id = id;
        this.overview = overview;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
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

    public String caculateRatings(Double voteAverage) {
        java.text.DecimalFormat df = new java.text.DecimalFormat(".##");
        double result = Double.parseDouble(df.format((voteAverage / 10) * 5)) + 0;
        return String.valueOf(result);
    }

    public double getVoteCount() {
        return vote_count;
    }

    public String getFormattedVoteCount() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(vote_count);
    }

    public String getFormattedReleaseDate () {
        if (TextUtils.isEmpty(getReleaseDate())) return "N/A";
        String[] kaboom = getReleaseDate().split("-");
        int year = Integer.parseInt(kaboom[0]),
                month = Integer.parseInt(kaboom[1]),
                day = Integer.parseInt(kaboom[2]);
        Calendar calendar = new GregorianCalendar(year,month - 1, day);
        return String.format(Locale.US,"%1$tB %1$te, %1$tY", calendar);
    }
    public static String getBackdrop(String path) {
        return Uri.parse(MOVIE_POSTER_BASE_URL)
                .buildUpon()
                .appendPath(POSTER_SIZE_BG)
                .appendEncodedPath(path)
                .build()
                .toString();
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
