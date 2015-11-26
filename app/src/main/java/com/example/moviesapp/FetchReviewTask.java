package com.example.moviesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.moviesapp.model.Review;
import com.example.moviesapp.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mzherdev on 23.11.2015.
 */
public class FetchReviewTask  extends AsyncTask<String, Void, ArrayList<Review>> {

    private static final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    public static ArrayList<Review> reviews = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    private Context mContext;

    public FetchReviewTask(Context context, ArrayAdapter<String> adapter) {
        mContext = context;
        arrayAdapter = adapter;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {

        // If there's no movie id, there's nothing to look up.  Verify size of params.
        if(params.length == 0) {
            return null;
        }

        String movieId = params[0];
        Log.d(LOG_TAG, "Movie id:" + movieId);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {
            // Construct the URL for the themoviedb query
            final String baseUrl = "http://api.themoviedb.org/3/movie/";
            String apiKey = "/reviews?api_key=66a8f704086fcd10fb323422997057c1";
            URL url = new URL(baseUrl.concat(movieId).concat(apiKey));

            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream
                    inputStream;
            inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
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
            return getReviewsFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        if(reviews != null) {
            arrayAdapter.clear();
            for(Review r : reviews) {
                arrayAdapter.add(r.getContent());
            }
            arrayAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "On post execute reviewAdapter " + arrayAdapter.getCount());
        }
    }

    private ArrayList<Review> getReviewsFromJson(String jsonStr) throws JSONException {
        JSONObject jsonRootObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonRootObject.getJSONArray(Trailer.RESULT_LIST);

        Review review;

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.optString(Review.REVIEW_ID);
            String author = jsonObject.optString(Review.REVIEW_AUTHOR);
            String content = jsonObject.optString(Review.REVIEW_CONTENT);
            String url = jsonObject.optString(Review.REVIEW_URL);

            review = new Review(id, author, content, url);
            reviews.add(review);
            Log.d(LOG_TAG, id + ", " + author + ", " + url + ", " + content + "\n");
        }
        Log.d(LOG_TAG, "reviews.size " + reviews.size());
        return reviews;
    }

    public static ArrayList<Review> getReviews() {
        return reviews;
    }

    public static void cleanAdapter() {
        if(arrayAdapter != null) {
            arrayAdapter.clear();
            Log.d("Detail frag Fetch task", "Adapter cleaned. " + arrayAdapter.getCount());
        }
        reviews.clear();
    }
}
