package com.example.android.movies.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class MovieEntry {
    @PrimaryKey(autoGenerate = true)
    private int id ;
    private int mMovieId;
    private Double mMovieRate;
    private String mMoviePoster;
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieDate;

    @Ignore
    public MovieEntry(int mMovieId, Double mMovieRate, String mMoviePoster, String mMovieTitle, String mMovieOverview, String mMovieDate) {
        this.mMovieId = mMovieId;
        this.mMovieRate = mMovieRate;
        this.mMoviePoster = mMoviePoster;
        this.mMovieTitle = mMovieTitle;
        this.mMovieOverview = mMovieOverview;
        this.mMovieDate = mMovieDate;
    }

    public MovieEntry(int id, int mMovieId, Double mMovieRate, String mMoviePoster, String mMovieTitle, String mMovieOverview, String mMovieDate) {
        this.id = id;
        this.mMovieId = mMovieId;
        this.mMovieRate = mMovieRate;
        this.mMoviePoster = mMoviePoster;
        this.mMovieTitle = mMovieTitle;
        this.mMovieOverview = mMovieOverview;
        this.mMovieDate = mMovieDate;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return mMovieId;
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public void setMovieRate(Double mMovieRate) {
        this.mMovieRate = mMovieRate;
    }

    public void setMoviePoster(String mMoviePoster) {
        this.mMoviePoster = mMoviePoster;
    }

    public void setMovieTitle(String mMovieTitle) {
        this.mMovieTitle = mMovieTitle;
    }

    public void setMovieOverview(String mMovieOverview) {
        this.mMovieOverview = mMovieOverview;
    }

    public void setMovieDate(String mMovieDate) {
        this.mMovieDate = mMovieDate;
    }
}
