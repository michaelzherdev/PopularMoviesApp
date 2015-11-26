package com.example.moviesapp.model;

import android.net.Uri;

/**
 * Created by mzherdev on 23.11.2015.
 */
public class Trailer {

    // These are the names of the JSON objects that need to be extracted.
    public static final String RESULT_LIST = "results";
    public static final String TRAILER_ID = "id";
    public static final String TRAILER_KEY = "key";
    public static final String TRAILER_NAME = "name"; // maybe backdrop_path
    public static final String TRAILER_SITE = "site";
    public static final String TRAILER_TYPE = "type";
    public static final String TRAILER_SIZE = "size";

    String id;
    String key;
    String name;
    String site;
    String type;
    Integer size;

    public Trailer() {}

    public Trailer(String id, String key, String name, String site, Integer size, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public static final String BASE_TRAILER_URI = "http://api.themoviedb.org/3/movie/";
    public static final String LABEL_AND_API_KEY = "/videos?api_key=66a8f704086fcd10fb323422997057c1";
    public static final String YOU_TUBE_BASE_PATH = "https://www.youtube.com/watch?v=";
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Uri getTrailerUri(String id) {
        String uriString = BASE_TRAILER_URI + id + LABEL_AND_API_KEY;
        return Uri.parse(uriString);
    }

    public static String openOnYouTube(String key) {
        return YOU_TUBE_BASE_PATH + key;
    }
}
