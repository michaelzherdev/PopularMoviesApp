package com.example.moviesapp.model;

import android.os.Bundle;

/**
 * Created by mzherdev on 23.11.2015.
 */
public class Review {

    public static String EXTRA_REVIEW = "com.example.moviesapp.model.review";

    // These are the names of the JSON objects that need to be extracted.
    public static final String REVIEW_ID = "id";
    public static final String REVIEW_AUTHOR = "author";
    public static final String REVIEW_CONTENT = "content";
    public static final String REVIEW_URL = "url";

    String id;
    String author;
    String content;
    String url;

    public Review() { }

    public Review(Bundle bundle) {
        this.id = bundle.getString(REVIEW_ID);
        this.author = bundle.getString(REVIEW_AUTHOR);
        this.content = bundle.getString(REVIEW_CONTENT);
        this.url = bundle.getString(REVIEW_URL);
    }

    public Review(String id, String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.id = id;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(REVIEW_ID, id);
        bundle.putString(REVIEW_AUTHOR, author);
        bundle.putString(REVIEW_CONTENT, content);
        bundle.putString(REVIEW_URL, url);
        return bundle;
    }
}
