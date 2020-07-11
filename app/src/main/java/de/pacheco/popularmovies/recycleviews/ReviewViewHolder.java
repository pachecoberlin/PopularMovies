package de.pacheco.popularmovies.recycleviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.pacheco.popularmovies.R;
import de.pacheco.popularmovies.model.Review;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_review);

    }

    public void setReview(Review review) {
        textView.setText(review.content);
    }
}
