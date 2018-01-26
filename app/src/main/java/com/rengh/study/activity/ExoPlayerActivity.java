package com.rengh.study.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.rengh.study.R;
import com.rengh.study.util.AgentUtils;
import com.rengh.study.util.common.BitmapUtils;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.util.common.ThreadUtils;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 18-1-26.
 */

public class ExoPlayerActivity extends AppCompatActivity implements
        PlaybackControlView.VisibilityListener {
    private Context context;
    private MyHandler mainHandler;
    private TextView tvCountdown;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private DefaultTrackSelector trackSelector;

    private Uri[] uris = {
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjYzLzcvODYvbGV0di1ndWcvMTcvMTExMzMwOTkxMC1hdmMtNzc3LWFhYy03Ny0xNTAwMC02Mzc1MjY4LTY4ZTg1NzA3ZTk4N2NhMWQ3MDBjODU5M2YwZmNjMjZlLTE1MDM0NjAxOTY3MjYudHM=?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66097865&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjQ4LzI4LzI5L2xldHYtZ3VnLzE3LzExMTk1Mzg2MzUtYXZjLTc3Ny1hYWMtNzctMTUwNDAtNjU2ODM0NC0wOWJjZTQxYTE2MzJiYWI5MWIyOThiYjYxMDY0ZjVkNC0xNTE0NDQ5NTA3MjU0LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66723569&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjU1LzQ2Lzc3L2xldHYtZ3VnLzE3LzExMTk3NDU0MDctYXZjLTc3Ny1hYWMtNzctMTUwMDAtNjQyNDcxMi1mNzBmOGFhMTlmNmQ4YzE1YTM0MTljMDQ2MGExN2MwZS0xNTE1NzQyMjY4Nzc5LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66740468&amp;type=tv_1080p"),
    };

    private final int WHAT_COUNTDOWN_UPDATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exoplayer);

        context = this;
        mainHandler = new MyHandler(this);

        tvCountdown = findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);

        simpleExoPlayerView = findViewById(R.id.player_view);
        simpleExoPlayerView.setKeepScreenOn(true);
        simpleExoPlayerView.setUseArtwork(true);
        simpleExoPlayerView.setDefaultArtwork(BitmapUtils.readBitMap(context, R.mipmap.ic_launcher_round));
        // simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        setIntent(intent);
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
        if (Util.SDK_INT <= 23 || player == null) {
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
    public void onDestroy() {
        super.onDestroy();
    }

    // Activity input
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return simpleExoPlayerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public void onVisibilityChange(int visibility) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    private void initializePlayer() {
        if (null == player) {
            // 1. Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            // Bind the player to the view.
            simpleExoPlayerView.setPlayer(player);
            player.addListener(new PlayerEventListener(this));
            player.setPlayWhenReady(true);
        }

        player.prepare(getMediaSource());

        CountdownThread countdownThread = new CountdownThread(this);
        countdownThread.start();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @NonNull
    private MediaSource getMediaSource() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                AgentUtils.getUserAgent(context, context.getString(R.string.app_name)));

        MediaSource[] mediaSourcesArr = new MediaSource[uris.length];
        for (int i = 0; i < mediaSourcesArr.length; i++) {
            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource
                    .Factory(dataSourceFactory).createMediaSource(uris[i]);
            mediaSourcesArr[i] = extractorMediaSource;
        }

        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mediaSourcesArr);
        LoopingMediaSource firstSourceTwice = new LoopingMediaSource(concatenatedSource);

        return firstSourceTwice;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ExoPlayerActivity> weakReference;

        public MyHandler(ExoPlayerActivity activity) {
            weakReference = new WeakReference<ExoPlayerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ExoPlayerActivity activity = weakReference.get();
            if (null != activity) {
                activity.processMessage(msg);
            }
        }
    }

    public void processMessage(Message msg) {
        switch (msg.what) {
            case WHAT_COUNTDOWN_UPDATE: {
                int total = msg.arg1;
                if (0 < total) {
                    int current = msg.arg2;
                    int time = total - current;
                    if (0 < time) {
                        if (tvCountdown.getVisibility() != View.VISIBLE) {
                            tvCountdown.setVisibility(View.VISIBLE);
                        }
                        tvCountdown.setText(context.getString(R.string.text_exo_countdown,
                                String.valueOf(time)));
                        if (time == 10) {
                            Intent intent = new Intent();
                            intent.setClass(context, StudyActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private static class CountdownThread extends Thread {
        private final WeakReference<ExoPlayerActivity> weakReference;

        public CountdownThread(ExoPlayerActivity activity) {
            weakReference = new WeakReference<ExoPlayerActivity>(activity);
        }

        @Override
        public void run() {
            ExoPlayerActivity activity = weakReference.get();
            while (!activity.isFinishing()) {
                activity.getCountdownMessage();
                ThreadUtils.sleep(500);
            }
        }
    }

    public void getCountdownMessage() {
        long duration = player.getDuration();
        long position = player.getCurrentPosition();
        Message msg = new Message();
        msg.arg1 = (int) duration / 1000;
        msg.arg2 = (int) position / 1000;
        msg.what = WHAT_COUNTDOWN_UPDATE;
        mainHandler.sendMessage(msg);
    }

    private static class PlayerEventListener extends Player.DefaultEventListener {
        private final String TAG = "PlayerEventListener";
        private final WeakReference<ExoPlayerActivity> weakReference;

        public PlayerEventListener(ExoPlayerActivity activity) {
            weakReference = new WeakReference<ExoPlayerActivity>(activity);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Do nothing.
            LogUtils.i(TAG, "onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
            LogUtils.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
            LogUtils.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtils.i(TAG, "onPlayerStateChanged");
            ExoPlayerActivity activity = weakReference.get();
            if (playbackState == Player.STATE_ENDED) {
                Toast.makeText(activity, "Play completed!", Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }

        @Override
        public void onRepeatModeChanged(@Player.RepeatMode int repeatMode) {
            // Do nothing.
            LogUtils.i(TAG, "onRepeatModeChanged");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            // Do nothing.
            LogUtils.i(TAG, "onShuffleModeEnabledChanged");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            LogUtils.i(TAG, "onPlayerError");
            String errorString = null;
            if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = error.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException;
                    decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.decoderName == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        } else if (decoderInitializationException.secureDecoderRequired) {
                        } else {
                        }
                    } else {
                    }
                }
            }
            if (errorString != null) {
                LogUtils.e(TAG, errorString);
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            // This will only occur if the user has performed a seek whilst in the error state. Update
            // the resume position so that if the user then retries, playback will resume from the
            // position to which they seeked.
            LogUtils.i(TAG, "onPositionDiscontinuity");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
            LogUtils.i(TAG, "onPlaybackParametersChanged");
        }

        @Override
        public void onSeekProcessed() {
            // Do nothing.
            LogUtils.i(TAG, "onSeekProcessed");
        }
    }

}
