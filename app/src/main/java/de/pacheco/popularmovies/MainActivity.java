package de.pacheco.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.pacheco.popularmovies.model.Movie;
import de.pacheco.popularmovies.util.MoviesUtil;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView moviePosters;
    private MoviePosterAdapter moviePosterAdapter;
    private ProgressBar progressBar;
    private TextView networkErrorMessage;
    private List<Movie> movies = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.pb_loading_indicator);
        networkErrorMessage = findViewById(R.id.tv_error_message_display);
        moviePosters = findViewById(R.id.rv_movie_overview);
        moviePosters.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2,
                RecyclerView.VERTICAL, false);
        moviePosters.setLayoutManager(layoutManager);
        moviePosterAdapter = new MoviePosterAdapter();
        moviePosters.setAdapter(moviePosterAdapter);
        new FetchMoviesTask().execute();
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
        moviePosterAdapter.setMovieData(movies);
        return true;
    }

    private void sortBy(final int itemId) {
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                switch (itemId) {
                    case R.id.sort_by_popularity:
                        return Float.compare(movie1.popularity, movie2.popularity);
                    case R.id.sort_by_rating:
                        return Float.compare(movie1.rating, movie2.rating);
                }
                Log.d(TAG, "Not supported operation id: " + itemId);
                return 0;
            }
        });
    }

    private void showErrorMessage() {
        networkErrorMessage.setVisibility(View.VISIBLE);
        moviePosters.setVisibility(View.INVISIBLE);
    }

    private void showMoviePosterView() {
        networkErrorMessage.setVisibility(View.INVISIBLE);
        moviePosters.setVisibility(View.VISIBLE);
    }

    /**
     * TODO Why do you teach us deprecated classes? This seems deprecated since november last year.
     * TODO Why is it good practice to not make an extra class for this?
     */
    public class FetchMoviesTask extends AsyncTask<Void, List<Movie>, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            List<Movie> movies = MoviesUtil.getFirstMovies();
            publishProgress(movies);
            MoviesUtil.addAllMovies(movies);
            return movies;
        }

        @Override
        protected void onProgressUpdate(List<Movie>... values) {
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