package de.pacheco.popularMovies.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

    @SuppressWarnings("deprecation")
    public LiveData<List<Movie>> getPopularMovies() {
        if (populars == null) {
            populars = new MutableLiveData<>();
            new FetchMoviesTask().execute(MoviesUtil.POPULAR);
        }
        return populars;
    }

    @SuppressWarnings("deprecation")
    public LiveData<List<Movie>> getTopRatedMovies() {
        if (topRated == null) {
            topRated = new MutableLiveData<>();
            new FetchMoviesTask().execute(MoviesUtil.TOP_RATED);
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

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class FetchMoviesTask extends AsyncTask<String, List<Movie>, List<Movie>> {

        private String criteria;

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length < 1) {
                return MoviesUtil.getFirstMovies();
            }
            criteria = params[0];
            List<Movie> movies = MoviesUtil.getFirstMovies(criteria);
            //noinspection unchecked
            publishProgress(movies);
            MoviesUtil.addAllMovies(movies, criteria);
            return movies;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(List<Movie>... values) {
            if (MoviesUtil.POPULAR.equals(criteria)) {
                populars.setValue(values[0]);
            } else if (MoviesUtil.TOP_RATED.equals(criteria)) {
                topRated.setValue(values[0]);
            } else {
                Log.e(TAG, "Unexpected criteria");
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                if (MoviesUtil.POPULAR.equals(criteria)) {
                    populars.setValue(movies);
                } else if (MoviesUtil.TOP_RATED.equals(criteria)) {
                    topRated.setValue(movies);
                } else {
                    Log.e(TAG, "Unexpected criteria");
                }
            } else {
                Toast.makeText(MoviesViewModel.this.getApplication(), "Network connection lost",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
