package de.pacheco.popularMovies;

import de.pacheco.popularMovies.databinding.CollapsingToolbarBinding;
import de.pacheco.popularMovies.model.Movie;
import de.pacheco.popularMovies.model.MovieDB;
import de.pacheco.popularMovies.model.ReviewResults;
import de.pacheco.popularMovies.model.TrailerResults;
import de.pacheco.popularMovies.recycleviews.RelatedVideosAdapter;
import de.pacheco.popularMovies.recycleviews.ReviewsAdapter;
import de.pacheco.popularMovies.util.MoviesUtil;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private ImageView poster;
    private TextView releaseDate;
    private TextView plot;
    private TextView rating;
    private Movie movie;
    private boolean isFavorite = false;
    private ImageView backdropPoster;

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

    private void fetchReviewsAndTrailers(int movieID, ReviewsAdapter reviewsAdapter, RelatedVideosAdapter relatedVideosAdapter) {
        Call<ReviewResults> reviewResultsCall = MoviesUtil.getReviews(movieID);
        reviewResultsCall.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                reviewsAdapter.setReviewsData(response.body().results);
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
        Call<TrailerResults> trailerResultsCall = MoviesUtil.getTrailer(movieID);
        trailerResultsCall.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Call<TrailerResults> call, Response<TrailerResults> response) {
                relatedVideosAdapter.setVideosData(response.body().results);
            }

            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

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
        //noinspection deprecation
        new SetFavoriteButtonTask().execute();
        fetchReviewsAndTrailers(movie.id, reviewsAdapter, relatedVideosAdapter);
    }

    public void toggleFavorite(View view) {
        isFavorite = !isFavorite;
        if (isFavorite) {
            view.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
        } else {
            view.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
        }
        //noinspection deprecation
        new ToggleFavoriteTask(isFavorite).execute(movie);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    public class ToggleFavoriteTask extends AsyncTask<Movie, Void, Void> {

        private final boolean makeFavourite;

        public ToggleFavoriteTask(boolean isFavorite) {
            this.makeFavourite = isFavorite;
        }

        @Override
        protected Void doInBackground(Movie... params) {
            Movie movie = params[0];
            if (makeFavourite) {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().insertMovie(movie);
            } else {
                MovieDB.getInstance(MovieDetailsActivity.this).moviesDAO().deleteMovie(movie);
            }
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
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
        }
    }
}