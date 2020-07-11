package de.pacheco.popularmovies.recycleviews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pacheco.popularmovies.MainActivity;
import de.pacheco.popularmovies.MovieDetailsActivity;
import de.pacheco.popularmovies.R;
import de.pacheco.popularmovies.model.RelatedVideo;

public class RelatedVideosAdapter extends RecyclerView.Adapter<RelatedVideoViewHolder> {
    private Activity activity;
    private List<RelatedVideo> videos;

    public RelatedVideosAdapter(MovieDetailsActivity mainActivity) {
        this.activity = mainActivity;
    }

    public void setVideosData(List<RelatedVideo> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatedVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_video_item,
                parent,
                false);
        return new RelatedVideoViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedVideoViewHolder holder, int position) {
        holder.setVideo(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos == null ? 0 : videos.size();
    }
}
