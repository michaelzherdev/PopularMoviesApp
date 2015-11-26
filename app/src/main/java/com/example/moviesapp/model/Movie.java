package com.example.moviesapp.model;

import android.net.Uri;

/**
 * Created by mzherdev on 18.11.2015.
 */
public class Movie {

    // These are the names of the JSON objects that need to be extracted.
    public static final String RESULT_LIST = "results";
    public static final String MOVIE_ID = "id";
    public static final String MOVIE_TITLE = "original_title";
    public static final String MOVIE_POSTER = "poster_path"; // maybe backdrop_path
    public static final String MOVIE_SYNOPSIS = "overview";
    public static final String MOVIE_RATING = "vote_average";
    public static final String MOVIE_RELEASE_DATE = "release_date";
    public static final String MOVIE_BACKDROP_PATH  ="backdrop_path";

    public static final String EXTRA_MOVIE_ID = "android.movieapp.movie_id";
    public static final String EXTRA_MOVIE_TITLE = "android.movieapp.original_title";
    public static final String EXTRA_MOVIE_POSTER = "android.movieapp.poster_path";
    public static final String EXTRA_MOVIE_SYNOPSIS = "android.movieapp.overview";
    public static final String EXTRA_MOVIE_RATING = "android.movieapp.vote_average";
    public static final String EXTRA_MOVIE_RELEASE_DATE = "android.movieapp.release_date";
    public static final String EXTRA_MOVIE_BACKDROP_PATH = "android.movieapp.backdrop_path";

    public static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";

    private long id;
    private String title, posterPath, overview,releaseDate, backdropPath;
    private Float rating;

    public Movie() {}

    public Movie(Integer id) {
        this.id = id;
    }

    public Movie(long id, String title, String posterPath, Float rating, String overview, String releaseDate, String backdropPath) {
        this.title = title;
        this.id = id;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getPosterUri() {
        String uriString = BASE_POSTER_URL + "w154" + this.getPosterPath();
        return Uri.parse(uriString);
    }

    public Uri getBackdropUri() {
        String uriString = BASE_POSTER_URL + "w185" + this.getBackdropPath();
        return Uri.parse(uriString);
    }
}
