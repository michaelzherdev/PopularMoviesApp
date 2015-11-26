package com.example.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviesapp.db.MovieContract;
import com.example.moviesapp.db.MovieDbHelper;
import com.example.moviesapp.model.Movie;
import com.example.moviesapp.model.Trailer;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.view.View.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    static ArrayAdapter<Trailer> adapter;
    static FetchTrailerTask trailerTask;

    MovieDbHelper dbHelper;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "OnCreateView");
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        adapter = new ArrayAdapter<Trailer>(getActivity(), 0);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {

            final String movieTitle = intent.getStringExtra(Intent.EXTRA_TEXT);
            TextView textView = (TextView) rootView.findViewById(R.id.title_detail_fragment);
            textView.setText(movieTitle);

            final String movieOverview = intent.getStringExtra(Movie.EXTRA_MOVIE_SYNOPSIS);
            TextView textView1 = (TextView) rootView.findViewById(R.id.overview_detail_fragment);
            textView1.setText(movieOverview);

            final String movieRating = intent.getStringExtra(Movie.EXTRA_MOVIE_RATING);
            TextView textView2 = (TextView) rootView.findViewById(R.id.rating_detail_fragment);
            textView2.setText(movieRating);

            final String moviePoster = intent.getStringExtra(Movie.EXTRA_MOVIE_POSTER);
            final String movieBackdropPath = intent.getStringExtra(Movie.EXTRA_MOVIE_BACKDROP_PATH);
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageview_detail_fragment);
            Uri uri = Uri.parse(Movie.BASE_POSTER_URL + "w185" + movieBackdropPath);
            Picasso.with(getActivity()).load(uri).into(imageView);

            final String movieReleaseDate = intent.getStringExtra(Movie.EXTRA_MOVIE_RELEASE_DATE);
            TextView textView3 = (TextView) rootView.findViewById(R.id.release_date_detail_fragment);
            textView3.setText("Release date: " + movieReleaseDate);

            final Long movieId = intent.getLongExtra(Movie.EXTRA_MOVIE_ID, 0L);
//            TextView trailerTextView = (TextView) rootView.findViewById(R.id.trailer_detail_fragment);
            ImageButton trailerTextView = (ImageButton) rootView.findViewById(R.id.trailer_detail_fragment);

            //replaced here from trailerTextView.setOnClickListener to get count of trailers (otherwise it`s allways 0 :( )
            trailerTask = new FetchTrailerTask(getContext(), adapter);
            trailerTask.execute(String.valueOf(movieId));
            trailerTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: think how to clean adapter on backButtonPressed to get only trailers for chosen film
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (adapter.getCount() != 0) {
                        Trailer trailer;
//                        if(adapter.getCount() == 1 ) {
                        Log.d(LOG_TAG, "adapter.getCount()" + adapter.getCount());
                        trailer = adapter.getItem(0);
//                        } else {
//                            trailer = adapter.getItem(adapter.getCount() - 1);
//                        }
                        intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
                        Log.d(LOG_TAG, "Trailer: " + trailer.getKey());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "No trailers for this movie.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ListView listView = (ListView) rootView.findViewById(R.id.review_adapter_listview);
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.review_adapter_list_item, R.id.review_adapter_textview);
            FetchReviewTask reviewTask = new FetchReviewTask(getContext(), adapter);
            reviewTask.execute(String.valueOf(movieId));
            listView.setAdapter(adapter);
            Log.d(LOG_TAG, "adapter get count" + adapter.getCount());



            dbHelper = new MovieDbHelper(getActivity());

            final ImageView imageViewFav = (ImageView) rootView.findViewById(R.id.imageview_favourites);
            Cursor cursor = dbHelper.getData(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME, movieId);
            cursor.moveToFirst();
            if(cursor.getCount() > 0) {
                imageViewFav.setImageResource(R.drawable.heart_filled);
                imageViewFav.setTag(R.drawable.heart_filled);
            } else {
                imageViewFav.setImageResource(R.drawable.heart);
                imageViewFav.setTag(R.drawable.heart);
            }
            Log.d(LOG_TAG, "current: " + cursor.getCount());

            imageViewFav.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageViewFav.getTag().equals(R.drawable.heart_filled)) {
                        Toast.makeText(getActivity(), "Already in favourites.", Toast.LENGTH_SHORT).show();
                    } else {
                        imageViewFav.setTag(R.drawable.heart_filled);
                        imageViewFav.setImageResource(R.drawable.heart_filled);
                        dbHelper.insertMovie(MovieContract.MovieEntry.FAV_MOVIES_TABLE_NAME, movieId, movieTitle, moviePoster, movieRating
                                    , movieOverview, movieReleaseDate, movieBackdropPath, "" , 0);
                        Toast.makeText(getActivity(), "Added to favourites.", Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG, "Insert successful.");
                    }
                    //TODO normal closing of dbHelper!
                    dbHelper.close();
                }
            });

        }
        return rootView;
    }


    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }
}
