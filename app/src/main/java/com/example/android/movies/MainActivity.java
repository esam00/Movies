package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.AsyncTaskLoader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.OpenMovieJsonUtils;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>>
        ,MovieAdapter.ListItemClickListener{
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private TextView mErrorMessageDisplay;
    private TextView mNetworkMessage;

    private String mSortByParam = "popular";

    private ProgressBar mLoadingIndicator;

    private NetworkInfo networkInfo;

    private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.loading_indicator_pb);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mNetworkMessage = findViewById(R.id.tv_no_internet_message_display);
        mRecyclerView = findViewById(R.id.movies_rv);

        mAdapter = new MovieAdapter(this,this);

        mRecyclerView.setAdapter(mAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo!=null && networkInfo.isConnected()){
            LoaderCallbacks<List<Movie>> callbacks = MainActivity.this;
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,null,callbacks);

        }else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mNetworkMessage.setVisibility(View.VISIBLE);
        }
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

        if(null==data){
            showErrorMessage();
        }else {
            showMovieData();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    void showMovieData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    void showErrorMessage (){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    /**
     * This is where we receive our callback
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedMovie Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(Movie clickedMovie) {

        Intent openDetailsActivityIntent = new Intent(MainActivity.this,DetailsActivity.class);
        openDetailsActivityIntent.putExtra("movie",clickedMovie);

        startActivity(openDetailsActivityIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_sort_by_popularity){
            if (networkInfo!=null&& networkInfo.isConnected()){
                mAdapter.setMovieData(null);
                mSortByParam = "popular";
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,null,this);
            }

        }

        if (id==R.id.action_sort_by_high_rated) {
            if (networkInfo != null && networkInfo.isConnected()) {
                mAdapter.setMovieData(null);
                mSortByParam = "top_rated";
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            }
        }
        return true;
    }
}
