package com.jesussoto.android.popularmovies.movies;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.model.Movie;

import io.reactivex.functions.Action;

/**
 * Wrapper class that encapsulates all the data related to a paged stream of movies, like the movies
 * stream itself, the network state, and retry action.
 */
public class MoviesListing {

    private LiveData<PagedList<Movie>> mPagedList;

    private LiveData<Resource.Status> mNetworkState;

    private LiveData<Resource.Status> mInitialLoadState;

    private Action mRetryAction;

    public MoviesListing(@NonNull LiveData<PagedList<Movie>> pagedList,
                  @NonNull LiveData<Resource.Status> networkState,
                  @NonNull LiveData<Resource.Status> initialLoadState,
                  @Nullable Action retry) {

        mPagedList = pagedList;
        mNetworkState = networkState;
        mInitialLoadState = initialLoadState;
        mRetryAction = retry;
    }

    public LiveData<PagedList<Movie>> getPagedList() {
        return mPagedList;
    }

    public LiveData<Resource.Status> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<Resource.Status> getInitialLoadState() {
        return mInitialLoadState;
    }

    public Action getRetryAction() {
        return mRetryAction;
    }


}
