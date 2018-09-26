package com.jesussoto.android.popularmovies.movies;

import com.jesussoto.android.popularmovies.model.Movie;

import java.util.List;

class MoviesUiModel {

    private List<Movie> mMovies;

    private boolean mIsProgressVisible;

    private boolean mIsErrorVisible;

    MoviesUiModel(List<Movie> movies, boolean isProgressVisible, boolean isErrorVisible) {
        this.mMovies = movies;
        this.mIsProgressVisible = isProgressVisible;
        this.mIsErrorVisible = isErrorVisible;
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    public boolean isProgressVisible() {
        return mIsProgressVisible;
    }

    public boolean isErrorVisible() {
        return mIsErrorVisible;
    }
}
