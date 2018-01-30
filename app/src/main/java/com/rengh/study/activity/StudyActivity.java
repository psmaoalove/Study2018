package com.rengh.study.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.util.common.ShellUtils;
import com.rengh.study.util.reflex.SystemPropProxy;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.utils.VideoUrisUtils;
import com.rengh.study.window.view.AdWindowView;
import com.rengh.study.window.view.ExoWindowView;
import com.rengh.study.window.view.VideoWindowView;

import java.io.File;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "StudyActivity";
    private Context context;
    private Button btnShell, btnService, btnInstall, btnExoPlayer, btnVideoPlayer, btnAdPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");
        setContentView(R.layout.activity_study);

        context = this;

        btnShell = findViewById(R.id.btn_shell);
        btnService = findViewById(R.id.btn_service);
        btnInstall = findViewById(R.id.btn_install);
        btnExoPlayer = findViewById(R.id.btn_exo_player);
        btnVideoPlayer = findViewById(R.id.btn_video_player);
        btnAdPlayer = findViewById(R.id.btn_ad_player);

        btnShell.setText("普通安装");
        btnShell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShellUtils.su("setprop service.adb.tcp.port 5555", "stop adbd", "start adbd");
//                ShellUtils.su("am install -r /sdcard/sjfwz_1.3.0_dangbei.apk");
//                SystemPropProxy.set(context, "service.adb.tcp.port", "6666");
                File file = new File("/sdcard/sjfwz_1.3.0_dangbei.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
            }
        });
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForwardToAccessibility();
            }
        });
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSmartInstall("/sdcard/sjfwz_1.3.0_dangbei.apk");
            }
        });
        btnExoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExoPlayerStart();
            }
        });
        btnVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVideoPlayerStart();
            }
        });
        btnAdPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdPlayerStart();
            }
        });
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "===== onDestroy() =====");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.v(TAG, "===== onActivityResult() =====");
        switch (requestCode) {
        }
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
        }
    }

    public void onForwardToAccessibility() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void onSmartInstall(String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.fromFile(new File(apkPath));
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(localIntent);
    }

    private void onExoPlayerStart() {
        Intent videoActivity = new Intent();
        videoActivity.setClass(context, VideoActivity.class);
        videoActivity.setAction("ExoPlayer");
        startActivity(videoActivity);
    }

    private void onVideoPlayerStart() {
        Intent videoActivity = new Intent();
        videoActivity.setClass(context, VideoActivity.class);
        videoActivity.setAction("VideoView");
        startActivity(videoActivity);
    }

    private void onAdPlayerStart() {
        Intent adActivity = new Intent();
        adActivity.setClass(context, AdActivity.class);
        startActivity(adActivity);
    }

}
