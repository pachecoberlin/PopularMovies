package de.pacheco.popularMovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MovieDB;
import de.pacheco.popularMovies.model.RelatedVideo;
import de.pacheco.popularMovies.model.Review;
import de.pacheco.popularMovies.recycleviews.RelatedVideosAdapter;
import de.pacheco.popularMovies.recycleviews.ReviewsAdapter;
import de.pacheco.popularMovies.util.MoviesUtil;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private ImageView poster;
    private TextView releaseDate;
    private TextView plot;
    private TextView rating;
    private Movie movie;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        title = findViewById(R.id.tv_title);
        poster = findViewById(R.id.iv_movie_thumbnail);
        releaseDate = findViewById(R.id.tv_release_date);
        rating = findViewById(R.id.tv_vote_average);
        plot = findViewById(R.id.tv_plot_synopsis);
        RecyclerView trailers = findViewById(R.id.rv_trailers);
        trailers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        trailers.setLayoutManager(layoutManager);
        RelatedVideosAdapter relatedVideosAdapter = new RelatedVideosAdapter(this);
        trailers.setAdapter(relatedVideosAdapter);
        RecyclerView reviews = findViewById(R.id.rv_reviews);
        reviews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        reviews.setLayoutManager(layoutManager);
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
        reviews.setAdapter(reviewsAdapter);
        fillViews(relatedVideosAdapter, reviewsAdapter);
    }

    @SuppressWarnings("deprecation")
    private void fillViews(RelatedVideosAdapter relatedVideosAdapter, ReviewsAdapter reviewsAdapter) {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT)) {
            return;
        }
        String movieExtra = getString(R.string.movie);
        if (!intent.hasExtra(movieExtra)) {
            return;
        }
        this.movie = intent.getParcelableExtra(movieExtra);
        String[] movieInformation = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
        if (movieInformation == null || movieInformation.length < 6) {
            return;
        }
        title.setText(movieInformation[1]);
        Picasso.get().load(movieInformation[2]).into(poster);
        releaseDate.setText(movieInformation[3].substring(0, movieInformation[3].indexOf("-")));
        String rating = movieInformation[4] + "/10";
        this.rating.setText(rating);
        plot.setText(movieInformation[5]);
        new SetFavoriteButtonTask().execute();
        new FetchTask<Review>(null, reviewsAdapter).execute(MoviesUtil.REVIEWS,
                movieInformation[0]);
        new FetchTask<RelatedVideo>(relatedVideosAdapter, null).execute(MoviesUtil.RELATED_VIDEOS,
                movieInformation[0]);
    }

    @SuppressWarnings("deprecation")
    public void toggleFavorite(View view) {
        if (isFavorite) {
            new ToggleFavoriteTask(true, view).execute(movie);
        } else {
            new ToggleFavoriteTask(false, view).execute(movie);
        }
        isFavorite = !isFavorite;
    }

    @SuppressWarnings("deprecation")
    public static class FetchTask<T> extends AsyncTask<String, Void, List<T>> {

        private String whichInfo;
        private final RelatedVideosAdapter relatedVideosAdapter;
        private final ReviewsAdapter reviewsAdapter;

        public FetchTask(RelatedVideosAdapter relatedVideosAdapter, ReviewsAdapter reviewsAdapter) {
            this.relatedVideosAdapter = relatedVideosAdapter;
            this.reviewsAdapter = reviewsAdapter;
        }

        @Override
        protected List<T> doInBackground(String... params) {
            whichInfo = params[0];
            return MoviesUtil.getMovieInformation(whichInfo, params[1]);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(List<T> list) {
            if (list != null && !list.isEmpty()) {
                if (MoviesUtil.RELATED_VIDEOS.equals(whichInfo)) {
                    relatedVideosAdapter.setVideosData((List<RelatedVideo>) list);
                } else if (MoviesUtil.REVIEWS.equals(whichInfo)) {
                    reviewsAdapter.setReviewsData((List<Review>) list);
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class ToggleFavoriteTask extends AsyncTask<Movie, Void, Void> {

        private final View view;
        private final boolean remove;

        public ToggleFavoriteTask(boolean remove, View view) {
            this.remove = remove;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Movie... params) {
            Movie movie = params[0];
            if (remove) {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().deleteMovie(movie);
            } else {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().insertMovie(movie);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (remove) {
                view.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
            } else {
                view.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class SetFavoriteButtonTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().getMovie(
                    String.valueOf(movie.id)) != null;
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            View view = findViewById(R.id.btn_favorite);
            if (exists) {
                view.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
                isFavorite=true;
            } else {
                view.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
                isFavorite=false;
            }
        }
    }
}