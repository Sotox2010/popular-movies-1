package com.jesussoto.android.popularmovies.moviedetail;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.model.Movie;
import com.jesussoto.android.popularmovies.widget.AlwaysEnterToolbarScrollListener;
import com.jesussoto.android.popularmovies.widget.SynchronizedScrollView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity
        implements SynchronizedScrollView.OnScrollListener {

    public static final String EXTRA_MOVIE = "extra_movie";

    public static final long ANIMATION_DURATION = 200L;

    public static void start(FragmentActivity launching, Movie movie) {
        Intent movieDetailIntent = new Intent(launching, MovieDetailActivity.class);
        movieDetailIntent.putExtra(EXTRA_MOVIE, movie);
        launching.startActivity(movieDetailIntent);
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.scroll_view)
    SynchronizedScrollView mScrollView;

    @BindView(R.id.title_container)
    ViewGroup mTitleContainer;

    @BindView(R.id.title)
    TextView mTitleView;

    @BindView(R.id.original_title)
    TextView mOriginalTitleView;

    @BindView(R.id.backdrop)
    ImageView mBackdropView;

    @BindView(R.id.poster)
    ImageView mPosterView;

    @BindView(R.id.overview_label)
    TextView mOverviewLabelView;

    @BindView(R.id.overview)
    TextView mOverviewView;

    @BindView(R.id.rating)
    TextView mRatingView;

    @BindView(R.id.release_date)
    TextView mReleaseDateView;

    @ColorInt
    private int mToolbarColor  = Color.TRANSPARENT;

    private ObjectAnimator mToolbarColorAnimator;

    private boolean mToolbarColored = false;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        if (mMovie == null) {
            Toast.makeText(this, "No movie provided, exiting...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBackdropView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            mTitleView.post(() -> {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleContainer
                        .getLayoutParams();
                lp.topMargin = mBackdropView.getHeight();
                mTitleContainer.setLayoutParams(lp);
            });
        });

        setupToolbar();

        mScrollView.addOnScrollListener(new AlwaysEnterToolbarScrollListener(mToolbar));
        mScrollView.addOnScrollListener(this);

        updateView(mMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollView.post(() -> {
            List<SynchronizedScrollView.OnScrollListener> listeners =
                    mScrollView.getOnScrollListeners();

            if (listeners != null) {
                for (SynchronizedScrollView.OnScrollListener listener : listeners) {
                    listener.onScrollChanged(0, 0, mScrollView.getScrollX(), mScrollView.getScrollY());
                }
            }
        });
    }

    /**
     * Setup toolbar properties.
     */
    private void setupToolbar() {
        if (mToolbarColor == Color.TRANSPARENT) {
            mToolbarColor = ContextCompat.getColor(this, R.color.colorPrimary);
        }

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.post(() -> mToolbar.setTitle(null));
        }

        // Init the color animator for the toolbar.
        mToolbarColorAnimator = ObjectAnimator.ofArgb(mToolbar, "backgroundColor", 0);
        mToolbarColorAnimator.setInterpolator(new LinearInterpolator());
        mToolbarColorAnimator.setAutoCancel(true);
    }

    /**
     * Update view to represent the movie details.
     *
     * @param movie {@link Movie} to update the view.
     */
    private void updateView(@NonNull Movie movie) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM YYYY", Locale.getDefault());

        if (mMovie.getPosterPath() != null) {
            Picasso.with(this)
                    .load(WebServiceUtils.buildMoviePosterUri(mMovie.getPosterPath()))
                    .placeholder(R.drawable.poster_image_placeholder)
                    .into(mPosterTarget);
        }

        if (mMovie.getBackdropPath() != null) {
            Picasso.with(this)
                    .load(WebServiceUtils.buildMovieBackdropUri(mMovie.getBackdropPath()))
                    .placeholder(R.drawable.image_placeholder)
                    .into(mBackdropView);
        }

        mTitleView.setText(movie.getTitle());
        mOriginalTitleView.setText(movie.getOriginalTitle());
        mOverviewView.setText(movie.getOverview());
        mRatingView.setText(getString(R.string.rating_format, movie.getVoteAverage()));
        mReleaseDateView.setText(dateFormatter.format(movie.getReleaseDate()));
    }

    /**
     * Callback invoked when the poster bitmap palette has been processed.
     * Here we tweak the UI based on the palette for a more immersive experience.
     *
     * @param palette The processed {@link Palette}
     */
    private void onPaletteGenerated(Palette palette) {
        Palette.Swatch darkMuted = palette.getDarkMutedSwatch();
        if (darkMuted != null) {
            mTitleContainer.setBackgroundColor(darkMuted.getRgb());
            mToolbarColor = darkMuted.getRgb();
            if (mToolbarColored) {
                mToolbar.post(() -> mToolbar.setBackgroundColor(mToolbarColor));
            }
        }

        Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
        if (lightVibrant != null) {
            mOverviewLabelView.setTextColor(lightVibrant.getRgb());
        }
    }

    @Override
    public void onScrollChanged(int left, int top, int deltaX, int deltaY) {
        // Set to half to create a nice parallax effect!
        mBackdropView.setTranslationY(top / 2);

        float computedHeight = mBackdropView.getHeight()
                - mToolbar.getHeight()
                - mToolbar.getTranslationY();

        boolean shouldColorize = top >= computedHeight;
        animateToolbar(shouldColorize);
    }

    /**
     * Animates toolbar show or hide.
     *
     * @param show whether to show the toolbar or hide it.
     */
    private void animateToolbar(boolean show) {
        if (show != mToolbarColored) {
            int startColor = show ? Color.TRANSPARENT : mToolbarColor;
            int endColor = show ? mToolbarColor : Color.TRANSPARENT;
            long duration = show ? 0L : ANIMATION_DURATION;

            mToolbarColorAnimator.setIntValues(startColor, endColor);
            mToolbarColorAnimator.setDuration(duration);
            mToolbarColorAnimator.start();
            mToolbarColored = show;
        }
    }

    // Custom Picasso target to be able to receive the poster bitmap for further color processing
    // using the Palette API.
    private Target mPosterTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mPosterView.setImageBitmap(bitmap);
            Palette.from(bitmap).generate(MovieDetailActivity.this::onPaletteGenerated);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
}
