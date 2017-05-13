package com.soundwebcraft.movietainment.networking.models;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@Parcel
public class TmdbApi {
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    // base url for loading tmdb images
    public static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/",
            YOUTUBE_VID_POSTER_BASE_URL = "https://img.youtube.com/vi/",
            YOUTUBE_DEFAULT_VID_THUMBNAIL = "default.jpg",
            POSTER_SIZE_SM = "w185",
            POSTER_SIZE_BG = "w342";

    public TmdbApi() {
    }

    public TmdbApi(String original_title, String poster_path, int id, String overview, double vote_average, Integer vote_count, String release_date) {
        this.originalTitle = original_title;
        this.posterPath = poster_path;
        this.id = id;
        this.overview = overview;
        this.voteAverage = vote_average;
        this.voteCount = vote_count;
        this.releaseDate = release_date;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Double getVoteAverage() {
        return voteAverage;
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

    public String getFormattedVoteCount() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(voteCount);
    }

    public String getFormattedReleaseDate() {
        if (TextUtils.isEmpty(getReleaseDate())) return "N/A";
        String[] kaboom = getReleaseDate().split("-");
        int year = Integer.parseInt(kaboom[0]),
                month = Integer.parseInt(kaboom[1]),
                day = Integer.parseInt(kaboom[2]);
        Calendar calendar = new GregorianCalendar(year, month - 1, day);
        return String.format(Locale.US, "%1$tB %1$te, %1$tY", calendar);
    }

    public class Genre {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Movie {
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        @SerializedName("genres")
        @Expose
        private List<Genre> genres = null;
        @SerializedName("imdb_id")
        @Expose
        private String imdbId;
        @SerializedName("runtime")
        @Expose
        private Integer runtime;

        public String getBackdropPath() {
            return backdropPath;
        }

        public List<Genre> getGenres() {
            return genres;
        }

        public String getImdbId() {
            return imdbId;
        }

        public Integer getRuntime() {
            return runtime;
        }

        public String getBackdrop(String path) {
            return Uri.parse(MOVIE_POSTER_BASE_URL)
                    .buildUpon()
                    .appendPath(POSTER_SIZE_BG)
                    .appendEncodedPath(path)
                    .build()
                    .toString();
        }
    }
    public class Trailers {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("key")
        @Expose
        private String key;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public Trailers(String key) {
            this.key = key;
        }

        public String getTrailerThumbnail() {
            return Uri.parse(YOUTUBE_VID_POSTER_BASE_URL)
                    .buildUpon()
                    .appendPath(getKey())
                    .appendPath(YOUTUBE_DEFAULT_VID_THUMBNAIL)
                    .build()
                    .toString();
        }
    }

    public class Reviews {
        @SerializedName("author")
        @Expose
        private String author;
        @SerializedName("content")
        @Expose
        private String content;

        public Reviews(String author, String content) {
            this.author = author;
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }


        @Override
        public String toString() {
            return "Author " + getAuthor() + "\n" +
                    "Content: " + getContent();
        }
    }
}
