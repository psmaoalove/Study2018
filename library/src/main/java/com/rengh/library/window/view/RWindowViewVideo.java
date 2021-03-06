package com.rengh.library.window.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import com.rengh.library.R;
import com.rengh.library.util.common.ThreadUtils;
import com.rengh.library.window.api.RVideoListinerR;
import com.rengh.library.window.api.RWindowListiner;
import com.rengh.library.window.api.RWindowViewInterface;
import com.rengh.library.window.utils.RParamUtils;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 2018/1/26.
 */

public class RWindowViewVideo implements RWindowViewInterface {
    private Context context;
    private MyHandler mainHandler;
    private View view;
    private TextView tvCountdown;
    private VideoView player;

    private RVideoListinerR listiner;

    private Uri[] uris;

    private int index = 0;

    private final int WHAT_COUNTDOWN_UPDATE = 1;

    public RWindowViewVideo(Context context, Uri[] uris) {
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
            view = View.inflate(context, R.layout.layout_videoview, null);
            view.setFocusableInTouchMode(true);
            tvCountdown = view.findViewById(R.id.tv_countdown);
            tvCountdown.setVisibility(View.GONE);
            player = view.findViewById(R.id.player_view);
            player.setKeepScreenOn(true);
            player.setOnKeyListener(new View.OnKeyListener() {
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
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    initialize();
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (null != listiner) {
                        listiner.onPlayError(String.valueOf(what));
                    }
                    return false;
                }
            });
            player.requestFocus();
        }
        return view;
    }

    @Override
    public void initialize() {
        if (index >= uris.length) {
            if (null != listiner) {
//                listiner.onPlayCompleted();
                index = 0;
                initialize();
            }
            return;
        }
        player.setVideoURI(uris[index++]);
        player.start();
        CountdownThread countdownThread = new CountdownThread(this);
        countdownThread.start();

        if (null != listiner) {
            listiner.onPlayStart();
        }
    }

    @Override
    public void release() {
        if (player != null) {
            player.stopPlayback();
            player.destroyDrawingCache();
            player = null;
        }
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
    public RWindowViewInterface setListiner(RWindowListiner listiner) {
        this.listiner = (RVideoListinerR) listiner;
        return this;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RWindowViewVideo> weakReference;

        public MyHandler(RWindowViewVideo windowView) {
            weakReference = new WeakReference<RWindowViewVideo>(windowView);
        }

        @Override
        public void handleMessage(Message msg) {
            RWindowViewVideo windowView = weakReference.get();
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
        private final WeakReference<RWindowViewVideo> weakReference;

        public CountdownThread(RWindowViewVideo windowView) {
            weakReference = new WeakReference<RWindowViewVideo>(windowView);
        }

        @Override
        public void run() {
            RWindowViewVideo windowView = weakReference.get();
            while (!windowView.isReleased()) {
                windowView.getCountdownMessage();
                ThreadUtils.sleep(500);
            }
        }
    }

    public void getCountdownMessage() {
        try {
            long duration = player.getDuration();
            long position = player.getCurrentPosition();
            Message msg = new Message();
            msg.arg1 = (int) duration / 1000;
            msg.arg2 = (int) position / 1000;
            msg.what = WHAT_COUNTDOWN_UPDATE;
            mainHandler.sendMessage(msg);
        } catch (IllegalStateException e) {
        }
    }

}
