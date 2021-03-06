package com.example.moviesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

    private static final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    public static ArrayList<Trailer> trailers = new ArrayList<>();
    private static ArrayAdapter<Trailer> adapter;

    private Context mContext;

    public FetchTrailerTask(Context context, ArrayAdapter arrayAdapter) {
        mContext = context;
        adapter = arrayAdapter;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {

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
            String apiKey = "/videos?api_key=66a8f704086fcd10fb323422997057c1";
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
            return getTrailersFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

        @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if(trailers != null) {
            adapter.clear();
            adapter.addAll(trailers);
            Log.d(LOG_TAG, "On post execute" + adapter.getCount());
        }
    }

    private ArrayList<Trailer> getTrailersFromJson(String jsonStr) throws JSONException {
        JSONObject jsonRootObject = new JSONObject(jsonStr);
        JSONArray jsonArray = jsonRootObject.getJSONArray(Trailer.RESULT_LIST);

        Trailer trailer;

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.optString(Trailer.TRAILER_ID);
            String key = jsonObject.optString(Trailer.TRAILER_KEY);
            String name = jsonObject.optString(Trailer.TRAILER_NAME);
            String site = jsonObject.optString(Trailer.TRAILER_SITE);
            String type = jsonObject.optString(Trailer.TRAILER_TYPE);
            Integer size = Integer.parseInt(jsonObject.optString(Trailer.TRAILER_SIZE));

            trailer = new Trailer(id, key, name, site, size, type);
            trailers.add(trailer);
            Log.d(LOG_TAG, id + ", " + key + ", " + name + ", " + site + ", " + type + ", " + size);
        }
        Log.d(LOG_TAG, "" + trailers.size());
        return trailers;
    }

    public static void cleanAdapter() {
        if(adapter != null) {
            adapter.clear();
            Log.d("Detail frag Fetch task", "Adapter cleaned. " + adapter.getCount());
        }
        trailers.clear();
    }
}
