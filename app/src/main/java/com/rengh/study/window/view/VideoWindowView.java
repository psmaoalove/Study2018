package com.rengh.study.window.view;

import com.google.android.exoplayer2.ui.PlaybackControlView;

import com.rengh.study.R;
import com.rengh.study.activity.StudyActivity;
import com.rengh.study.util.common.ThreadUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.api.WindowManagerInterface;
import com.rengh.study.window.api.WindowViewInterface;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 2018/1/26.
 */

public class VideoWindowView implements WindowViewInterface, PlaybackControlView.VisibilityListener {
    private Context context;
    private WindowManagerInterface windowManager;
    private MyHandler mainHandler;
    private TextView tvCountdown;
    private VideoView player;

    private Uri[] uris = {
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjYzLzcvODYvbGV0di1ndWcvMTcvMTExMzMwOTkxMC1hdmMtNzc3LWFhYy03Ny0xNTAwMC02Mzc1MjY4LTY4ZTg1NzA3ZTk4N2NhMWQ3MDBjODU5M2YwZmNjMjZlLTE1MDM0NjAxOTY3MjYudHM=?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66097865&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjQ4LzI4LzI5L2xldHYtZ3VnLzE3LzExMTk1Mzg2MzUtYXZjLTc3Ny1hYWMtNzctMTUwNDAtNjU2ODM0NC0wOWJjZTQxYTE2MzJiYWI5MWIyOThiYjYxMDY0ZjVkNC0xNTE0NDQ5NTA3MjU0LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66723569&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjU1LzQ2Lzc3L2xldHYtZ3VnLzE3LzExMTk3NDU0MDctYXZjLTc3Ny1hYWMtNzctMTUwMDAtNjQyNDcxMi1mNzBmOGFhMTlmNmQ4YzE1YTM0MTljMDQ2MGExN2MwZS0xNTE1NzQyMjY4Nzc5LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66740468&amp;type=tv_1080p"),
    };

    private final int WHAT_COUNTDOWN_UPDATE = 1;

    public VideoWindowView(Context context, MyWindowManager windowManager) {
        mainHandler = new MyHandler(this);
        this.context = context;
        this.windowManager = windowManager;
    }

    @Override
    public WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = 192 * 5;//WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 108 * 5;//WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_SECURE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        return params;
    }

    @Override
    public View getView() {
        View view = View.inflate(context, R.layout.layout_videoview, null);
        view.setFocusableInTouchMode(true);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvCountdown.setVisibility(View.GONE);
        player = view.findViewById(R.id.player_view);
        player.setKeepScreenOn(true);
        player.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    windowManager.finish();
                    return true;
                }
                return false;
            }
        });
        player.requestFocus();
        return view;
    }

    @Override
    public void initialize() {
        player.setVideoURI(uris[0]);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
                Toast.makeText(context, "Play completed!", Toast.LENGTH_LONG).show();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                finish();
                Toast.makeText(context, "Play error!", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        player.start();
        CountdownThread countdownThread = new CountdownThread(this);
        countdownThread.start();
    }

    @Override
    public void release() {
        if (player != null) {
            player.stopPlayback();
            player.destroyDrawingCache();
            player = null;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<VideoWindowView> weakReference;

        public MyHandler(VideoWindowView windowView) {
            weakReference = new WeakReference<VideoWindowView>(windowView);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoWindowView windowView = weakReference.get();
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
                        tvCountdown.setText(context.getString(R.string.text_exo_countdown,
                                String.valueOf(time)));
                        if (12 == time) {
                            Intent intent = new Intent();
                            intent.setClass(context, StudyActivity.class);
                            context.startActivity(intent);
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
        private final WeakReference<VideoWindowView> weakReference;

        public CountdownThread(VideoWindowView windowView) {
            weakReference = new WeakReference<VideoWindowView>(windowView);
        }

        @Override
        public void run() {
            VideoWindowView windowView = weakReference.get();
            while (!windowView.isFinishing()) {
                windowView.getCountdownMessage();
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

    @Override
    public void onVisibilityChange(int visibility) {
    }

    public boolean isFinishing() {
        return windowManager.isFinishing();
    }

    public void finish() {
        windowManager.finish();
    }
}
