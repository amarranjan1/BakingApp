package com.ashalab.bakingapp.player;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ashalab.bakingapp.Constants;
import com.ashalab.bakingapp.R;
import com.ashalab.bakingapp.databinding.StepDetailActivityBinding;
import com.ashalab.bakingapp.model.Recipe;
import com.ashalab.bakingapp.model.Step;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class StepDetailActivity extends AppCompatActivity {

    private static final String TAG = StepDetailActivity.class.getSimpleName();

    private List<Step> stepList;
    private ArrayList<Recipe> recipeArrayList;
    private Recipe recipe;
    private TextView playerViewDefaultMessage;

    private Step currentStep;
    private int stepPosition;

    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateListener playbackStateListener;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    // data object we want to retain

    private OnStepChangeListener onStepChangeListener;
    private long playbackPosition = 0;


    ImageView fullscreenButton;
    boolean fullscreen = false;


    public StepDetailActivity() {
    }

    public StepDetailActivity(Step currentStep, int stepPosition) {
        this.currentStep = currentStep;
        this.stepPosition = stepPosition;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.ashalab.bakingapp.databinding.StepDetailActivityBinding binding = StepDetailActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        playerView = binding.playerView;
        TextView stepDescription = binding.stepDescription;
        Button previousStep = binding.previousStep;
        Button nextStep = binding.nextStep;
        playerViewDefaultMessage = binding.playerDefaultMessage;
        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (savedInstanceState != null) {
            this.currentStep = savedInstanceState.getParcelable("SELECTED_STEP");
            this.stepPosition = savedInstanceState.getInt("STEP_POSITION");
        }


        Bundle recipeDataBundle = getIntent().getExtras();

        recipeArrayList = new ArrayList<>();
        if (recipeDataBundle != null) {
            recipeArrayList = recipeDataBundle.getParcelableArrayList(Constants.INTENT_RECIPES);
            int recipeCurrentPosition = recipeDataBundle.getInt(Constants.RECIPE_POSITION);
            recipe = recipeArrayList.get(recipeCurrentPosition);
            stepList = recipe.getSteps();
        }


        stepDescription.setText(currentStep.getDescription());
        playbackStateListener = new PlaybackStateListener();
        initializeMediaSession();

        int lastIndex = stepList.size() - 1;

        if (currentStep.getId() == 1) previousStep.setVisibility(View.GONE);

        if (currentStep.getId().equals(stepList.get(lastIndex).getId()))
            nextStep.setVisibility(View.GONE);


        previousStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep.getId() > 1) {
                    if (exoPlayer != null) releasePlayer();
                    stepPosition--;
                    onStepChangeListener.onStepChangeClicked(stepPosition);
                } else {
                    Toast.makeText(StepDetailActivity.this, "This is the first step", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastIndex = stepList.size() - 1;
                if (currentStep.getId() < stepList.get(lastIndex).getId()) {
                    if (exoPlayer != null) releasePlayer();
                    stepPosition++;
                    onStepChangeListener.onStepChangeClicked(stepPosition);
                } else {
                    Toast.makeText(StepDetailActivity.this, "This is the Last step", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializeExoPlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || exoPlayer == null)) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //exoPlayer.setPlayWhenReady(false);
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }


    private void initializeExoPlayer() {
        if (exoPlayer == null) {
            Uri videoUrl = Uri.parse(currentStep.getVideoURL());
            if (videoUrl != null && !currentStep.getVideoURL().isEmpty()) {
                exoPlayer = new SimpleExoPlayer.Builder(StepDetailActivity.this).build();
                playerView.setPlayer(exoPlayer);
                MediaSource mediaSource = buildMediaSource(videoUrl);
                //    exoPlayer.setPlayWhenReady(playWhenReady);
                //    exoPlayer.seekTo(currentWindow, playbackPosition);
                exoPlayer.addListener(playbackStateListener);
                exoPlayer.prepare(mediaSource, false, false);
            } else {
                playerViewDefaultMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(StepDetailActivity.this, getString(R.string.app_name));
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(StepDetailActivity.this, userAgent);
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(StepDetailActivity.this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        // mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

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

    private void releasePlayer() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.stop();
            exoPlayer.removeListener(playbackStateListener);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SELECTED_STEP", currentStep);
        outState.putInt("STEP_POSITION", stepPosition);
    }

    public interface OnStepChangeListener {
        void onStepChangeClicked(int position);
    }

    private class PlaybackStateListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        exoPlayer.getCurrentPosition(), 1f);
            } else if ((playbackState == ExoPlayer.STATE_READY)) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        exoPlayer.getCurrentPosition(), 1f);
            }
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }
    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            exoPlayer.seekTo(0);
        }
    }

}

