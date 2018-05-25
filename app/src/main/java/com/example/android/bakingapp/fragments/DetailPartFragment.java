package com.example.android.bakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.activity.RecipeStepDetailActivity;
import com.example.android.bakingapp.model.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

public class DetailPartFragment extends Fragment implements ExoPlayer.EventListener{

    private Step mStep;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ImageView thumbnailImage;

    private static final String TAG = RecipeStepDetailActivity.class.getSimpleName();
    private static final String ARG_PAGE  = "ARG_PAGE";
    private static final String ARG_STEPS = "ARG_STEPS";
    private static final String STATE_KEY_STEP_DETAIL = "state_key_step_detail";



    public static DetailPartFragment newInstance(int page, Step step)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putParcelable(ARG_STEPS, step);
        DetailPartFragment fragment = new DetailPartFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public DetailPartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            int mPage = getArguments().getInt(ARG_PAGE);
            mStep = getArguments().getParcelable(ARG_STEPS);
        }
    }

    /**
     * Inflates the fragment layout file and sets the correct resource for the image to display
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null)
        {
            mStep = savedInstanceState.getParcelable(STATE_KEY_STEP_DETAIL);
        }

        // Inflate the Android-Me fragment layout
        View rootView = inflater.inflate(R.layout.fragment_detail_part, container, false);

        // Initialize the player view.
        mPlayerView    = rootView.findViewById(R.id.playerView);
        thumbnailImage = rootView.findViewById(R.id.step_imageview);

        TextView description     = rootView.findViewById(R.id.step_title_txt);
        TextView shorDescription = rootView.findViewById(R.id.step_detail_txt);



        if (mStep != null)
        {
            description.setText(mStep.getShortDescription());
            shorDescription.setText(mStep.getDescription());


            String  videoURL     = mStep.getVideoURL();
            String  thumbnailURL = mStep.getThumbnailURL();

            if (TextUtils.isEmpty(videoURL))
            {
                mPlayerView.setVisibility(View.GONE);
                thumbnailImage.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(thumbnailURL))
                {
                    Picasso.get().load(R.drawable.user_placeholder)
                                 .into(thumbnailImage);
                }
                else
                {
                    Picasso.get()
                            .load(thumbnailURL)
                            .placeholder(R.drawable.user_placeholder)
                            .error(R.drawable.user_placeholder_error)
                            .into(thumbnailImage);
                }
            }
            else
            {
                mPlayerView.setVisibility(View.VISIBLE);
                thumbnailImage.setVisibility(View.GONE);

                // Initialize the Media Session.
                initializeMediaSession();

                // Initialize the player.
                initializePlayer(Uri.parse(mStep.getVideoURL()));
            }
        }

        // Return the rootView
        return rootView;
    }


    public void setStep(Step step)
    {
        mStep = step;
    }


    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession()
    {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new DetailPartFragment.MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }



    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri)
    {
        if (mExoPlayer == null)
        {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer()
    {
        if (mExoPlayer != null)
        {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putParcelable(STATE_KEY_STEP_DETAIL,  mStep);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (mExoPlayer != null)
        {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mExoPlayer != null)
        {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        releasePlayer();
        if (mMediaSession != null)
        {
            mMediaSession.setActive(false);
        }

    }


    // ExoPlayer Event Listeners

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {


    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady)
        {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        else if((playbackState == ExoPlayer.STATE_READY))
        {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay()
        {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause()
        {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver
    {
        public MediaReceiver()
        {}

        @Override
        public void onReceive(Context context, Intent intent)
        {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }


}
