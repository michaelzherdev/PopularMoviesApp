package com.example.moviesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.moviesapp.model.Movie;

import java.util.ArrayList;

/**
 * Created by mzherdev on 20.11.2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 4;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MovieDBHelper", "OnCreate called");

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " ( " +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VIDEO + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARUTY + " REAL NOT NULL, " +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAV_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME + " ( " +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VIDEO + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARUTY + " REAL NOT NULL, " +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAV_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("MovieDBHelper", "onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME);
        onCreate(db);
    }


    public Cursor getData(String tableName, long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + tableName + " where movie_id="+id+"", null );
        return res;
    }

    public boolean insertMovie  (String tableName, Long id, String title, String posterPath, String rating, String overview,String releaseDate,
                                   String backdropPath, String video, Integer popularity)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARUTY, popularity);
        db.insert(tableName, null, contentValues);
        return true;
    }

    public boolean updateMovie (String tableName, Long id, String title, String posterPath, String rating, String overview,String releaseDate,
                                  String backdropPath, String video, Integer popularity)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARUTY, popularity);
        db.update(tableName, contentValues, "id = ? ", new String[]{Long.toString(id)});
        return true;
    }

    public Integer deleteMovie (String tableName, Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName,"id = ? ", new String[] { Integer.toString(id) });
    }

    public Cursor getAllMovies(String tableName) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from " + tableName, null);
        cursor.moveToFirst();
        return cursor;
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME);
    }

}
