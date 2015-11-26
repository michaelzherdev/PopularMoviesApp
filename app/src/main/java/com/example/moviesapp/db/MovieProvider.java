package com.example.moviesapp.db;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.moviesapp.model.Movie;

/**
 * Created by mzherdev on 20.11.2015.
 */
public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int MOVIES = 100;
    static final int MOVIE = 101;
    static final int FAV_MOVIE = 102;
    static final int FAV_MOVIES = 103;


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES :
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case FAV_MOVIES :
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME);
        return true;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE:
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    Log.d(LOG_TAG, "MOVIE _id." + _id);
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                Log.d(LOG_TAG, "MOVIE insert successful.");
                break;
            }
            case FAV_MOVIE:
            case FAV_MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME, null, values);
                if (_id > 0) {
                    Log.d(LOG_TAG, "FAV_MOVIE _id." + _id);
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                Log.d(LOG_TAG, "MOVIE insert successful.");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
//        movieDbHelper.getWritableDatabase()
//                .insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;

        if(selection == null) selection = "1";

        switch (match) {
            case MOVIES: {
                rowDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAV_MOVIES: {
                rowDeleted = db.delete(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
         switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                Log.d(LOG_TAG, "MOVIE query successful.");
                break;
            case MOVIES:
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                Log.d(LOG_TAG, "MOVIES query successful.");
                break;
             case FAV_MOVIE:
                 retCursor = movieDbHelper.getReadableDatabase().query(
                         MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME,
                         projection,
                         selection,
                         selectionArgs,
                         null,
                         null,
                         null
                 );
                 Log.d(LOG_TAG, "MOVIE query successful.");
                 break;
             case FAV_MOVIES:
                 retCursor = movieDbHelper.getReadableDatabase().query(
                         MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME,
                         null,
                         null,
                         null,
                         null,
                         null,
                         sortOrder
                 );
                 Log.d(LOG_TAG, "MOVIES query successful.");
                 break;
            default:
                throw new UnsupportedOperationException("Unknown url: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdated;

        if(selection == null) selection = "1";

        switch (match) {
            case MOVIE: {
                rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            case FAV_MOVIE: {
                rowUpdated = db.update(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE);

        return matcher;
    }

    @TargetApi(11)
    @Override
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
