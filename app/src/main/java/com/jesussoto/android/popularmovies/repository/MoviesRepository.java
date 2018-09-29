package com.jesussoto.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.model.Movie;
import com.jesussoto.android.popularmovies.movies.MoviesListing;

import java.util.concurrent.Executors;

public class MoviesRepository {

    /**
     * Default page size by TheMovieDB API.
     */
    public static final int PAGE_SIZE = 20;

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

    public MoviesRepository(@NonNull WebService service) {
        mService = service;
    }

    /**
     * Builds paged popular movies
     *
     * @return {@link MoviesListing} encapsulating all information about popular movies stream.
     */
    public MoviesListing getPopularMoviesListing() {
        PopularMoviesDataSourceFactory factory = new PopularMoviesDataSourceFactory(mService);
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Movie>> pagedList = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();

        return new MoviesListing(
                pagedList,

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getNetworkState),

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getInitialLoading),

                () -> {
                    PopularMoviesPageKeyedDataSource dataSource = factory.getSourceLiveData().getValue();
                    if (dataSource != null) {
                        dataSource.retry();
                    }
                }
        );
    }

    /**
     *
     *
     * @return {@link MoviesListing} encapsulating all information about top-rated movies stream.
     */
    public MoviesListing getTopRatedMoviesListing() {
        TopRatedMoviesDataSourceFactory factory = new TopRatedMoviesDataSourceFactory(mService);
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Movie>> pagedList = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();

        return new MoviesListing(
                pagedList,

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getNetworkState),

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getInitialLoading),

                () -> {
                    TopRatedMoviesPageKeyedDataSource dataSource = factory.getSourceLiveData().getValue();
                    if (dataSource != null) {
                        dataSource.retry();
                    }
                }
        );
    }
}

