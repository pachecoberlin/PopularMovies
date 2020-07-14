package de.pacheco.popularMovies;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import de.pacheco.popularMovies.databinding.ActivityMainBinding;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MoviesViewModel;
import de.pacheco.popularMovies.recycleviews.MoviePosterAdapter;
import de.pacheco.popularMovies.util.MoviesUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MoviePosterAdapter moviePosterAdapter;
    private ProgressBar progressBar;
    private TextView networkErrorMessage;
    private List<Movie> movies = Collections.emptyList();
    private View contents;
    private List<Movie> favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);
            ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
            progressBar = binding.pbLoadingIndicator;
            networkErrorMessage = binding.tvErrorMessageDisplay;
            contents = binding.contents;
            Spinner spinner = binding.spinnerSortBy;
            spinner.setOnItemSelectedListener(getSpinnerListener());
            binding.rvMovieOverview.setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2,
                    RecyclerView.VERTICAL, false);
            binding.rvMovieOverview.setLayoutManager(layoutManager);
            moviePosterAdapter = new MoviePosterAdapter(this);
            binding.rvMovieOverview.setAdapter(moviePosterAdapter);
            View view = binding.getRoot();
            setContentView(view);
            setupViewModel();
    }

    /**
     * If the activity is re-created, it receives the same ViewModelProvider instance that was created by the first activity.
     */
    private void setupViewModel() {
        new ViewModelProvider(this).get(MoviesViewModel.class).getMovies().observe(this,
                list -> {
                    moviePosterAdapter.setMovieData(list);
                    favorites = list;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (movies == null || itemId != R.id.sort_by_popularity && itemId != R.id.sort_by_rating) {
            return false;
        }
        sortBy(itemId);
        moviePosterAdapter.notifyDataSetChanged();
        return true;
    }

    private void sortBy(final int itemId) {
        Collections.sort(movies, (movie1, movie2) -> {
            switch (itemId) {
                case R.id.sort_by_popularity:
                    return Float.compare(movie1.popularity, movie2.popularity);
                case R.id.sort_by_rating:
                    return Float.compare(movie1.rating, movie2.rating);
            }
            Log.d(TAG, "Not supported operation id: " + itemId);
            return 0;
        });
    }

    private void showErrorMessage() {
        networkErrorMessage.setVisibility(View.VISIBLE);
        contents.setVisibility(View.INVISIBLE);
    }

    private void showMoviePosterView() {
        networkErrorMessage.setVisibility(View.INVISIBLE);
        contents.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("deprecation")
    public AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    moviePosterAdapter.setMovieData(favorites);
                    movies = favorites;
                } else {
                    new FetchMoviesTask().execute(i == 1 ? MoviesUtil.POPULAR : MoviesUtil.RATED);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class FetchMoviesTask extends AsyncTask<String, List<Movie>, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length < 1) {
                return MoviesUtil.getFirstMovies();
            }
            String criteria = params[0];
            List<Movie> movies = MoviesUtil.getFirstMovies(criteria);
            //noinspection unchecked
            publishProgress(movies);
            MoviesUtil.addAllMovies(movies, criteria);
            return movies;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(List<Movie>... values) {
            progressBar.setVisibility(View.INVISIBLE);
            showMoviePosterView();
            MainActivity.this.movies = values[0];
            moviePosterAdapter.setMovieData(movies);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            progressBar.setVisibility(View.INVISIBLE);
            if (movies != null && !movies.isEmpty()) {
                showMoviePosterView();
                MainActivity.this.movies = movies;
                moviePosterAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}