package com.jesussoto.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.MoviesResponse;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.model.Movie;

import java.io.IOException;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AbstractMoviesPageKeyedDataSource extends PageKeyedDataSource<Integer, Movie> {

    @NonNull
    private WebService mService;

    // Keep a function reference for the retry event.
    private Action mRetryAction = null;

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    @NonNull
    private MutableLiveData<Resource.Status> mNetworkState;

    @NonNull
    private MutableLiveData<Resource.Status> mInitialLoad;

    AbstractMoviesPageKeyedDataSource(@NonNull WebService service) {
        mService = service;
        mNetworkState = new MutableLiveData<>();
        mInitialLoad = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Movie> callback) {

        int initialPage = 1;
        Call<MoviesResponse> request = getCall(mService, initialPage);
        mNetworkState.postValue(Resource.Status.LOADING);
        mInitialLoad.postValue(Resource.Status.LOADING);

        try {
            Response<MoviesResponse> response = request.execute();
            List<Movie> movies = response.body().getResults();
            callback.onResult(movies, null, initialPage + 1);
            mNetworkState.postValue(Resource.Status.SUCCESS);
            mInitialLoad.postValue(Resource.Status.SUCCESS);
            mRetryAction = null;
        } catch (IOException e) {
            e.printStackTrace();
            mNetworkState.postValue(Resource.Status.ERROR);
            mInitialLoad.postValue(Resource.Status.ERROR);
            mRetryAction = () -> loadInitial(params, callback);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Movie> callback) {

        // Intentionally ignored, since we only ever append to our initial load.
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Movie> callback) {

        Call<MoviesResponse> request = getCall(mService, params.key);
        mNetworkState.postValue(Resource.Status.LOADING);

        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        request.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call,
                                   @NonNull Response<MoviesResponse> response) {

                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getResults();
                    callback.onResult(movies, params.key + 1);
                    mNetworkState.postValue(Resource.Status.SUCCESS);
                    mRetryAction = null;
                } else {
                    mRetryAction = () -> loadAfter(params, callback);
                    mNetworkState.postValue(Resource.Status.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                mRetryAction = () -> loadAfter(params, callback);
                mNetworkState.postValue(Resource.Status.ERROR);
            }
        });
    }

    public LiveData<Resource.Status> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<Resource.Status> getInitialLoading() {
        return mInitialLoad;
    }

    abstract Call<MoviesResponse> getCall(@NonNull WebService service, int page);

    public void retry() {
        Action retry = mRetryAction;
        mRetryAction = null;
        if (retry != null) {
            Completable.fromAction(retry)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe();
        }
    }
}
