package com.example.bakeme.presentation.fragment;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bakeme.R;
import com.example.bakeme.app.App;
import com.example.bakeme.data.Repository;
import com.example.bakeme.model.Recipe;
import com.example.bakeme.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepFragment extends Fragment {
    // region CONSTANTS
    public static final String ARG_RECIPE_ID = "recipe_id";
    public static final String ARG_STEP_ID = "step_id";
    private static final String STATE_PLAY_WHEN_READY = "play_when_ready";
    private static final String STATE_CURRENT_WINDOW = "current_window";
    private static final String STATE_PLAYBACK_POSITION = "playback_position";
    // endregion

    // region VARIABLES
    @BindView(R.id.video_view)
    SimpleExoPlayerView playerView;
    @Nullable @BindView(R.id.description_text_view)
    TextView descriptionTextView;
    @Nullable @BindView(R.id.previous_button)
    ImageButton previousButton;
    @Nullable @BindView(R.id.next_button)
    ImageButton nextButton;

    private Recipe mRecipe;
    private Step mStep;
    private Unbinder mUnbinder;
    private SimpleExoPlayer mPlayer;
    private boolean mPlayWhenReady;
    private int mCurrentWindow;
    private long mPlaybackPosition;
    private int mCurrentVisibleStepIndex = 0;
    private Repository mRepository;
    // endregion

    // region CONSTRUCTORS
    public RecipeStepFragment() {
        // Required empty public constructor
    }
    // endregion

    // region LIFE CYCLE METHODS
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = ((App) getActivity().getApplication()).getRepository();

        initArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_PLAY_WHEN_READY)) {
                mPlayWhenReady = savedInstanceState.getBoolean(STATE_PLAY_WHEN_READY);
            }

            if (savedInstanceState.containsKey(STATE_CURRENT_WINDOW)) {
                mCurrentWindow = savedInstanceState.getInt(STATE_CURRENT_WINDOW);
            }

            if (savedInstanceState.containsKey(STATE_PLAYBACK_POSITION)) {
                mPlaybackPosition = savedInstanceState.getLong(STATE_PLAYBACK_POSITION);
            }
        }

        setupUI();
        updateUI();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }

        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }
    // endregion

    // region STATE METHODS
    @Override
    public void onSaveInstanceState(Bundle outState) {
        getPlayerCurrentState();
        outState.putBoolean(STATE_PLAY_WHEN_READY, mPlayWhenReady);
        outState.putInt(STATE_CURRENT_WINDOW, mCurrentWindow);
        outState.putLong(STATE_PLAYBACK_POSITION, mPlaybackPosition);

        super.onSaveInstanceState(outState);
    }
    // endregion

    // region PUBLIC METHODS
    public static RecipeStepFragment newInstance(int recipeId, int stepId) {
        RecipeStepFragment fragment = new RecipeStepFragment();
        Bundle args = new Bundle();

        if (recipeId > -1) {
            args.putInt(ARG_RECIPE_ID, recipeId);
        }

        if (stepId > -1) {
            args.putInt(ARG_STEP_ID, stepId);
        }

        fragment.setArguments(args);

        return fragment;
    }
    // endregion

    // region PRIVATE METHODS
    private void initArguments() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey(ARG_RECIPE_ID) && bundle.containsKey(ARG_STEP_ID)) {
                int recipeId = bundle.getInt(ARG_RECIPE_ID, -1);
                int stepId = bundle.getInt(ARG_STEP_ID, -1);

                if (recipeId > -1 && stepId > -1) {
                    mRecipe = mRepository.getRecipe(recipeId);

                    int index = 0;
                    for (Step step: mRecipe.getSteps()) {
                        if (step.getId() == stepId) {
                            mStep = step;
                            mCurrentVisibleStepIndex = index;

                            break;
                        }

                        index++;
                    }
                }
            }
        }
    }

    private void setupUI() {
        if (previousButton != null) {
            previousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurrentVisibleStepIndex--;

                    if (mCurrentVisibleStepIndex < 0) {
                        mCurrentVisibleStepIndex = 0;
                    }

                    setStep();
                }
            });
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurrentVisibleStepIndex++;

                    if (mCurrentVisibleStepIndex >= mRecipe.getSteps().size()) {
                        mCurrentVisibleStepIndex = mRecipe.getSteps().size() - 1;
                    }

                    setStep();
                }
            });
        }
    }

    private void setStep() {
        mStep = mRecipe.getSteps().get(mCurrentVisibleStepIndex);

        updateUI();
        releasePlayer();
        initializePlayer();
    }

    private void updateUI() {
        if (mRecipe != null && mStep != null) {
            getActivity().setTitle(mRecipe.getName());

            if (descriptionTextView != null) {
                descriptionTextView.setText(mStep.getDescription());
            }

            if (previousButton != null && nextButton != null) {
                previousButton.setEnabled(true);
                nextButton.setEnabled(true);

                if (mCurrentVisibleStepIndex == 0) {
                    previousButton.setEnabled(false);
                } else if (mCurrentVisibleStepIndex == mRecipe.getSteps().size() - 1) {
                    nextButton.setEnabled(false);
                }
            }
        }
    }

    private void initializePlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(mPlayer);

        mPlayer.setPlayWhenReady(mPlayWhenReady);
        mPlayer.seekTo(mCurrentWindow, mPlaybackPosition);

        if (mStep != null) {
            String path = !TextUtils.isEmpty(mStep.getVideoURL()) ? mStep.getVideoURL() : (
                    !TextUtils.isEmpty(mStep.getThumbnailURL()) ? mStep.getThumbnailURL() : null);

            if (!TextUtils.isEmpty(path)) {
                Uri uri = Uri.parse(path);
                MediaSource mediaSource = buildMediaSource(uri);
                mPlayer.prepare(mediaSource, true, false);
            }
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            getPlayerCurrentState();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void getPlayerCurrentState() {
        if (mPlayer == null) {
            return;
        }

        mPlaybackPosition = mPlayer.getCurrentPosition();
        mCurrentWindow = mPlayer.getCurrentWindowIndex();
        mPlayWhenReady = mPlayer.getPlayWhenReady();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    // endregion
}
