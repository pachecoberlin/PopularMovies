package de.pacheco.popularMovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import de.pacheco.popularMovies.util.MoviesUtil;

@Entity(tableName = "movies")
public class Movie implements Parcelable {
    @SerializedName("vote_average")
    public float rating;

    @SerializedName("popularity")
    public float popularity;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("backdrop_path")
    public String backdropPath;

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

    public Movie() {
    }

    protected Movie(Parcel in) {
        rating = in.readFloat();
        popularity = in.readFloat();
        posterPath = in.readString();
        backdropPath = in.readString();
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        plot = in.readString();
        releaseDate = in.readString();
        originalLanguage = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getFullPosterPath() {
        return MoviesUtil.BASE_POSTER_URL + MoviesUtil.DEFAULT_SIZE + posterPath;
    }

    public String[] getInformationAsArray() {
        return new String[]{String.valueOf(id), title, getFullPosterPath(), releaseDate,
                String.valueOf(rating), plot, originalLanguage, originalTitle};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(rating);
        parcel.writeFloat(popularity);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(originalTitle);
        parcel.writeString(plot);
        parcel.writeString(releaseDate);
        parcel.writeString(originalLanguage);
    }

    public String getBackdropPath() {
        return MoviesUtil.BASE_POSTER_URL + MoviesUtil.W1280 + backdropPath;
    }
}