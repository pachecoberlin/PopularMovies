package de.pacheco.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviePosters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviePosters = findViewById(R.id.rv_movie_overview);
        moviePosters.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL,
                false);
        moviePosters.setLayoutManager(layoutManager);
        MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter();
        moviePosters.setAdapter(moviePosterAdapter);

        //TODO setMovie data
        moviePosterAdapter.notifyDataSetChanged();
    }
}