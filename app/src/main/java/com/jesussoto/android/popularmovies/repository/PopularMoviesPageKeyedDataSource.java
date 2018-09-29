package com.jesussoto.android.popularmovies.repository;

import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.MoviesResponse;
import com.jesussoto.android.popularmovies.api.WebService;

import retrofit2.Call;

/**
 * Paged data source for Popular Movies.
 */
public class PopularMoviesPageKeyedDataSource extends AbstractMoviesPageKeyedDataSource {

    public PopularMoviesPageKeyedDataSource(@NonNull WebService service) {
        super(service);
    }

    @Override
    Call<MoviesResponse> getCall(@NonNull WebService service, int page) {
        return service.getPopularMoviesCall(page);
    }
}
