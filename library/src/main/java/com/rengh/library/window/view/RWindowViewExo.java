package com.rengh.library.window.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
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
import com.rengh.library.R;
import com.rengh.library.util.common.LogUtils;
import com.rengh.library.util.common.ThreadUtils;
import com.rengh.library.window.api.RVideoListinerR;
import com.rengh.library.window.api.RWindowListiner;
import com.rengh.library.window.api.RWindowViewInterface;
import com.rengh.library.window.utils.RParamUtils;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 2018/1/26.
 */

public class RWindowViewExo implements RWindowViewInterface, PlaybackControlView.VisibilityListener {
    private Context context;
    private MyHandler mainHandler;
    private View view;
    private TextView tvCountdown;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private DefaultTrackSelector trackSelector;

    private RVideoListinerR listiner;

    private Uri[] uris;

    private final int WHAT_COUNTDOWN_UPDATE = 1;

    public RWindowViewExo(Context context, Uri[] uris) {
        mainHandler = new MyHandler(this);
        this.context = context.getApplicationContext();
        this.uris = uris;
    }

    @Override
    public WindowManager.LayoutParams getParams() {
        return RParamUtils.getParams();
    }

    @Override
    public View getView() {
        if (null == view) {
            view = View.inflate(context, R.layout.layout_exoplayer, null);
            view.setFocusableInTouchMode(true);
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (null != listiner) {
                            listiner.onPlayCompletedByUser();
                        }
                        return true;
                    }
                    return false;
                }
            });
            tvCountdown = view.findViewById(R.id.tv_countdown);
            tvCountdown.setVisibility(View.GONE);
            simpleExoPlayerView = view.findViewById(R.id.player_view);
            simpleExoPlayerView.setUseController(false);
            simpleExoPlayerView.setControllerVisibilityListener(this);
            simpleExoPlayerView.setUseArtwork(false);
            simpleExoPlayerView.setKeepScreenOn(true);
            simpleExoPlayerView.requestFocus();
        }
        return view;
    }

    @Override
    public void initialize() {
        if (null == player) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            simpleExoPlayerView.setPlayer(player);
            player.addListener(new PlayerEventListener(this));
            player.setPlayWhenReady(true);
            player.prepare(getMediaSource());

            CountdownThread countdownThread = new CountdownThread(this);
            countdownThread.start();

            if (null != listiner) {
                listiner.onPlayStart();
            }
        }
    }

    @Override
    public void release() {
        if (null != simpleExoPlayerView) {
            simpleExoPlayerView.destroyDrawingCache();
            simpleExoPlayerView = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
        trackSelector = null;
        mainHandler = null;

        if (null != listiner) {
            listiner.onReleased();
            listiner = null;
        }
    }

    @Override
    public boolean isReleased() {
        return player == null;
    }

    @Override
    public RWindowViewExo setListiner(RWindowListiner listiner) {
        this.listiner = (RVideoListinerR) listiner;
        return this;
    }

    @NonNull
    private MediaSource getMediaSource() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                getUserAgent(context, context.getString(R.string.app_name)));

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
        private final WeakReference<RWindowViewExo> weakReference;

        public MyHandler(RWindowViewExo windowView) {
            weakReference = new WeakReference<RWindowViewExo>(windowView);
        }

        @Override
        public void handleMessage(Message msg) {
            RWindowViewExo windowView = weakReference.get();
            if (null != windowView) {
                windowView.processMessage(msg);
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
                        tvCountdown.setText(context.getString(R.string.text_ad_countdown, time));
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private static class CountdownThread extends Thread {
        private final WeakReference<RWindowViewExo> weakReference;

        public CountdownThread(RWindowViewExo windowView) {
            weakReference = new WeakReference<RWindowViewExo>(windowView);
        }

        @Override
        public void run() {
            RWindowViewExo windowView = weakReference.get();
            while (!windowView.isReleased()) {
                windowView.getCountdownMessage();
                ThreadUtils.sleep(500);
            }
        }
    }

    public void getCountdownMessage() {
        if (isReleased()) {
            return;
        }
        long duration = player.getDuration();
        long position = player.getCurrentPosition();
        Message msg = new Message();
        msg.arg1 = (int) duration / 1000;
        msg.arg2 = (int) position / 1000;
        msg.what = WHAT_COUNTDOWN_UPDATE;
        mainHandler.sendMessage(msg);
    }

    @Override
    public void onVisibilityChange(int visibility) {
    }

    private static class PlayerEventListener extends Player.DefaultEventListener {
        private final String TAG = "PlayerEventListener";
        private final WeakReference<RWindowViewExo> weakReference;

        public PlayerEventListener(RWindowViewExo windowView) {
            LogUtils.v(TAG, "===== PlayerEventListener() =====");
            weakReference = new WeakReference<RWindowViewExo>(windowView);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
            LogUtils.v(TAG, "===== onTracksChanged() =====");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtils.v(TAG, "===== onPlayerStateChanged() =====");
            RWindowViewExo windowView = weakReference.get();
            switch (playbackState) {
                case Player.STATE_IDLE:
                    LogUtils.v(TAG, "STATE_IDLE!");
                    break;
                case Player.STATE_BUFFERING:
                    LogUtils.v(TAG, "STATE_BUFFERING!");
                case Player.STATE_READY:
                    LogUtils.v(TAG, "STATE_READY!");
                    break;
                case Player.STATE_ENDED:
                    LogUtils.v(TAG, "STATE_ENDED!");
                    if (null != windowView && null != windowView.listiner) {
                        windowView.listiner.onPlayCompleted();
                    }
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(@Player.RepeatMode int repeatMode) {
            // Do nothing.
            LogUtils.v(TAG, "===== onRepeatModeChanged() =====");
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            // Do nothing.
            LogUtils.v(TAG, "===== onShuffleModeEnabledChanged() =====");
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            LogUtils.v(TAG, "===== onPlayerError() =====");
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
            RWindowViewExo windowView = weakReference.get();
            if (null != windowView && null != windowView.listiner) {
                windowView.listiner.onPlayError(errorString);
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            // This will only occur if the user has performed a seek whilst in the error state. Update
            // the resume position so that if the user then retries, playback will resume from the
            // position to which they seeked.
            LogUtils.v(TAG, "===== onPositionDiscontinuity() =====");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
            LogUtils.v(TAG, "===== onPlaybackParametersChanged() =====");
        }

        @Override
        public void onSeekProcessed() {
            // Do nothing.
            LogUtils.v(TAG, "===== onSeekProcessed() =====");
        }
    }

    /**
     * Returns a user agent string based on the given application name and the library version.
     *
     * @param context         A valid context of the calling application.
     * @param applicationName String that will be prefix'ed to the generated user agent.
     * @return A user agent string generated using the applicationName and the library version.
     */
    public static String getUserAgent(Context context, String applicationName) {
        String versionName;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY;
    }

}
