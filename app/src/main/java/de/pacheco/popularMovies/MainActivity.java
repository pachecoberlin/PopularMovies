package de.pacheco.popularMovies;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final String BUNDLE_LAYOUT = "BUNDLE_LAYOUT";
    public static final String BUNDLE_SELECTION = "Selection";

    private MoviePosterAdapter moviePosterAdapter;
    private List<Movie> movies = Collections.emptyList();
    private List<Movie> favorites;
    private List<Movie> populars;
    private List<Movie> topRated;
    private String selection = MoviesUtil.FAVOURITES;
    private GridLayoutManager layoutManager;
    private String oldSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.spinnerSortBy.setOnItemSelectedListener(getSpinnerListener());
        binding.rvMovieOverview.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, calculateNoOfColumns(),
                RecyclerView.VERTICAL, false);
        binding.rvMovieOverview.setLayoutManager(layoutManager);
        moviePosterAdapter = new MoviePosterAdapter(this);
        binding.rvMovieOverview.setAdapter(moviePosterAdapter);
        View view = binding.getRoot();
        setContentView(view);
        setupViewModel();
        //data is set within spinner listener, which is called after created
    }

    public int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_LAYOUT,
                layoutManager.onSaveInstanceState());
        outState.putString(BUNDLE_SELECTION, selection);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState);
            selection = savedInstanceState.getString(BUNDLE_SELECTION);
            setMovieData(selection);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_LAYOUT);
            layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void setMovieData(String selection) {
        if (selection == null) {
            selection = MoviesUtil.FAVOURITES;
        }
        switch (selection) {
            case MoviesUtil.POPULAR:
                moviePosterAdapter.setMovieData(populars);
                movies = populars;
                break;
            case MoviesUtil.TOP_RATED:
                moviePosterAdapter.setMovieData(topRated);
                movies = topRated;
                break;
            case MoviesUtil.FAVOURITES:
            default:
                moviePosterAdapter.setMovieData(favorites);
                movies = favorites;
                break;
        }
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
                } else if (i == 1) {
                    selection = MoviesUtil.POPULAR;
                } else {
                    selection = MoviesUtil.TOP_RATED;
                }
                if (oldSelection != null && !oldSelection.equals(selection)) {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
                setMovieData(selection);
                oldSelection = selection;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }
}