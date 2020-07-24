package de.pacheco.popularMovies;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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
    private List<Movie> movies = Collections.emptyList();
    private List<Movie> favorites;
    private List<Movie> populars;
    private List<Movie> topRated;
    private String selection = MoviesUtil.FAVOURITES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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
        moviePosterAdapter.setMovieData(favorites);
    }

    /**
     * If the activity is re-created, it receives the same ViewModelProvider instance that was created by the first activity.
     */
    private void setupViewModel() {
        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        moviesViewModel.getFavouriteMovies().observe(this,
                list -> {
                    favorites = list;
                    if (selection.equals(MoviesUtil.FAVOURITES)) {
                        moviePosterAdapter.notifyDataSetChanged();
                    }
                });
        moviesViewModel.getPopularMovies().observe(this,
                list -> {
                    populars = list;
                    if (selection.equals(MoviesUtil.POPULAR)) {
                        moviePosterAdapter.notifyDataSetChanged();
                    }
                });
        moviesViewModel.getTopRatedMovies().observe(this,
                list -> {
                    topRated = list;
                    if (selection.equals(MoviesUtil.TOP_RATED)) {
                        moviePosterAdapter.notifyDataSetChanged();
                    }
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

    public AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selection = MoviesUtil.FAVOURITES;
                    moviePosterAdapter.setMovieData(favorites);
                    movies = favorites;
                } else if (i == 1) {
                    selection = MoviesUtil.POPULAR;
                    moviePosterAdapter.setMovieData(populars);
                    movies = populars;
                } else {
                    selection = MoviesUtil.TOP_RATED;
                    moviePosterAdapter.setMovieData(topRated);
                    movies = topRated;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }
}