package com.jesussoto.android.popularmovies.repository;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.MoviesResponse;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.model.Movie;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class MoviesRepository {

    private static MoviesRepository sInstance;

    /**
     * Get shared instance using the singleton pattern.
     *
     * @return the shared instance of {@link MoviesRepository}.
     */
    @NonNull
    public static synchronized MoviesRepository getInstance() {
        if (sInstance == null) {
            sInstance = new MoviesRepository(WebServiceUtils.getWebService());
        }

        return sInstance;
    }

    // Web service to fetch the data from.
    private WebService mService;

    // Simple in-memory cache for popular movies.
    private MutableLiveData<Resource<List<Movie>>> mPopularMoviesLiveData;

    // Simple in-memory cache for top rated movies.
    private MutableLiveData<Resource<List<Movie>>> mTopRatedMoviesLiveData;

    public MoviesRepository(@NonNull WebService service) {
        mService = service;
    }

    /**
     * Load top-rated movies from the network if no previous fetch occurred, or return the
     * cached movies.
     *
     * @return {@link LiveData} with the result of the most popular movies network fetch.
     */
    @SuppressLint("CheckResult")
    public LiveData<Resource<List<Movie>>> loadPopularMovies() {
        if (mPopularMoviesLiveData != null
                && mPopularMoviesLiveData.getValue().getStatus() != Resource.Status.ERROR) {

            return mPopularMoviesLiveData;
        }

        if (mPopularMoviesLiveData == null) {
            mPopularMoviesLiveData = new MutableLiveData<>();
        }

        fetchPopularMovies(mPopularMoviesLiveData);
        return mPopularMoviesLiveData;
    }

    /**
     * Load top-rated movies from the network if no previous fetch occurred, or return the
     * cached movies.
     *
     * @return {@link LiveData} with the result of the top-rated movies network fetch.
     */
    public LiveData<Resource<List<Movie>>> loadTopRatedMovies() {
        if (mTopRatedMoviesLiveData != null
                && mTopRatedMoviesLiveData .getValue().getStatus() != Resource.Status.ERROR) {

            return mTopRatedMoviesLiveData;
        }

        if (mTopRatedMoviesLiveData == null) {
            mTopRatedMoviesLiveData = new MutableLiveData<>();
        }

        fetchTopRatedMovies(mTopRatedMoviesLiveData);
        return mTopRatedMoviesLiveData;
    }

    /**
     * Forces refresh of the popular movies from network.
     */
    public void refreshPopularMovies() {
        if (mPopularMoviesLiveData == null) {
            throw new IllegalStateException("Popular movies LiveData must not be null.");
        }

        fetchPopularMovies(mPopularMoviesLiveData);
    }

    /**
     * Forces refresh of the top-rated movies from network.
     */
    public void refreshTopRatedMovies() {
        if (mTopRatedMoviesLiveData == null) {
            throw new IllegalStateException("Top-rated movies LiveData must not be null.");
        }

        fetchTopRatedMovies(mTopRatedMoviesLiveData);
    }

    /**
     * Core method for fetch popular movies from the network using RxJava, placed on a separate
     * method for re-usability on first load or forced refresh.
     *
     * @param resultData the {@link LiveData} to post the result to.
     */
    @SuppressLint("CheckResult")
    private void fetchPopularMovies(@NonNull MutableLiveData<Resource<List<Movie>>> resultData) {
        mService.getPopularMovies()
                .doOnSubscribe(__ -> resultData.postValue(Resource.loading(null)))
                .map(MoviesResponse::getResults)
                .map(Resource::success)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(
                        // onSuccess
                        resultData::postValue,

                        //onError
                        throwable -> resultData.postValue(Resource.error(throwable, null))
                );
    }

    /**
     * Core method for fetch top-rated movies from the network using RxJava, placed on a separate
     * method for re-usability on first load or forced refresh.
     *
     * @param resultData the {@link LiveData} to post the result to.
     */
    @SuppressLint("CheckResult")
    private void fetchTopRatedMovies(@NonNull MutableLiveData<Resource<List<Movie>>> resultData) {
        mService.getTopRatedMovies()
                .doOnSubscribe(__ -> resultData.postValue(Resource.loading(null)))
                .map(MoviesResponse::getResults)
                .map(Resource::success)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(
                        // onSuccess
                        resultData::postValue,

                        //onError
                        throwable -> resultData.postValue(Resource.error(throwable, null))
                );
    }
}
