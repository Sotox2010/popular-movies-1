package com.jesussoto.android.popularmovies.movies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    public interface OnMovieTappedListener {
        void onMovieTapped(Movie movie);
    }

    private List<Movie> mMovies;

    private OnMovieTappedListener mMovieTappedListener;

    MoviesAdapter(@Nullable List<Movie> movies) {
        mMovies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_movie, parent, false);

        ViewHolder holder = new ViewHolder(itemView);

        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && mMovieTappedListener != null) {
                mMovieTappedListener.onMovieTapped(mMovies.get(position));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindMovie(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    public void replaceData(@Nullable List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void setOnMovieTappedListener(@Nullable OnMovieTappedListener listener) {
        mMovieTappedListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mPosterView;
        private TextView mTitleView;
        private TextView mRatingView;

        ViewHolder(View itemView) {
            super(itemView);

            mPosterView = itemView.findViewById(R.id.movie_poster_view);
            mTitleView = itemView.findViewById(R.id.movie_title_view);
            mRatingView = itemView.findViewById(R.id.movie_rating_view);
        }

        void bindMovie(Movie movie) {
            Picasso.with(itemView.getContext())
                    .load(WebServiceUtils.buildMoviePosterUri(movie.getPosterPath()))
                    .placeholder(R.drawable.image_placeholder)
                    .into(mPosterView);

            mTitleView.setText(movie.getTitle());
            mRatingView.setText(String.format(Locale.US, "%.1f", movie.getVoteAverage()));
        }
    }
}
