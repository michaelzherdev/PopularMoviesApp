package com.example.moviesapp;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.OrientationHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moviesapp.db.MovieContract;
import com.example.moviesapp.model.Movie;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by mzherdev on 20.11.2015.
 */
public class MovieAdapter extends CursorAdapter {


    static int width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
    int height = (int) Math.round(width * 1.503);


    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movie_adapter_imageview, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Cursor adapterCursor = (Cursor)this.getItem(cursor.getPosition());
        String path = adapterCursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        Movie movie = this.getMovie(cursor.getPosition());

        ImageView imageView ;

        if(view == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            );
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(params));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) view;
        }


        Picasso.with(context).load(movie.getPosterUri())
                .resize(width, height)
//                .memoryPolicy(MemoryPolicy.NO_STORE)
                .into(imageView);

    }

    public Movie getMovie(int position) {
        Cursor cursor = getCursor();
        Movie movie = new Movie();;
        if(cursor.moveToPosition(position)) {
            movie.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            movie.setRating(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
        }
        return movie;
    }
}
