package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.AsyncTaskLoader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.MovieEntry;
import com.example.android.movies.utilities.FavoriteMoviesUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.OpenMovieJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>>
        , MovieAdapter.ListItemClickListener {

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private TextView mErrorMessageDisplay;
    private TextView mNetworkMessage;
    private TextView noFavoriteMessageDisplay;

    private String mSortByParam = "popular";

    private ProgressBar mLoadingIndicator;

    private NetworkInfo networkInfo;

    private static final int MOVIE_LOADER_ID = 0;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.loading_indicator_pb);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mNetworkMessage = findViewById(R.id.tv_no_internet_message_display);
        mRecyclerView = findViewById(R.id.movies_rv);
        noFavoriteMessageDisplay = findViewById(R.id.tv_no_favorite_movies);

        mAdapter = new MovieAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderCallbacks<List<Movie>> callbacks = MainActivity.this;
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callbacks);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mNetworkMessage.setVisibility(View.VISIBLE);
        }

        mDb = AppDatabase.getsInstance(getApplicationContext());
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> mMovies = null;

            @Override
            protected void onStartLoading() {
                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {

                URL url = NetworkUtils.buildUrl(mSortByParam);

                String jsonMovieResponse = null;
                try {
                    jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                List<Movie> simpleMovieJsonData;
                try {
                    simpleMovieJsonData = OpenMovieJsonUtils.getMovieListFromJson(jsonMovieResponse);
                    return simpleMovieJsonData;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mAdapter.setMovieData(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showMovieData();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    /**
     * This is where we receive our callback
     * <p>
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedMovie  the item that was clicked.
     */
    @Override
    public void onListItemClick(Movie clickedMovie) {

        Intent openDetailsActivityIntent = new Intent(MainActivity.this, DetailsActivity.class);
        openDetailsActivityIntent.putExtra("movie", clickedMovie);

        startActivity(openDetailsActivityIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            showMostPopularMovies();
        }

        if (id == R.id.action_sort_by_high_rated) {
            showHighRatedMovies();
        }

        if (id == R.id.action_my_favorite) {
            showFavoriteMovies();
        }

        return true;
    }

    private void showMostPopularMovies() {
        hideNoFavoriteMovieMessage();
        if (networkInfo != null && networkInfo.isConnected()) {
            mAdapter.setMovieData(null);
            mSortByParam = "popular";
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else {
            mAdapter.setMovieData(null);
            mNetworkMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showHighRatedMovies() {
        hideNoFavoriteMovieMessage();
        if (networkInfo != null && networkInfo.isConnected()) {
            mAdapter.setMovieData(null);
            mSortByParam = "top_rated";
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else {
            mAdapter.setMovieData(null);
            mNetworkMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showFavoriteMovies() {
        mNetworkMessage.setVisibility(View.INVISIBLE);
        mAdapter.setMovieData(null);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(List<MovieEntry> movieEntries) {
                //if the list has no items show a text view telling that there are no favorite movies
                if (movieEntries.size() == 0) {
                    showNoFavoriteMoviesMessage();
                    mAdapter.setMovieData(null);

                } else {
                    // first we need to convert data type from list<MovieEntry> to list<Movie> to be able to
                    // show them using our MovieAdapter
                    List<Movie> movies = FavoriteMoviesUtils.getMyFavoriteMovies(movieEntries);
                    mAdapter.setMovieData(movies);
                    hideNoFavoriteMovieMessage();
                }
            }
        });
    }

    void showMovieData() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    void showNoFavoriteMoviesMessage() {
        noFavoriteMessageDisplay.setVisibility(View.VISIBLE);
    }

    void hideNoFavoriteMovieMessage() {
        noFavoriteMessageDisplay.setVisibility(View.INVISIBLE);
    }

}
