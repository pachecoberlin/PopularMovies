package de.pacheco.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pacheco.popularmovies.model.Movie;
import de.pacheco.popularmovies.util.MoviesUtil;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviePosters;
    private MoviePosterAdapter moviePosterAdapter;
    private ProgressBar progressBar;
    private TextView networkErrorMessage;

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
    public class FetchMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            return MoviesUtil.getMovies();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            progressBar.setVisibility(View.INVISIBLE);
            if (movies != null && !movies.isEmpty()) {
                showMoviePosterView();
                moviePosterAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}