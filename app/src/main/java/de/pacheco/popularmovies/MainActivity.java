package de.pacheco.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import de.pacheco.popularmovies.model.Movie;
import de.pacheco.popularmovies.util.MoviesUtil;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviePosters;
    private MoviePosterAdapter moviePosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviePosters = findViewById(R.id.rv_movie_overview);
        moviePosters.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL,
                false);
        moviePosters.setLayoutManager(layoutManager);
        moviePosterAdapter = new MoviePosterAdapter();
        moviePosters.setAdapter(moviePosterAdapter);
        new FetchMoviesTask().execute();
    }
    /**
     * TODO Why do you teach us deprecated classes? This seems deprecated since november last year.
     * TODO Why is it good practice to not make an extra class for this?
     */
    public class FetchMoviesTask extends AsyncTask<Void, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {
            return MoviesUtil.getMovies();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
//            progressBar.setVisibility(View.INVISIBLE);
            moviePosterAdapter.setMovieData(movies);

//            if (movies != null) {
//                showMoviePosterView();
//                movieAdapter.setMovieData(movies);
//            } else {
////                showErrorMessage();
//            }
        }
    }
}