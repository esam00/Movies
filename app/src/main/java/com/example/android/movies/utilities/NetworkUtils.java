package com.example.android.movies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //  api key , please put your api key here so the app can run correctly
    private static final String API_KEY = "";

    private static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/discover/movie";

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
    static final String SORT_BY_PARAM = "sort_by";
    static final String INCLUDE_ADULT_PARAM = "include_adult";
    static final String INCLUDE_VIDEO_PARAM = "include_video";
    static final String PAGES_PARAM = "page";

    /**
     * Builds the URL used to talk to the movieDB server using a sort by parameter which specified
     * according to user preferences
     *
     * @param sortBy The location that will be queried for.
     * @return The URL
     */
    public static URL buildUrl (String sortBy){
        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM,API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM,language)
                .appendQueryParameter(SORT_BY_PARAM,sortBy)
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
