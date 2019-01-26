package com.example.android.movies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int mMovieId;
    private Double mMovieRate;
    private String mMoviePoster;
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieDate;

    public Movie(int movieId, Double movieRate, String moviePoster, String movieTitle, String movieOverview, String movieDate) {
        this.mMovieId = movieId;
        this.mMovieRate = movieRate;
        this.mMoviePoster = moviePoster;
        this.mMovieTitle = movieTitle;
        this.mMovieOverview = movieOverview;
        this.mMovieDate = movieDate;
    }

    public int getMovieId (){return mMovieId;}

    public Double getMovieRate() {
        return mMovieRate;
    }

    public String getMoviePoster() {
        return mMoviePoster;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public String getMovieDate() {
        return mMovieDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //write object values to parcel for storage
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieId);
        parcel.writeDouble(mMovieRate);
        parcel.writeString(mMoviePoster);
        parcel.writeString(mMovieTitle);
        parcel.writeString(mMovieOverview);
        parcel.writeString(mMovieDate);
    }

    //constructor used for parcel
    public Movie(Parcel parcel){
        //read and set saved values from parcel
        mMovieId = parcel.readInt();
        mMovieRate = parcel.readDouble();
        mMoviePoster = parcel.readString();
        mMovieTitle = parcel.readString();
        mMovieOverview = parcel.readString();
        mMovieDate = parcel.readString();
    }

    //creator - used when un-parceling our parcle (creating the object)
    public final static Creator<Movie> CREATOR = new Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[0];
        }
    };

}
