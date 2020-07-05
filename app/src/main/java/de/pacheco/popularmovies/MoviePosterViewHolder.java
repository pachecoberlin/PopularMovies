package de.pacheco.popularmovies;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class MoviePosterViewHolder extends RecyclerView.ViewHolder {
    public MoviePosterViewHolder(@NonNull View itemView) {
        super(itemView);
        ImageView imageView=itemView.findViewById(R.id.iv_movie_poster);
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }
}
