package com.example.android.movies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.movies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //  api key , please put your api key here so the app can run correctly
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie";

    // Specify a language to query translatable fields with.
    private static final String language = "en-US";

    // A filter to include or exclude adult movies.
    private static final boolean includeAdults = false;

    //A filter to include or exclude videos.
    private static final boolean includeVideo = false ;

    //Specify the page of results to query.
    private static final int page = 1;


    // url parameters constants
    static final String API_KEY_PARAM = "api_key";
    static final String LANGUAGE_PARAM = "language";
    static final String INCLUDE_ADULT_PARAM = "include_adult";
    static final String INCLUDE_VIDEO_PARAM = "include_video";
    static final String PAGES_PARAM = "page";
    static final String VIDEO_PATH = "videos";
    static final String REVIEWS_PATH = "reviews";

    /**
     * Builds the URL used to talk to the movieDB server using a sort by parameter which specified
     * according to user preferences
     *
     * @param sortBy specify the sort order depending on user preferences it could be popular or top_rated
     * @return The URL
     */
    public static URL buildUrl (String sortBy){
        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM,language)
                .appendQueryParameter(INCLUDE_ADULT_PARAM,String.valueOf(includeAdults))
                .appendQueryParameter(INCLUDE_VIDEO_PARAM,String.valueOf(includeVideo))
                .appendQueryParameter(PAGES_PARAM,String.valueOf(page))
                .build();
        URL url = null ;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"built URL : "+url);

        return url;
    }

    /**
     * Builds the URL used to request trailers of the current movie using movie id
     *
     * @param movieId specify the id of the selected movie
     * @return The URL
     */
    public static URL buildTrailerRequestUrl (String movieId){
        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM,language)
                .build();
        URL url = null ;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"built URL : "+url);

        return url;
    }

    /**
     * Builds the URL used to request reviews of the current movie using movie id
     *
     * @param movieId specify the id of the selected movie
     * @return The URL
     */
    public static URL buildReviewsRequestUrl (String movieId){
        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM,language)
                .build();
        URL url = null ;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"built URL : "+url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }

}
