package com.jesussoto.android.popularmovies.repository;

import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.MoviesResponse;
import com.jesussoto.android.popularmovies.api.WebService;

import retrofit2.Call;

/**
 * Paged data source for Top-rated Movies.
 */
public class TopRatedMoviesPageKeyedDataSource extends AbstractMoviesPageKeyedDataSource {

    public TopRatedMoviesPageKeyedDataSource(@NonNull WebService service) {
        super(service);
    }

    @Override
    Call<MoviesResponse> getCall(@NonNull WebService service, int page) {
        return service.getTopRatedMovieCall(page);
    }
}
