package de.pacheco.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;
    private ImageView poster;
    private TextView releaseDate;
    private TextView plot;
    private TextView rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        title = findViewById(R.id.tv_title);
        poster = findViewById(R.id.iv_movie_thumbnail);
        releaseDate = findViewById(R.id.tv_release_date);
        rating = findViewById(R.id.tv_vote_average);
        plot = findViewById(R.id.tv_plot_synopsis);
        fillViews();
    }

    private void fillViews() {
        Intent intent = getIntent();
        if (!intent.hasExtra(Intent.EXTRA_TEXT)){
            return;
        }
        String[] movieInformations = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
        if (movieInformations==null||movieInformations.length<6){
            return;
        }
        title.setText(movieInformations[1]);
        Picasso.get().load(movieInformations[2]).into(poster);
        releaseDate.setText(movieInformations[3].substring(0,movieInformations[3].indexOf("-")));
        String rating = movieInformations[4] + "/10";
        this.rating.setText(rating);
        plot.setText(movieInformations[5]);
    }
}