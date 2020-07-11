package de.pacheco.popularmovies.recycleviews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pacheco.popularmovies.MovieDetailsActivity;
import de.pacheco.popularmovies.R;
import de.pacheco.popularmovies.model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private Activity activity;
    private List<Review> reviews;

    public void setReviewsData(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public ReviewsAdapter(MovieDetailsActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,
                parent,
                false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.setReview(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

}
