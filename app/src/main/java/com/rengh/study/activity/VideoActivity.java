package com.rengh.study.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.rengh.library.util.common.LogUtils;
import com.rengh.library.window.RWindowManager;
import com.rengh.library.window.api.RVideoListinerR;
import com.rengh.library.window.api.RWindowViewInterface;
import com.rengh.library.window.utils.RVideoUris;
import com.rengh.library.window.view.RWindowViewAd;
import com.rengh.library.window.view.RWindowViewExo;
import com.rengh.library.window.view.RWindowViewVideo;
import com.rengh.study.R;

public class VideoActivity extends Activity implements View.OnClickListener, RVideoListinerR {
    private final String TAG = "VideoActivity";
    private Context context;
    private RWindowViewInterface windowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");
        setContentView(R.layout.activity_null);
        context = this;

        boolean useWindow = false;
        String type = "";
        Intent intent = getIntent();
        if (null != intent) {
            useWindow = intent.getBooleanExtra("useWindow", false);
            type = intent.getStringExtra("type");
        }

        if ("ad".equalsIgnoreCase(type)) {
            windowView = new RWindowViewAd(context, RVideoUris.getUris()).setListiner(this);
        } else if ("exoplayer".equalsIgnoreCase(type)) {
            windowView = new RWindowViewExo(context, RVideoUris.getUris()).setListiner(this);
        } else if ("videoview".equalsIgnoreCase(type)) {
            windowView = new RWindowViewVideo(context, RVideoUris.getUris()).setListiner(this);
        } else {
            windowView = new RWindowViewAd(context, RVideoUris.getUris()).setListiner(this);
        }

        if (useWindow) {
            // WindowManager展示
            RWindowManager.getInstance(context).openWindow(windowView);
        } else {
            // Activity展示
            setContentView(windowView.getView());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.v(TAG, "===== onNewIntent() =====");
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.v(TAG, "===== onStart() =====");

        windowView.initialize();

        String msg = "Unknow";
        if (windowView instanceof RWindowViewAd) {
            msg = "Ad...";
        } else if (windowView instanceof RWindowViewExo) {
            msg = "ExoPlayer...";
        } else if (windowView instanceof RWindowViewVideo) {
            msg = "VideoView...";
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.v(TAG, "===== onResume() =====");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.v(TAG, "===== onPause() =====");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.v(TAG, "===== onStop() =====");
//        RWindowManager.getInstance(context).release();
        windowView.release();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "===== onDestroy() =====");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.v(TAG, "===== onKeyDown() =====");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.v(TAG, "===== dispatchKeyEvent() =====");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        LogUtils.v(TAG, "===== onClick() =====");
        switch (view.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onPlayStart() {
        LogUtils.v(TAG, "===== onPlayStart() =====");
    }

    @Override
    public void onPlayError(String err) {
        LogUtils.v(TAG, "===== onPlayError() =====");
        Toast.makeText(context, "Play error!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onPlayCompleted() {
        LogUtils.v(TAG, "===== onPlayCompleted() =====");
        Toast.makeText(context, "Play completed!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onPlayCompletedByUser() {
        LogUtils.v(TAG, "===== onPlayCompletedByUser() =====");
        Toast.makeText(context, "Play completed by user!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onReleased() {
        LogUtils.v(TAG, "===== onReleased() =====");
    }
}
