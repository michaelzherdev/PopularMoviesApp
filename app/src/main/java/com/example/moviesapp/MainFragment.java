package com.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.moviesapp.db.MovieContract;
import com.example.moviesapp.model.Movie;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;


public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

//    ImageAdapter adapter;
    private static final int MOVIE_LOADER = 0;
    private MovieAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview_main);
        adapter = new MovieAdapter(getContext(), null, 0);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieAdapter movieAdapter = (MovieAdapter) parent.getAdapter();
                Movie selectedMovie = movieAdapter.getMovie(position);

                Long movieId = selectedMovie.getId();
                String movieTitle = selectedMovie.getTitle();
                String movieOverview = selectedMovie.getOverview();
                String movieRating = selectedMovie.getRating().toString();
                String moviePoster = selectedMovie.getPosterPath();
                String movieReleaseDate = selectedMovie.getReleaseDate();
                String movieBackdropPath = selectedMovie.getBackdropPath();
                Log.d("MainFragment", "MainFragment backdrop " + movieBackdropPath);

                if (isTablet(getActivity()) && Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d("Main Fragment", "This is tablet on landscape!");
                    Intent intent = new Intent(getActivity(), MainActivity.class)
                            .putExtra(Movie.EXTRA_MOVIE_ID, movieId)
                            .putExtra(Intent.EXTRA_TEXT, movieTitle)
                            .putExtra(Movie.EXTRA_MOVIE_SYNOPSIS, movieOverview)
                            .putExtra(Movie.EXTRA_MOVIE_POSTER, moviePoster)
                            .putExtra(Movie.EXTRA_MOVIE_RELEASE_DATE, movieReleaseDate)
                            .putExtra(Movie.EXTRA_MOVIE_BACKDROP_PATH, movieBackdropPath)
                            .putExtra(Movie.EXTRA_MOVIE_RATING, movieRating);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Movie.EXTRA_MOVIE_ID, movieId)
                            .putExtra(Intent.EXTRA_TEXT, movieTitle)
                            .putExtra(Movie.EXTRA_MOVIE_SYNOPSIS, movieOverview)
                            .putExtra(Movie.EXTRA_MOVIE_POSTER, moviePoster)
                            .putExtra(Movie.EXTRA_MOVIE_RELEASE_DATE, movieReleaseDate)
                            .putExtra(Movie.EXTRA_MOVIE_BACKDROP_PATH, movieBackdropPath)
                            .putExtra(Movie.EXTRA_MOVIE_RATING, movieRating);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateMovieList() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String sortBy = preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
        movieTask.execute(sortBy);
    }

    // LoaderManager.LoaderCallbacks methods
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String movieSortBy = preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(id);
        String sortOrder = "desc";
        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }



    public static boolean isTablet(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density  = context.getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        return dpWidth >= MainActivity.TABLET_DP;
    }

}
