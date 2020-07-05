package de.pacheco.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("vote_average")
    public float rating;

    @SerializedName("popularity")
    public float popularity;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("id")
    public int id;

    @SerializedName("original_title")
    public String title;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public String release_date;

    @SerializedName("original_language")
    public String original_language;

    private String fullPosterPath;

    public String getFullPosterPath() {
        return fullPosterPath;
    }

    public void setFullPosterPath(String path) {
        this.fullPosterPath = path;
    }
}