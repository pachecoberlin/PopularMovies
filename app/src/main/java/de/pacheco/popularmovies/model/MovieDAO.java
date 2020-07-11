package de.pacheco.popularmovies.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {
    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("Select * FROM movies")
    LiveData<List<Movie>> loadMovies();

    @Query("Select * FROM movies ORDER BY :category")
    List<Movie> loadMovies(String category);

    @Query("Select * FROM movies WHERE id = :id")
    Movie getMovie(String id);
}
