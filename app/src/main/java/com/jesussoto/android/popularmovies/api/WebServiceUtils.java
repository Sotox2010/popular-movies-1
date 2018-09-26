package com.jesussoto.android.popularmovies.api;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceUtils {

    private WebServiceUtils() { }

    private static WebService sWebService = null;

    @NonNull
    public static synchronized WebService getWebService() {
        if (sWebService == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor());

            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder.addInterceptor(loggingInterceptor);
            }

            sWebService = new Retrofit.Builder()
                    .baseUrl(BuildConfig.TMDB_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build()
                    .create(WebService.class);
        }

        return sWebService;
    }

    @NonNull
    public static Uri buildMoviePosterUri(@NonNull String moviePosterPath) {
        return Uri.parse(BuildConfig.TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(WebService.PATH_POSTER_SIZE)
                .appendPath(moviePosterPath.substring(1))
                .build();
    }

    @NonNull
    public static Uri buildMovieBackdropUri(@NonNull String movieBackdropPath) {
        return Uri.parse(BuildConfig.TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(WebService.PATH_BACKDROP_SIZE)
                .appendPath(movieBackdropPath.substring(1))
                .build();
    }
}
