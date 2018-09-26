package com.jesussoto.android.popularmovies.api;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebService {

    /**
     * Paths for TheMovieDB web service.
     */
    String PATH_MOVIE_POPULAR = "movie/popular";
    String PATH_MOVIE_TOP_RATED = "movie/top_rated";
    String PATH_POSTER_SIZE = "w185";
    String PATH_BACKDROP_SIZE = "w500";

    String PARAM_PAGE = "page";

    /**
     * Query params for the web service.
     */
    String PARAM_API_KEY = "api_key";

    @GET(PATH_MOVIE_POPULAR)
    Single<MoviesResponse> getPopularMovies(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_POPULAR)
    Call<MoviesResponse> getPopularMoviesCall(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_TOP_RATED)
    Single<MoviesResponse> getTopRatedMovies(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_TOP_RATED)
    Call<MoviesResponse> getTopRatedMovieCall(
            @Query(PARAM_PAGE) int page);
}
