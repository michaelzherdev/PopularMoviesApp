package com.example.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.moviesapp.db.MovieContract;
import com.example.moviesapp.db.MovieDbHelper;
import com.example.moviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by mzherdev on 20.11.2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    ArrayList<Movie> movies = new ArrayList<Movie>();

//    private ImageAdapter adapter;
    private final Context mContext;

    public FetchMovieTask(Context c) {
        this.mContext = c;
    }

//    public FetchMovieTask(Context c, ImageAdapter imageAdapter) {
//        this.adapter = imageAdapter;
//        this.mContext = c;
//    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        //Preferences for SettingActivity
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortByFromSharedPrefs = preferences.getString(
                mContext.getString(R.string.pref_sort_by_key),
                mContext.getString(R.string.pref_sort_by_popularity));
        String sortOrderFromSharedPrefs = preferences.getString(
                mContext.getString(R.string.pref_sort_order_key),
                mContext.getString(R.string.pref_sort_order_desc));

        Log.d(LOG_TAG, "sort by value: " + sortByFromSharedPrefs);

        URL url = null;

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);

        if(sortByFromSharedPrefs.equals("favorites")) {
            clearDb(dbHelper);
            return showFavouriteMovies();
        }
        else if(sortByFromSharedPrefs.equals("top_rated")) {
            Log.d(LOG_TAG, "top rated");
            try {
                url = new URL("https://api.themoviedb.org/3/movie/top_rated?api_key=66a8f704086fcd10fb323422997057c1");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            // Construct the URL for the themoviedb query
            final String baseUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=";
            Log.d(LOG_TAG, "SORT BY: " + sortByFromSharedPrefs);
            String sortBy = sortByFromSharedPrefs;
            String sortOrder = sortOrderFromSharedPrefs;
            String apiKey = "&api_key=66a8f704086fcd10fb323422997057c1";
            try {
                url = new URL(baseUrl.concat(sortBy).concat(".")
                        .concat(sortOrder).concat(apiKey));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

            try {


                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                clearDb(dbHelper);

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                jsonStr = buffer.toString();
                Log.d("JSON STRING", jsonStr);

            } catch (IOException e) {
                Log.e("Main Fragment", "Error" + e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Main Fragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        return null;
    }

//    @Override
//    protected void onPostExecute(ArrayList<Movie> movies) {
//        if(movies != null) {
//            adapter.cleanAdapter();
//            adapter.addAll(movies);
//            Log.d(LOG_TAG, "On post execute");
//        }
//    }


    private ArrayList<Movie> getMovieDataFromJson(String jsonStr) throws JSONException {
        JSONObject jsonRootObject = new JSONObject(jsonStr);
        JSONArray jsonArray= jsonRootObject.getJSONArray(Movie.RESULT_LIST);

        Movie movie;
        // Insert the new weather information into the database
        Vector<ContentValues> cvVector = new Vector<>(movies.size());
        ContentValues contentValues = null;

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = Integer.parseInt(jsonObject.optString(Movie.MOVIE_ID).toString());
            String title = jsonObject.optString(Movie.MOVIE_TITLE);
            String posterPath = jsonObject.optString(Movie.MOVIE_POSTER);
            float rating = Float.parseFloat(jsonObject.optString(Movie.MOVIE_RATING));
            String overview = jsonObject.optString(Movie.MOVIE_SYNOPSIS);
            String releaseDate = jsonObject.optString(Movie.MOVIE_RELEASE_DATE);
            String backdropPath = jsonObject.optString(Movie.MOVIE_BACKDROP_PATH);

            movie = new Movie(id, title, posterPath, rating, overview, releaseDate, backdropPath);
            movies.add(movie);

            // Insert the new movie information into the database
            // adding Movie to ContentProvider (MovieProvider)
            contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, "");
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARUTY, 0);

            cvVector.add(contentValues);
        }

        int inserted = 0;
        int deleted = 0;
        if(cvVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(
                    MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }


        Log.d(LOG_TAG, "FetchMovieTask complete. " + inserted + " inserted, " + deleted + " deleted.");
        return movies;
    }


    private ArrayList<Movie> showFavouriteMovies() {
        ArrayList<Movie> movies = new ArrayList<>();
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);

        clearDb(dbHelper);

        // Insert the new weather information into the database
        Vector<ContentValues> cvVector = new Vector<>(movies.size());
        Cursor cursor = dbHelper.getAllMovies(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME);
        for(int i = 0; i < cursor.getCount(); i++) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
            movie.setRating(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movies.add(movie);



            // Insert the new movie information into the database
            // adding Movie to ContentProvider (MovieProvider)
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, "");
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARUTY, 0);

            cvVector.add(contentValues);
        }

        int inserted = 0;
        if(cvVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(
                    MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Favorites movies count: " + movies.size() + "; inserted: " + inserted);
        return movies;
    }

    // dummy clean of tables
    private void clearDb(MovieDbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + MovieContract.MovieEntry.TABLE_NAME);
        db.close();
    }
}
