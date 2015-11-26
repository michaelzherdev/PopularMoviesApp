package com.example.moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.moviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mzherdev on 17.11.2015.
 *
 * 23.11: this class is not needed anymore for programm and is used no where
 * in the code since MovieAdapter is used.
 * MovieAdapter is used for saving and retreiving movie data in/from SQLite.
 *
 * Left this class for
 */
public class ImageAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Movie> mMovies;

    public ImageAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if(mMovies.size() < 20) {
            return mMovies.size();
        }
        return 20;
    }

    @Override
    public Movie getItem(int position) {
        if(position < 0 || position >= mMovies.size()) {
            return null;
        }
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        Movie movie = getItem(position);
        if(movie == null) {
            return 0;
        }
        return movie.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView ;

        if(convertView == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            );
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(params));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = getItem(position);

        if (movie == null) {
            Log.d("ImageAdapter", "movie == null");
//            imageView.setImageResource(mImages[position]);
            return null;
        } else {
            Log.d("ImageAdapter", "movie != null: " + movie.getId() + movie.getTitle()
                    + movie.getPosterPath() + movie.getReleaseDate());
            Picasso.with(mContext).load(movie.getPosterUri()).into(imageView);
        }
        return imageView;
    }

    public void addAll(ArrayList<Movie> movies) {
        for(int i =0; i < movies.size(); i++) {
            mMovies.addAll(movies);
        }
        notifyDataSetChanged();
    }

    public void cleanAdapter() {
        mMovies.clear();
    }

}
