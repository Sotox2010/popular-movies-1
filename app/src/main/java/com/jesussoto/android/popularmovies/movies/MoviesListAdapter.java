package com.jesussoto.android.popularmovies.movies;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jesussoto.android.popularmovies.api.Resource.Status.ERROR;
import static com.jesussoto.android.popularmovies.api.Resource.Status.LOADING;

public class MoviesListAdapter extends PagedListAdapter<Movie, RecyclerView.ViewHolder> {

    interface OnMovieTappedListener {
        void onMovieTapped(Movie movie);
    }

    interface OnRetryCallback {
        void onRetry();
    }

    /**
     * There are two layout types we define in this adapter:
     * 1. Progress footer view
     * 2. Movie item view
     */
    private static final int VIEW_TYPE_PROGRESS = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Resource.Status mNetworkState;

    private OnMovieTappedListener mMovieTappedListener;

    @NonNull
    private OnRetryCallback mRetryCallback;

    MoviesListAdapter(@NonNull OnRetryCallback retryCallback) {
        super(Movie.DIFF_CALLBACK);
        mRetryCallback = retryCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PROGRESS) {
            return NetworkStateItemViewHolder.create(parent, mRetryCallback);
        }

        MovieItemViewHolder holder = MovieItemViewHolder.create(parent);
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && mMovieTappedListener != null) {
                mMovieTappedListener.onMovieTapped(getItem(position));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MovieItemViewHolder) {
            ((MovieItemViewHolder) holder).bindMovie(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindState(mNetworkState);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return hasExtraRow() && position == getItemCount() - 1
                ? VIEW_TYPE_PROGRESS : VIEW_TYPE_ITEM;
    }

    boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != Resource.Status.SUCCESS;
    }

    void setNetworkState(Resource.Status state) {
        Resource.Status previousState = mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        mNetworkState = state;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != state) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    void setOnMovieTappedListener(@Nullable OnMovieTappedListener listener) {
        mMovieTappedListener = listener;
    }

    static class MovieItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_view)
        ImageView mPosterView;

        @BindView(R.id.movie_title_view)
        TextView mTitleView;

        @BindView(R.id.movie_rating_view)
        TextView mRatingView;

        MovieItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindMovie(Movie movie) {
            if (movie.getPosterPath() != null) {
                Picasso.with(itemView.getContext())
                        .load(WebServiceUtils.buildMoviePosterUri(movie.getPosterPath()))
                        .placeholder(R.drawable.image_placeholder)
                        .into(mPosterView);
            } else {
                mPosterView.setImageResource(R.drawable.image_placeholder);
            }

            mTitleView.setText(movie.getTitle());
            mRatingView.setText(String.format(Locale.US, "%.1f", movie.getVoteAverage()));
        }

        static MovieItemViewHolder create(@NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_movie, parent, false);

            return new MovieItemViewHolder(itemView);
        }
    }

    static class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;

        @BindView(R.id.error_msg)
        TextView mErrorMessage;

        @BindView(R.id.retry_button)
        Button mRetryButton;

        NetworkStateItemViewHolder(View itemView, OnRetryCallback retryCallback) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mRetryButton.setOnClickListener(v -> retryCallback.onRetry() );
        }

        void bindState(Resource.Status networkState) {
            mProgressBar.setVisibility(networkState == LOADING ? View.VISIBLE : View.GONE);
            mRetryButton.setVisibility(networkState == ERROR ? View.VISIBLE : View.GONE);
            mErrorMessage.setVisibility(networkState == ERROR ? View.VISIBLE : View.GONE);
        }

        static NetworkStateItemViewHolder create(
                @NonNull ViewGroup parent, @NonNull OnRetryCallback retryCallback) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_network_state, parent, false);

            return new NetworkStateItemViewHolder(itemView, retryCallback);
        }
    }
}
