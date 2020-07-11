package de.pacheco.popularmovies.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies")
public class Movie {
    @SerializedName("vote_average")
    public float rating;

    @SerializedName("popularity")
    public float popularity;

    @SerializedName("poster_path")
    public String posterPath;

    @PrimaryKey
    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("overview")
    public String plot;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("original_language")
    public String originalLanguage;

    private String fullPosterPath;

    public String getFullPosterPath() {
        return fullPosterPath;
    }

    public void setFullPosterPath(String path) {
        this.fullPosterPath = path;
    }

    public String[] getInformationAsArray() {
        return new String[]{String.valueOf(id), title, fullPosterPath, releaseDate,
                String.valueOf(rating), plot, originalLanguage, originalTitle};
    }
}