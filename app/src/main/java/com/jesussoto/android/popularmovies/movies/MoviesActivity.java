package com.jesussoto.android.popularmovies.movies;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.model.Movie;
import com.jesussoto.android.popularmovies.moviedetail.MovieDetailActivity;
import com.jesussoto.android.popularmovies.widget.AlwaysEnterToolbarScrollListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesActivity extends AppCompatActivity {

    public static final String STATE_FILTERING = "state_filtering";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mMoviesRecyclerView;

    @BindView(R.id.progress)
    ProgressBar mProgressIndicator;

    @BindView(R.id.empty_container)
    ViewGroup mEmptyContainer;

    private MoviesListAdapter mAdapter;

    private AlwaysEnterToolbarScrollListener mAlwaysEnterToolbarScrollListener;

    private MoviesViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        // Restore the filter from the previous saved state (if any).
        MovieFilterType filter = (savedInstanceState == null)
                ? MovieFilterType.POPULAR_MOVIES
                : MovieFilterType.fromValue(savedInstanceState.getInt(STATE_FILTERING));

        setupToolbar();
        setupRecyclerView();

        Button emptyAction = mEmptyContainer.findViewById(R.id.empty_action);
        emptyAction.setOnClickListener(__ -> mViewModel.retryLastFetch());

        bindViewModel();
        mViewModel.setFiltering(filter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FILTERING, mViewModel.getFiltering().getValue());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the toolbar animation state by manually calling the scroll listener.
        mMoviesRecyclerView.post(() ->
            mAlwaysEnterToolbarScrollListener.onScrolled(mMoviesRecyclerView,
                mMoviesRecyclerView.computeHorizontalScrollOffset(),
                mMoviesRecyclerView.computeVerticalScrollOffset()));
    }

    /**
     * Bind to the view model to react to data changes.
     */
    private void bindViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);

        mViewModel.getMoviesPagedList().observe(
                this, pagedList -> mAdapter.submitList(pagedList));

        mViewModel.getNetworkState().observe(
                this, networkState -> mAdapter.setNetworkState(networkState));

        mViewModel.getInitialLoadState().observe(this, this::updateView);
    }

    /**
     * Set up the toolbar and the filter spinner as well.
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        // Remove the default title set by the system in favor of the filter spinner.
        mToolbar.post(() -> mToolbar.setTitle(null));

        String[] options = getResources().getStringArray(R.array.movie_filters);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_movie_filter,
                options);
        adapter.setDropDownViewResource(R.layout.spinner_item_movie_filter_dropdown);

        int verticalOffset = getResources().getDimensionPixelSize(R.dimen.spacing_medium);

        Spinner spinner = mToolbar.findViewById(R.id.toolbar_spinner);
        spinner.setDropDownVerticalOffset(verticalOffset);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.setFiltering(position == 0
                        ? MovieFilterType.POPULAR_MOVIES
                        : MovieFilterType.TOP_RATED_MOVIES);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Set up the movies grid layout and add the toolbar animation scroll listener.
     */
    private void setupRecyclerView() {
        int spanCount = getResources().getInteger(R.integer.movie_grid_span_count);
        mAlwaysEnterToolbarScrollListener = new AlwaysEnterToolbarScrollListener(mToolbar);

        mAdapter = new MoviesListAdapter(() -> mViewModel.retryLastFetch());
        mAdapter.setOnMovieTappedListener(this::navigateToMovieDetail);

        // This grid layout manager with custom span size lookup will ensure that the 'loading' view
        // takes the whole row size instead of a single cell size.
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == mAdapter.getItemCount() - 1 && mAdapter.hasExtraRow()
                        ? spanCount : 1;
            }
        });

        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setAdapter(mAdapter);
        mMoviesRecyclerView.addOnScrollListener(mAlwaysEnterToolbarScrollListener);
    }

    /**
     * Updates the view based on the UiModel state, this gets called each time new data is
     * available to display in the UI.
     *
     * @param initialLoadState with all the data to display in the UI.
     */
    private void updateView(Resource.Status initialLoadState) {
        if (initialLoadState == null) {
            return;
        }

        int listVisibility = initialLoadState == Resource.Status.SUCCESS ? View.VISIBLE : View.GONE;
        int progressVisibility = initialLoadState == Resource.Status.LOADING ? View.VISIBLE : View.GONE;
        int errorVisibility = initialLoadState == Resource.Status.ERROR ? View.VISIBLE : View.GONE;

        mMoviesRecyclerView.setVisibility(listVisibility);
        mProgressIndicator.setVisibility(progressVisibility);
        mEmptyContainer.setVisibility(errorVisibility);
    }

    /**
     * Navigate to {@link MovieDetailActivity} to show the details of the given {@link Movie}.
     *
     * @param movie the {@link Movie} to show its details.
     */
    private void navigateToMovieDetail(@NonNull Movie movie) {
        MovieDetailActivity.start(this, movie);
    }
}
