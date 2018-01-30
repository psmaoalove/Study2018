package com.rengh.study.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.api.VideoWindowListiner;
import com.rengh.study.window.api.WindowViewInterface;
import com.rengh.study.window.utils.VideoUrisUtils;
import com.rengh.study.window.view.AdWindowView;
import com.rengh.study.window.view.VideoWindowView;

public class AdActivity extends Activity implements View.OnClickListener, VideoWindowListiner {
    private final String TAG = "AdActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");

        setContentView(R.layout.activity_null);

        context = this;

        openWindow();
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
        MyWindowManager.getInstance(context).release();
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

    private void openWindow() {
        LogUtils.v(TAG, "===== openWindow() =====");

        MyWindowManager.getInstance(context).setListiner(this);

        WindowViewInterface windowView = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(context, "VideoView...", Toast.LENGTH_LONG).show();
            windowView = new VideoWindowView(this, VideoUrisUtils.getUris());
        } else {
            Toast.makeText(context, "AdPlayer...", Toast.LENGTH_LONG).show();
            windowView = new AdWindowView(this, VideoUrisUtils.getUris());
        }

        MyWindowManager.getInstance(context).openWindow(windowView);
    }

}
