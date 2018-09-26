package com.jesussoto.android.popularmovies.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.model.Movie;
import com.jesussoto.android.popularmovies.repository.MoviesRepository;

import java.util.List;

public class MoviesViewModel extends ViewModel {

    private MoviesRepository mRepository;

    private MutableLiveData<MovieFilterType> mFilteringLiveData;

    MoviesViewModel() {
        // This should be preferable injected from outside;
        mRepository = MoviesRepository.getInstance();

        mFilteringLiveData = new MutableLiveData<>();
    }

    public LiveData<MoviesUiModel> getMoviesUiModel() {
        return Transformations.map(
            Transformations.switchMap(mFilteringLiveData, this::getMoviesByFilter),
            this::constructUiModel
        );
    }

    public void setFiltering(MovieFilterType newFilter) {
        if (getFiltering() != newFilter) {
            mFilteringLiveData.setValue(newFilter);
        }
    }

    public MovieFilterType getFiltering() {
        return mFilteringLiveData.getValue();
    }

    private LiveData<Resource<List<Movie>>> getMoviesByFilter(@NonNull MovieFilterType filter) {
        return filter == MovieFilterType.POPULAR_MOVIES
                ? mRepository.loadPopularMovies()
                : mRepository.loadTopRatedMovies();
    }

    public void refreshMovies() {
        if (getFiltering() == MovieFilterType.POPULAR_MOVIES) {
            mRepository.refreshPopularMovies();
        } else {
            mRepository.refreshTopRatedMovies();
        }
    }

    private MoviesUiModel constructUiModel(Resource<List<Movie>> result) {
        List<Movie> movies = result.getData();
        boolean showLoading = result.getStatus() == Resource.Status.LOADING;
        boolean showError = result.getStatus() == Resource.Status.ERROR;

        return new MoviesUiModel(movies, showLoading, showError);
    }
}
