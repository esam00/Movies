package com.example.android.movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    private ImageView mShowPosterImageView;
    private TextView mShowRateTextView;
    private TextView mshowDateTextView;
    private TextView mShowTitleTextView;
    private TextView mOverviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mShowPosterImageView = findViewById(R.id.show_poster_iv);
        mShowRateTextView = findViewById(R.id.show_rate_tv);
        mshowDateTextView = findViewById(R.id.show_date_tv);
        mShowTitleTextView =findViewById(R.id.tv_show_movie_title);
        mOverviewTextView = findViewById(R.id.tv_show_movie_overView);

        Intent intent =getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        String rate = Double.toString(movie.getMovieRate());
        String date = movie.getMovieDate();
        String title = movie.getMovieTitle();
        String overview = movie.getMovieOverview();
        String posterUrl = MovieAdapter.getImageUri(movie);

        Picasso.with(this)
                .load(posterUrl)
                .error(R.drawable.noimage)
                .into(mShowPosterImageView);

        mShowRateTextView.setText(rate);

        mshowDateTextView.setText(date);

        mShowTitleTextView.setText(title);

        mOverviewTextView.setText(overview);

    }
}
