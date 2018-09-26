package com.jesussoto.android.popularmovies.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull Throwable throwable, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, throwable);
    }

    public enum Status {
        LOADING, SUCCESS, ERROR
    }

    @NonNull
    private Status status;

    @Nullable
    private T data;

    @Nullable
    private Throwable throwable;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable Throwable throwable) {
        this.status = status;
        this.data = data;
        this.throwable = throwable;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
