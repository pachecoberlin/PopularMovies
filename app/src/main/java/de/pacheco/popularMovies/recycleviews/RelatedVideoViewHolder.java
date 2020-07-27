package de.pacheco.popularMovies.recycleviews;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.pacheco.popularMovies.R;
import de.pacheco.popularMovies.model.RelatedVideo;

public class RelatedVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static final String JPG_SIZE = "/0.jpg";
    public static final String YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/";
    private final Activity activity;
    private final TextView textView;
    private final ImageView thumbnail;
    private RelatedVideo video;

    public RelatedVideoViewHolder(@NonNull View itemView, Activity activity) {
        super(itemView);
        thumbnail = itemView.findViewById(R.id.iv_video);
        textView = itemView.findViewById(R.id.tv_trailer);
        thumbnail.setOnClickListener(this);
        this.activity = activity;
    }

    public void setVideo(RelatedVideo video) {
        this.video = video;
        textView.setText(video.name);
        Picasso.get().load(YOUTUBE_THUMBNAIL_URL +video.key+ JPG_SIZE).placeholder(android.R.drawable.ic_media_play).into(thumbnail);
    }

    @Override
    public void onClick(View view) {
        if (!activity.getString(R.string.youtube_site).equals(video.site)){
            return;
        }
        String id = video.key;
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        if (appIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(appIntent);
        } else if (webIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(webIntent);
        } else {
            Toast.makeText(activity, "Sorry no youtube and no browser... let me without my " +
                    "trousers", Toast.LENGTH_LONG).show();
        }
    }
}