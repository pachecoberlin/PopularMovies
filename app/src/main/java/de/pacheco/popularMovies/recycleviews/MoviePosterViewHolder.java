package de.pacheco.popularMovies.recycleviews;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.pacheco.popularMovies.MovieDetailsActivity;
import de.pacheco.popularMovies.R;
import de.pacheco.popularMovies.model.Movie;

public class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ImageView imageView;
    private final Activity activity;
    private Movie movie;

    public MoviePosterViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);
        imageView = itemView.findViewById(R.id.iv_movie_poster);
        imageView.setOnClickListener(this);
        this.activity = activity;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        Picasso.get().load(movie.getFullPosterPath()).placeholder(R.drawable.ic_tmdb_logo).into(imageView);
    }

    @Override
    public void onClick(View view) {
        Intent intentToStartDetailActivity = new Intent(activity, MovieDetailsActivity.class);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie.getInformationAsArray());
        intentToStartDetailActivity.putExtra(activity.getString(R.string.movie), movie);
        activity.startActivity(intentToStartDetailActivity);
    }
}