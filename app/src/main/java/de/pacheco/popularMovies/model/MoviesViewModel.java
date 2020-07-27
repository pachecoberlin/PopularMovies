package de.pacheco.popularMovies.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.pacheco.popularMovies.util.MoviesUtil;

public class MoviesViewModel extends AndroidViewModel {

    private static final String TAG = MoviesViewModel.class.getSimpleName();
    private LiveData<List<Movie>> favourites;
    private MutableLiveData<List<Movie>> populars;
    private MutableLiveData<List<Movie>> topRated;

    public LiveData<List<Movie>> getFavouriteMovies() {
        if (favourites == null) {
            favourites = MovieDB.getInstance(this.getApplication()).moviesDAO().loadMovies();
        }
        return favourites;
    }

    public LiveData<List<Movie>> getPopularMovies() {
        if (populars == null) {
            populars = new MutableLiveData<>();
            MoviesUtil.getMovies(MoviesUtil.POPULAR, populars);
        }
        return populars;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        if (topRated == null) {
            topRated = new MutableLiveData<>();
            MoviesUtil.getMovies(MoviesUtil.TOP_RATED, topRated);
        }
        return topRated;
    }

    public MoviesViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    protected void onCleared() {
        super.onCleared();
    }
}