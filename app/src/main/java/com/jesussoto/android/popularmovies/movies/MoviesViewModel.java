package com.jesussoto.android.popularmovies.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.model.Movie;
import com.jesussoto.android.popularmovies.repository.MoviesRepository;

import java.util.List;

public class MoviesViewModel extends ViewModel {

    private MoviesRepository mRepository;

    private MutableLiveData<MovieFilterType> mFilteringLiveData;

    // Reference to the popular and top-rated listing, we switch them over depending on the current
    // filtering.
    private MoviesListing mPopularMoviesListing;
    private MoviesListing mTopRatedMoviesListing;

    MoviesViewModel() {
        // This repo should be preferable injected from outside using D.I.
        mRepository = MoviesRepository.getInstance();

        mFilteringLiveData = new MutableLiveData<>();
        mPopularMoviesListing = mRepository.getPopularMoviesListing();
        mTopRatedMoviesListing = mRepository.getTopRatedMoviesListing();
    }

    /**
     * Gets filtered movies paged data source live data.
     *
     * @return {@link LiveData} of movies paged stream.
     */
    public LiveData<PagedList<Movie>> getMoviesPagedList() {
        return Transformations.switchMap(mFilteringLiveData, this::getMoviesByFilter);
    }

    /**
     * Gets filtered network state live data.
     *
     * @return {@link LiveData} of network state.
     */
    public LiveData<Resource.Status> getNetworkState() {
        return Transformations.switchMap(mFilteringLiveData, this::getNetworkStateByFilter);
    }

    /**
     * Gets filtered initial load network state live data.
     *
     * @return {@link LiveData} of initial load network state.
     */
    public LiveData<Resource.Status> getInitialLoadState() {
        return Transformations.switchMap(mFilteringLiveData, this::getInitialLoadStateByFilter);
    }

    /**
     * Updates the current movie filtering.
     *
     * @param newFilter the new filter for the movies.
     */
    public void setFiltering(MovieFilterType newFilter) {
        if (getFiltering() != newFilter) {
            mFilteringLiveData.setValue(newFilter);
        }
    }

    /**
     * Retrieves the currently active movie filtering.
     *
     * @return the active filter.
     */
    public MovieFilterType getFiltering() {
        return mFilteringLiveData.getValue();
    }

    /**
     * Obtains the appropriate movies paged data source based on the given filter.
     *
     * @param filter {@link MovieFilterType} to filter the movies.
     * @return {@link LiveData} wrapping the filtered movies paged data source.
     */
    private LiveData<PagedList<Movie>> getMoviesByFilter(@NonNull MovieFilterType filter) {
        return filter == MovieFilterType.POPULAR_MOVIES
                ? mPopularMoviesListing.getPagedList()
                : mTopRatedMoviesListing.getPagedList();
    }

    /**
     * Obtains the network state based on the given filter.
     *
     * @param filter {@link MovieFilterType} to filter the state.
     * @return {@link LiveData} wrapping the network state.
     */
    private LiveData<Resource.Status> getNetworkStateByFilter(@NonNull MovieFilterType filter) {
        return filter == MovieFilterType.POPULAR_MOVIES
                ? mPopularMoviesListing.getNetworkState()
                : mTopRatedMoviesListing.getNetworkState();
    }

    /**
     * Obtains the initial load network state based on the given filter.
     *
     * @param filter {@link MovieFilterType} to filter the state.
     * @return {@link LiveData} wrapping the network state.
     */
    private LiveData<Resource.Status> getInitialLoadStateByFilter(@NonNull MovieFilterType filter) {
        return filter == MovieFilterType.POPULAR_MOVIES
                ? mPopularMoviesListing.getInitialLoadState()
                : mTopRatedMoviesListing.getInitialLoadState();
    }

    /**
     * Retries the last failed data fetch.
     */
    public void retryLastFetch() {
        try {
            if (getFiltering() == MovieFilterType.POPULAR_MOVIES) {
                mPopularMoviesListing.getRetryAction().run();
            } else {
                mTopRatedMoviesListing.getRetryAction().run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
