package com.rengh.library.window.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.rengh.library.R;
import com.rengh.library.util.common.ThreadUtils;
import com.rengh.library.view.MarqueeAlwaysTextView;
import com.rengh.library.window.api.RVideoListinerR;
import com.rengh.library.window.api.RWindowListiner;
import com.rengh.library.window.api.RWindowViewInterface;
import com.rengh.library.window.utils.RParamUtils;

import java.lang.ref.WeakReference;

/**
 * Created by rengh on 2018/1/26.
 */
public class RWindowViewAd implements RWindowViewInterface {
    private Context context;
    private MyHandler mainHandler;
    private View view;
    private VideoView videoView1, videoView2, videoView3;
    private MarqueeAlwaysTextView marqueeAlwaysTextView;
    private MediaPlayer.OnCompletionListener completionListener1, completionListener2, completionListener3;
    private MediaPlayer.OnErrorListener errorListener1, errorListener2, errorListener3;
    private MediaPlayer.OnPreparedListener preparedListener1, preparedListener2, preparedListener3;

    private RVideoListinerR listiner;

    private Uri[] uris;

    private int index = 0;

    private final int WHAT_COUNTDOWN_UPDATE = 1;

    public RWindowViewAd(Context context, Uri[] uris) {
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
            view = View.inflate(context, R.layout.layout_ad, null);
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
            videoView1 = view.findViewById(R.id.player_videoview1);
            videoView2 = view.findViewById(R.id.player_videoview2);
            videoView3 = view.findViewById(R.id.player_videoview3);
            marqueeAlwaysTextView = view.findViewById(R.id.tv_marquee);

            initListeners();

            videoView1.setOnPreparedListener(preparedListener1);
            videoView1.setOnCompletionListener(completionListener1);
            videoView1.setOnErrorListener(errorListener1);
            videoView2.setOnPreparedListener(preparedListener2);
            videoView2.setOnCompletionListener(completionListener2);
            videoView2.setOnErrorListener(errorListener2);
            videoView3.setOnPreparedListener(preparedListener3);
            videoView3.setOnCompletionListener(completionListener3);
            videoView3.setOnErrorListener(errorListener3);
        }
        return view;
    }

    private void initListeners() {
        preparedListener1 = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }
        };
        completionListener1 = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initialize();
            }
        };
        errorListener1 = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != listiner) {
                    listiner.onPlayError(String.valueOf(what));
                }
                return false;
            }
        };
        preparedListener2 = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }
        };
        completionListener2 = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initialize();
            }
        };
        errorListener2 = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != listiner) {
                    listiner.onPlayError(String.valueOf(what));
                }
                return false;
            }
        };
        preparedListener3 = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            }
        };
        completionListener3 = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initialize();
            }
        };
        errorListener3 = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != listiner) {
                    listiner.onPlayError(String.valueOf(what));
                }
                return false;
            }
        };
    }

    @Override
    public void initialize() {
        if (index >= uris.length) {
            if (null != listiner) {
                if (!videoView1.isPlaying() && !videoView2.isPlaying() && !videoView3.isPlaying()) {
//                    listiner.onPlayCompleted();
                    index = 0;
                    initialize();
                }
            }
            return;
        }

        videoView1.setVideoURI(uris[index++]);
        videoView2.setVideoURI(uris[index++]);
        videoView3.setVideoURI(uris[index++]);

        index++;

        videoView1.start();
        videoView2.start();
        videoView3.start();

        CountdownThread countdownThread = new CountdownThread(this);
        countdownThread.start();

        if (null != listiner) {
            listiner.onPlayStart();
        }
    }

    @Override
    public void release() {
        videoView1.stopPlayback();
        videoView1 = null;
        videoView2.stopPlayback();
        videoView2 = null;

        mainHandler = null;

        if (null != listiner) {
            listiner.onReleased();
            listiner = null;
        }
    }

    @Override
    public boolean isReleased() {
        return mainHandler == null;
    }

    @Override
    public RWindowViewAd setListiner(RWindowListiner listiner) {
        this.listiner = (RVideoListinerR) listiner;
        return this;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<RWindowViewAd> weakReference;

        public MyHandler(RWindowViewAd windowView) {
            weakReference = new WeakReference<RWindowViewAd>(windowView);
        }

        @Override
        public void handleMessage(Message msg) {
            RWindowViewAd windowView = weakReference.get();
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
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private static class CountdownThread extends Thread {
        private final WeakReference<RWindowViewAd> weakReference;

        public CountdownThread(RWindowViewAd windowView) {
            weakReference = new WeakReference<RWindowViewAd>(windowView);
        }

        @Override
        public void run() {
            RWindowViewAd windowView = weakReference.get();
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
        try {
            long duration = videoView1.getDuration();
            long position = videoView1.getCurrentPosition();
            Message msg = new Message();
            msg.arg1 = (int) duration / 1000;
            msg.arg2 = (int) position / 1000;
            msg.what = WHAT_COUNTDOWN_UPDATE;
            mainHandler.sendMessage(msg);
        } catch (Exception e) {
        }
    }
}
