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

import de.pacheco.popularMovies.databinding.CollapsingToolbarBinding;
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
    private boolean isSetFavouriteTaskDone = false;
    private ImageView backdropPoster;
    private ToggleFavoriteTask toggleFavoriteTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CollapsingToolbarBinding binding = CollapsingToolbarBinding.inflate(getLayoutInflater());
        backdropPoster = binding.ivDetail;
        title = binding.details.tvTitle;
        poster = binding.details.ivMovieThumbnail;
        releaseDate = binding.details.tvReleaseDate;
        rating = binding.details.tvVoteAverage;
        plot = binding.details.tvPlotSynopsis;
        RecyclerView trailers = binding.details.rvTrailers;
        RecyclerView reviews = binding.details.rvReviews;
        trailers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false);
        trailers.setLayoutManager(layoutManager);
        RelatedVideosAdapter relatedVideosAdapter = new RelatedVideosAdapter(this);
        trailers.setAdapter(relatedVideosAdapter);
        reviews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        reviews.setLayoutManager(layoutManager);
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
        reviews.setAdapter(reviewsAdapter);
        fillViews(relatedVideosAdapter, reviewsAdapter);
        setContentView(binding.getRoot());
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
        Picasso.get().load(movie.getBackdropPath()).placeholder(R.drawable.ic_tmdb_logo).error(
                R.drawable.ic_tmdb_logo).into(
                backdropPoster);
        Picasso.get().load(movieInformation[2]).placeholder(R.drawable.ic_tmdb_logo).error(
                R.drawable.ic_tmdb_logo).into(poster);
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
        isFavorite = !isFavorite;
        if (isFavorite) {
            view.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
        } else {
            view.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
        }
        //TODO Thread.start verwendne damit das richtig in die DB geschrieben wird, weil
        // AsyncTask irgendwann startet nicht sofort
        toggleFavoriteTask = new ToggleFavoriteTask(view, isFavorite);
        toggleFavoriteTask.execute(movie);
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
        private final boolean makeFavourite;

        public ToggleFavoriteTask(View view, boolean isFavorite) {
            this.view = view;
            this.makeFavourite = isFavorite;
        }

        @Override
        protected Void doInBackground(Movie... params) {
            toggleFavoriteTask = null;
            Movie movie = params[0];
            if (makeFavourite) {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().insertMovie(movie);
            } else {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().deleteMovie(movie);
            }
            return null;
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
                isFavorite = true;
            } else {
                view.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
                isFavorite = false;
            }
            isSetFavouriteTaskDone = true;
        }
    }
}