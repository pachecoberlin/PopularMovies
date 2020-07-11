package de.pacheco.popularmovies.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MoviesViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public LiveData<List<Movie>> getMovies() {
        if (movies ==null){
            movies = MovieDB.getInstance(this.getApplication()).moviesDAO().loadMovies();
        }
        return movies;
    }

    public MoviesViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
