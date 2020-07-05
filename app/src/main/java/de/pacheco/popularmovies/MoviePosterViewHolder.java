package de.pacheco.popularmovies;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.pacheco.popularmovies.model.Movie;

public class MoviePosterViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;

    public MoviePosterViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.iv_movie_poster);
    }

    public void setMovie(Movie movie) {
        Picasso.get().load(movie.getFullPosterPath()).into(imageView);
    }
}
