package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.MovieEntry;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.OpenMovieJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements LoaderCallbacks<List<String>>
        , TrailerAdapter.ListItemClickListener {

    // list of member variables of Details Activity Views
    private ImageView mShowPosterImageView;
    private TextView mShowRateTextView;
    private TextView mShowDateTextView;
    private TextView mShowTitleTextView;
    private TextView mOverviewTextView;
    private ImageButton mFavoriteButton;
    private TextView mNoReviewsMessageTextView;
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;
    private TextView mNetworkConnectionMessage;
    private TextView mNoTrailersMessage;

    private NetworkInfo networkInfo;
    private Movie movie;
    private AppDatabase mDb;
    private boolean isFavorite = true;


    private static final int MOVIE_TRAILER_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mShowPosterImageView = findViewById(R.id.show_poster_iv);
        mShowRateTextView = findViewById(R.id.show_rate_tv);
        mShowDateTextView = findViewById(R.id.show_date_tv);
        mShowTitleTextView = findViewById(R.id.tv_show_movie_title);
        mOverviewTextView = findViewById(R.id.tv_show_movie_overView);
        mNoReviewsMessageTextView = findViewById(R.id.tv_no_reviews_message);
        mFavoriteButton = findViewById(R.id.like_Button);
        mNetworkConnectionMessage = findViewById(R.id.tv_no_network_connection_message);
        mNoTrailersMessage = findViewById(R.id.tv_no_trailers_message);

        // intent to receive the selected movie .. movie is received as parcel from main activity
        final Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");

        //extract movie info
        final int movieId = movie.getMovieId();
        final Double rate = movie.getMovieRate();
        final String date = movie.getMovieDate();
        final String title = movie.getMovieTitle();
        final String overview = movie.getMovieOverview();
        final String poster = movie.getMoviePoster();

        String posterUrl = MovieAdapter.getImageUri(movie);
        Picasso.with(this)
                .load(posterUrl)
                .error(R.drawable.noimage)
                .into(mShowPosterImageView);

        //populate Ui
        mShowRateTextView.setText(Double.toString(rate));
        mShowDateTextView.setText(date);
        mShowTitleTextView.setText(title);
        mOverviewTextView.setText(overview);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // fetch data about reviews and trailers only if the mobile network is connected
        if (networkInfo != null && networkInfo.isConnected()) {
            mNetworkConnectionMessage.setVisibility(View.GONE);
            mNoTrailersMessage.setVisibility(View.GONE);

            LoaderCallbacks<List<String>> callbacks = DetailsActivity.this;
            getSupportLoaderManager().initLoader(MOVIE_TRAILER_LOADER_ID, null, callbacks);
            reviewsAsyncTask.execute();
        } else {
            mNetworkConnectionMessage.setVisibility(View.VISIBLE);
            mNoTrailersMessage.setVisibility(View.VISIBLE);
        }

        //instance of our favorite movies database
        mDb = AppDatabase.getsInstance(getApplicationContext());

        /**check if this movie is in my favorite list or not By making a query to
         the database using movieId
         notice that  : movieId is the id provided by theMovieDb API it self ,
         not the id of the movie in the database if it's exist
         **/

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final MovieEntry checkFavoriteMovieEntry = mDb.movieDao().loadMovieById(movieId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // if it dose not exist in the favorite movie table , set the flag to false and the image view to be an empty heart :L
                        if (checkFavoriteMovieEntry == null) {
                            isFavorite = false;
                            mFavoriteButton.setBackground(getDrawable(R.drawable.icon_add_favorite));
                        }
                    }
                });
            }

        });


        //dealing with favorite button
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when the button is clicked we decide what to do according to the status of isFavorite flag
                //if the flag is false it means that this movie is not in the favorite list in our database
                // so we initiate a new movieEntry based on the details we did extract before from the Movie object
                // and insert it in the movie table , then set the flag to be true and change the background image
                if (!isFavorite) {

                    final MovieEntry newMovieEntry = new MovieEntry(movieId, rate, poster, title, overview, date);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(newMovieEntry);
                        }
                    });

                    mFavoriteButton.setBackground(getDrawable(R.drawable.icon_unfavorite));
                    isFavorite = true;
                    Toast.makeText(DetailsActivity.this, "Added to your favorite list",
                            Toast.LENGTH_SHORT).show();
                    // otherwise , if the flag is true it means that this movie is already in the favorite list
                    // and the user wants to remove it from the list
                    //so we first make a query by movieId to get this movieEntry and then delete it
                } else {

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            MovieEntry movieEntry = mDb.movieDao().loadMovieById(movieId);
                            mDb.movieDao().deleteMovie(movieEntry);
                        }
                    });

                    isFavorite = false;
                    mFavoriteButton.setBackground(getDrawable(R.drawable.icon_add_favorite));
                    Toast.makeText(DetailsActivity.this, "Removed from your favorite list",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //display list of reviews horizontally
        recyclerView = findViewById(R.id.reviews_rv);
        adapter = new ReviewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //display list of trailers horizontally
        trailerRecyclerView = findViewById(R.id.trailers_rv);
        trailerAdapter = new TrailerAdapter(this, this);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        trailerRecyclerView.setLayoutManager(layoutManager);

    }

    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<String>>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Nullable
            @Override
            public List<String> loadInBackground() {

                /**** getting movie trailer using movie id ***/
                String id = Integer.toString(movie.getMovieId());
                URL trailerUrl = NetworkUtils.buildTrailerRequestUrl(id);


                String trailerJsonResponse = null;
                try {
                    trailerJsonResponse = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                } catch (IOException e1) {
                    e1.printStackTrace();

                }

                List<String> youtubeKeys;
                try {
                    youtubeKeys = OpenMovieJsonUtils.getMovieYoutubeKeyFromJson(trailerJsonResponse);

                    return youtubeKeys;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        trailerAdapter.setTrailerData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    // another asyncTask to request reviews from API using movie id
    // i know that it's not the most  efficient way to make different calls .. and not the best practice
    // so i will come back later and use one of the third-party libraries like retrofit to handle this situation
    private AsyncTask<Void, Void, List<Review>> reviewsAsyncTask = new AsyncTask<Void, Void, List<Review>>() {
        @Override
        protected List<Review> doInBackground(Void... voids) {

            String id = Integer.toString(movie.getMovieId());
            URL reviewUrl = NetworkUtils.buildReviewsRequestUrl(id);

            String reviewJsonResponse = null;

            try {
                reviewJsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Review> reviews = null;
            try {
                reviews = OpenMovieJsonUtils.getMovieReviewFromJson(reviewJsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return reviews;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews.size() == 0 || reviews == null) {
                showNoReviewsMessage();

            } else {
                showReviews();
                adapter.setReviewData(reviews);
            }
        }
    };

    void showReviews() {
        recyclerView.setVisibility(View.VISIBLE);
        mNoReviewsMessageTextView.setVisibility(View.GONE);
    }

    void showNoReviewsMessage() {
        recyclerView.setVisibility(View.GONE);
        mNoReviewsMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(String youtubeKey) {
        String videoPath = "https://www.youtube.com/watch?v=" + youtubeKey;
        Uri youTubeUri = Uri.parse(videoPath);
        Intent openYoutubeTrailerIntent = new Intent(Intent.ACTION_VIEW, youTubeUri);
        startActivity(openYoutubeTrailerIntent);
    }
}
