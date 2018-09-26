package com.jesussoto.android.popularmovies.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.MoviesResponse;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.model.Movie;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class AbstractMoviesPageKeyedDataSource extends PageKeyedDataSource<Integer, Movie> {

    @NonNull
    private WebService mService;

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    @NonNull
    MutableLiveData<Resource.Status> networkState = new MutableLiveData<>();

    @NonNull
    MutableLiveData<Resource.Status> initialLoad = new MutableLiveData<>();

    AbstractMoviesPageKeyedDataSource(@NonNull WebService service) {
        mService = service;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Movie> callback) {

        int initialPage = 1;
        Call<MoviesResponse> request = getCall(mService, initialPage);
        try {
            Response<MoviesResponse> response = request.execute();
            List<Movie> movies = response.body().getResults();
            callback.onResult(movies, null, initialPage + 1);
        } catch (IOException e) {
            e.printStackTrace();
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

        request.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call,
                                   @NonNull Response<MoviesResponse> response) {

                List<Movie> movies = response.body().getResults();
                callback.onResult(movies, params.key + 1);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    abstract Call<MoviesResponse> getCall(@NonNull WebService service, int page);
}
