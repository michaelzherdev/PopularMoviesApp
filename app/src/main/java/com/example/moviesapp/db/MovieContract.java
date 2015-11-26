package com.example.moviesapp.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by mzherdev on 19.11.2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTORITY = "com.example.movieapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";


     /* Inner class that defines the table contents of the weather table */
    public static final class MovieEntry implements BaseColumns {

         public static final Uri CONTENT_URI =
                 BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

         public static final String CONTENT_TYPE =
                 ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTORITY + "/" + PATH_MOVIE;
         public static final String CONTENT_ITEM_TYPE =
                 ContentResolver.CURSOR_ITEM_BASE_TYPE+  "/" + CONTENT_AUTORITY + "/" + PATH_MOVIE;

         public static final String TABLE_NAME = "movie";
         public static final String FAV_MOVIES_TABLE_NAME = "favourites";

         public static final String COLUMN_MOVIE_ID = "movie_id";
         public static final String COLUMN_TITLE = "title";
         public static final String COLUMN_RELEASE_DATE = "release_date";
         public static final String COLUMN_POSTER_PATH = "poster_path";
         public static final String COLUMN_OVERVIEW = "overview";
         public static final String COLUMN_POPULARUTY = "popularuty";
         public static final String COLUMN_VIDEO = "trailer";
         public static final String COLUMN_RATING = "vote_average";
         public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

         public static Uri buildMovieUri(long id) {
             return ContentUris.withAppendedId(CONTENT_URI, id);
         }

         public static String extractMovieId(Uri uri) {
             return uri.getLastPathSegment();
         }

     }
}
