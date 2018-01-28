package com.rengh.study.window.view;

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

import com.rengh.study.R;
import com.rengh.study.util.common.AgentUtils;
import com.rengh.study.util.common.BitmapUtils;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.util.common.ThreadUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.api.WindowManagerInterface;
import com.rengh.study.window.api.WindowViewInterface;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 2018/1/26.
 */

public class ExoWindowView implements WindowViewInterface, PlaybackControlView.VisibilityListener {
    private static final String TAG = "ExoWindowView";
    private Context context;
    private WindowManagerInterface windowManager;
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

    public ExoWindowView(Context context, MyWindowManager windowManager) {
        LogUtils.v(TAG, "===== ExoWindowView() =====");
        mainHandler = new MyHandler(this);
        this.context = context.getApplicationContext();
        this.windowManager = windowManager;
    }

    @Override
    public WindowManager.LayoutParams getParams() {
        LogUtils.v(TAG, "===== getParams() =====");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = 192 * 5;//WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 108 * 5;//WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 100;
        params.y = 100;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
//                | WindowManager.LayoutParams.FLAG_SECURE
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        return params;
    }

    @Override
    public View getView() {
        LogUtils.v(TAG, "===== getView() =====");
        View view = View.inflate(context, R.layout.layout_exoplayer, null);
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    windowManager.finish();
                    return true;
                }
                return false;
            }
        });
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);
        simpleExoPlayerView = view.findViewById(R.id.player_view);
        simpleExoPlayerView.setKeepScreenOn(true);
        simpleExoPlayerView.setUseArtwork(true);
        simpleExoPlayerView.setDefaultArtwork(
                BitmapUtils.readBitMap(context, R.mipmap.ic_launcher_round));
        simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();
        return view;
    }

    @Override
    public void initialize() {
        LogUtils.v(TAG, "===== initialize() =====");
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

    @Override
    public void release() {
        LogUtils.v(TAG, "===== release() =====");
        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @NonNull
    private MediaSource getMediaSource() {
        LogUtils.v(TAG, "===== getMediaSource() =====");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                AgentUtils.getUserAgent(context, context.getString(R.string.app_name)));

        MediaSource[] mediaSourcesArr = new MediaSource[uris.length];
        for (int i = 0; i < mediaSourcesArr.length; i++) {
            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource
                    .Factory(dataSourceFactory).createMediaSource(uris[i]);
            mediaSourcesArr[i] = extractorMediaSource;
        }

        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mediaSourcesArr);
//        LoopingMediaSource firstSourceTwice = new LoopingMediaSource(concatenatedSource);

        return concatenatedSource;
    }

    private static class MyHandler extends Handler {
        private final String TAG = "MyHandler";
        private final WeakReference<ExoWindowView> weakReference;

        public MyHandler(ExoWindowView windowView) {
            LogUtils.v(TAG, "===== MyHandler() =====");
            weakReference = new WeakReference<ExoWindowView>(windowView);
        }

        @Override
        public void handleMessage(Message msg) {
            LogUtils.v(TAG, "===== handleMessage() =====");
            ExoWindowView windowView = weakReference.get();
            if (null != windowView) {
                windowView.processMessage(msg);
            }
        }
    }

    public void processMessage(Message msg) {
        LogUtils.v(TAG, "===== processMessage() =====");
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
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private static class CountdownThread extends Thread {
        private final String TAG = "CountdownThread";
        private final WeakReference<ExoWindowView> weakReference;

        public CountdownThread(ExoWindowView windowView) {
            LogUtils.v(TAG, "===== CountdownThread() =====");
            weakReference = new WeakReference<ExoWindowView>(windowView);
        }

        @Override
        public void run() {
            LogUtils.v(TAG, "===== run() =====");
            ExoWindowView windowView = weakReference.get();
            while (!windowView.isFinishing()) {
                windowView.getCountdownMessage();
                ThreadUtils.sleep(500);
            }
        }
    }

    public void getCountdownMessage() {
        LogUtils.v(TAG, "===== getCountdownMessage() =====");
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
        LogUtils.v(TAG, "===== onVisibilityChange() =====");
    }

    private static class PlayerEventListener extends Player.DefaultEventListener {
        private final String TAG = "PlayerEventListener";
        private final WeakReference<ExoWindowView> weakReference;

        public PlayerEventListener(ExoWindowView windowView) {
            LogUtils.v(TAG, "===== PlayerEventListener() =====");
            weakReference = new WeakReference<ExoWindowView>(windowView);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Do nothing.
            LogUtils.v(TAG, "===== onTimelineChanged() =====");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
            LogUtils.v(TAG, "===== onTracksChanged() =====");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
            LogUtils.v(TAG, "===== onLoadingChanged() =====");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtils.v(TAG, "===== onPlayerStateChanged() =====");
            ExoWindowView windowView = weakReference.get();
            if (playbackState == Player.STATE_ENDED) {
                if (null != windowView) {
                    Toast.makeText(windowView.context, "Play completed!", Toast.LENGTH_LONG).show();
                }
                windowView.finish();
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
            ExoWindowView windowView = weakReference.get();
            if (null != windowView) {
                Toast.makeText(windowView.context, "Play error!", Toast.LENGTH_LONG).show();
                windowView.finish();
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

    public boolean isFinishing() {
        LogUtils.v(TAG, "===== isFinishing() =====");
        return windowManager.isFinishing();
    }

    public void finish() {
        LogUtils.v(TAG, "===== finish() =====");
        windowManager.finish();
    }
}
