package com.example.android.movies.utilities;

import com.example.android.movies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle JSON data.
 */
public final class OpenMovieJsonUtils {
    /**
     * This method parses JSON from a web response and returns a List of Movie objects
     *
     * @param movieJsonString JSON response from server
     *
     * @return a List of Movie objects
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */

    public static List<Movie> getMovieListFromJson (String movieJsonString) throws JSONException {
        List <Movie> movies = new ArrayList<>();

        final String RESULTS_ARRAY = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW ="overview";
        final String DATE = "release_date";
        final String MESSAGE_CODE = "cod";

        JSONObject movieJson = new JSONObject(movieJsonString);

        /* Is there an error? */
        if ( movieJson.has(MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

            JSONArray resultsArray = movieJson.getJSONArray(RESULTS_ARRAY);
            for (int i=0; i<resultsArray.length(); i++){
                JSONObject movieSingleItem = resultsArray.getJSONObject(i);

                Double rate = movieSingleItem.getDouble(VOTE_AVERAGE);
                String poster = movieSingleItem.getString(POSTER_PATH);
                String originalTitle = movieSingleItem.getString(ORIGINAL_TITLE);
                String overview = movieSingleItem.getString(OVERVIEW);
                String date = movieSingleItem.getString(DATE);

                Movie movie = new Movie(rate,poster,originalTitle,overview,date);
                movies.add(movie);
            }
        return movies;
    }

}
