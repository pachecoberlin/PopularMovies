package de.pacheco.popularMovies.model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("id")
    public String id;

    @SerializedName("url")
    public String url;
}
