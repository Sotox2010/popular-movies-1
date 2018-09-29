package com.jesussoto.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.model.Movie;

public class PopularMoviesDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    @NonNull
    private WebService mService;

    @NonNull
    private MutableLiveData<PopularMoviesPageKeyedDataSource> mSourceLiveData;

    public PopularMoviesDataSourceFactory(@NonNull WebService service) {
        this.mService = service;
        mSourceLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, Movie> create() {
        PopularMoviesPageKeyedDataSource source = new PopularMoviesPageKeyedDataSource(mService);
        mSourceLiveData.postValue(source);
        return source;
    }

    @NonNull
    public LiveData<PopularMoviesPageKeyedDataSource> getSourceLiveData() {
        return mSourceLiveData;
    }
}
