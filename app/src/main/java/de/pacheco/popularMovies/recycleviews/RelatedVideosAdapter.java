package de.pacheco.popularMovies.recycleviews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.pacheco.popularMovies.MovieDetailsActivity;
import de.pacheco.popularMovies.R;
import de.pacheco.popularMovies.model.RelatedVideo;

public class RelatedVideosAdapter extends RecyclerView.Adapter<RelatedVideoViewHolder> {
    private final Activity activity;
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
