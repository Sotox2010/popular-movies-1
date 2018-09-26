package com.jesussoto.android.popularmovies.api;

import com.google.gson.annotations.SerializedName;
import com.jesussoto.android.popularmovies.model.Movie;

import java.util.List;

public class MoviesResponse {

    @SerializedName("page")
    private Integer page;

    @SerializedName("total_results")
    private Integer totalResults;

    @SerializedName("total_pages")
    private Integer totalPages;

    @SerializedName("results")
    private List<Movie> results;

    public Integer getPage() {
        return page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }
}
