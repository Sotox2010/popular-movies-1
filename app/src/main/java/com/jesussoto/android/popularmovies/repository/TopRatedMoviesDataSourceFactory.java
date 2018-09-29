package com.jesussoto.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.model.Movie;

public class TopRatedMoviesDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    @NonNull
    private WebService mService;

    @NonNull
    private MutableLiveData<TopRatedMoviesPageKeyedDataSource> mSourceLiveData;

    public TopRatedMoviesDataSourceFactory(@NonNull WebService service) {
        this.mService = service;
        mSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, Movie> create() {
        TopRatedMoviesPageKeyedDataSource source = new TopRatedMoviesPageKeyedDataSource(mService);
        mSourceLiveData.postValue(source);
        return source;
    }

    @NonNull
    public LiveData<TopRatedMoviesPageKeyedDataSource> getSourceLiveData() {
        return mSourceLiveData;
    }
}
