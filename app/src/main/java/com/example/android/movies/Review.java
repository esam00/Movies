package com.example.android.movies;

public class Review {

    private String mReview;
    private String mAuthor;

    public Review(String mReview, String mAouther) {
        this.mReview = mReview;
        this.mAuthor = mAouther;
    }

    public String getmReview() {
        return mReview;
    }

    public String getmAouther() {
        return mAuthor;
    }

    public void setmReview(String mReview) {
        this.mReview = mReview;
    }

    public void setmAouther(String mAouther) {
        this.mAuthor = mAouther;
    }
}
