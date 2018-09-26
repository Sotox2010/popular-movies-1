package com.jesussoto.android.popularmovies.movies;

/**
 * Used with the filter spinner in the movies grid.
 */
enum MovieFilterType {
    /**
     * Filters by popular movies in decreasing order.
     */
    POPULAR_MOVIES(0),

    /**
     * Filters by top-rated movies in decreasing order.
     */
    TOP_RATED_MOVIES(1);

    private int value;

    MovieFilterType(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    static MovieFilterType fromValue(int value) {
        switch (value) {
            case 0:
                return POPULAR_MOVIES;
            case 1:
                return TOP_RATED_MOVIES;
            default:
                throw new IllegalArgumentException("Value out of range: " + value);
        }
    }
}
