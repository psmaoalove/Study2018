package com.rengh.study.activity;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.view.ExoWindowView;
import com.rengh.study.window.view.VideoWindowView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "StudyActivity";
    private Context context;
    private Button btnPlay;
    private VideoView videoView;

    private ExoWindowView exoWindowView;
    private VideoWindowView videoWindowView;

    private final int RESULT_CODE_CHECK_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");

        setContentView(R.layout.activity_study);

        context = this;

        btnPlay = findViewById(R.id.btn_play);
        videoView = findViewById(R.id.vv_test);

        exoWindowView = new ExoWindowView(context, MyWindowManager.getInstance(context));
        videoWindowView = new VideoWindowView(context, MyWindowManager.getInstance(context));

        btnPlay.setOnClickListener(this);
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
        videoView.stopPlayback();
        videoView = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.v(TAG, "===== onActivityResult() =====");
        switch (requestCode) {
            case RESULT_CODE_CHECK_PERMISSION: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        //若用户开启了overlay权限,则打开window
                        openWindow();
                    } else {
                        Toast.makeText(this, "未开启Overlay权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.v(TAG, "===== onKeyDown() =====");
        return super.onKeyDown(keyCode, event);
    }

    // Activity input
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.v(TAG, "===== dispatchKeyEvent() =====");
        // See whether the player view wants to handle media or DPAD keys events.
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        LogUtils.v(TAG, "===== onClick() =====");
        switch (view.getId()) {
            case R.id.btn_play:
                startWindowWithCheckPermission();
//                Intent exoPlayerIntent = new Intent();
//                exoPlayerIntent.setClass(context, ExoPlayerActivity.class);
//                startActivity(exoPlayerIntent);
                break;
            default:
                break;
        }
    }

    private void startWindowWithCheckPermission() {
        LogUtils.v(TAG, "===== startWindowWithCheckPermission() =====");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //版本大于6.0则需要判断是否获取了overlays权限
            if (!Settings.canDrawOverlays(StudyActivity.this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())), RESULT_CODE_CHECK_PERMISSION);
                return;
            }
        }
        openWindow();
    }

    private void openWindow() {
        LogUtils.v(TAG, "===== openWindow() =====");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(context, "VideoView...", Toast.LENGTH_LONG).show();
            MyWindowManager.getInstance(context).openWindow(videoWindowView);
        } else {
            Toast.makeText(context, "ExoPlayer...", Toast.LENGTH_LONG).show();
            MyWindowManager.getInstance(context).openWindow(exoWindowView);
        }
    }

}
