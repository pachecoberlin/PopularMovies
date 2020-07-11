package de.pacheco.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.pacheco.popularmovies.model.RelatedVideo;
import de.pacheco.popularmovies.model.Review;
import de.pacheco.popularmovies.recycleviews.RelatedVideosAdapter;
import de.pacheco.popularmovies.recycleviews.ReviewsAdapter;
import de.pacheco.popularmovies.util.MoviesUtil;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private ImageView poster;
    private TextView releaseDate;
    private TextView plot;
    private TextView rating;
    private RelatedVideosAdapter relatedVideosAdapter;
    private ReviewsAdapter reviewsAdapter;

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
        relatedVideosAdapter = new RelatedVideosAdapter(this);
        trailers.setAdapter(relatedVideosAdapter);
        RecyclerView reviews = findViewById(R.id.rv_reviews);
        reviews.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        reviews.setLayoutManager(layoutManager);
        reviewsAdapter = new ReviewsAdapter(this);
        reviews.setAdapter(reviewsAdapter);
        fillViews();
    }

    private void fillViews() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT)) {
            return;
        }
        String[] movieInformations = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
        if (movieInformations == null || movieInformations.length < 6) {
            return;
        }
        title.setText(movieInformations[1]);
        Picasso.get().load(movieInformations[2]).into(poster);
        releaseDate.setText(movieInformations[3].substring(0, movieInformations[3].indexOf("-")));
        String rating = movieInformations[4] + "/10";
        this.rating.setText(rating);
        plot.setText(movieInformations[5]);
        new FetchTask<Review>().execute(MoviesUtil.REVIEWS, movieInformations[0]);
        new FetchTask<RelatedVideo>().execute(MoviesUtil.RELATED_VIDEOS, movieInformations[0]);
    }

    public class FetchTask<T> extends AsyncTask<String, Void, List<T>> {

        private String whichInfo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
}