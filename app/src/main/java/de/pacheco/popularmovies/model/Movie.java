package de.pacheco.popularmovies.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.pacheco.popularmovies.util.MoviesUtil;

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

    public String getFullPosterPath() {
        return MoviesUtil.BASE_POSTER_URL + MoviesUtil.DEFAULT_SIZE + posterPath;
    }

    public String[] getInformationAsArray() {
        return new String[]{String.valueOf(id), title, getFullPosterPath(), releaseDate,
                String.valueOf(rating), plot, originalLanguage, originalTitle};
    }
}