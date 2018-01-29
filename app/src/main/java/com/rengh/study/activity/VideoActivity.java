package com.rengh.study.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.api.VideoWindowListiner;
import com.rengh.study.window.api.WindowViewInterface;
import com.rengh.study.window.view.ExoWindowView;
import com.rengh.study.window.view.VideoWindowView;

public class VideoActivity extends Activity implements View.OnClickListener, VideoWindowListiner {
    private final String TAG = "VideoActivity";
    private Context context;

    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int RESULT_CODE_CHECK_PERMISSION = 1;

    private Uri[] uris = {
//            Uri.parse("/sdcard/0.ts"),
//            Uri.parse("/sdcard/1.ts"),
//            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjYzLzcvODYvbGV0di1ndWcvMTcvMTExMzMwOTkxMC1hdmMtNzc3LWFhYy03Ny0xNTAwMC02Mzc1MjY4LTY4ZTg1NzA3ZTk4N2NhMWQ3MDBjODU5M2YwZmNjMjZlLTE1MDM0NjAxOTY3MjYudHM=?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66097865&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjQ4LzI4LzI5L2xldHYtZ3VnLzE3LzExMTk1Mzg2MzUtYXZjLTc3Ny1hYWMtNzctMTUwNDAtNjU2ODM0NC0wOWJjZTQxYTE2MzJiYWI5MWIyOThiYjYxMDY0ZjVkNC0xNTE0NDQ5NTA3MjU0LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66723569&amp;type=tv_1080p"),
            Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjU1LzQ2Lzc3L2xldHYtZ3VnLzE3LzExMTk3NDU0MDctYXZjLTc3Ny1hYWMtNzctMTUwMDAtNjQyNDcxMi1mNzBmOGFhMTlmNmQ4YzE1YTM0MTljMDQ2MGExN2MwZS0xNTE1NzQyMjY4Nzc5LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66740468&amp;type=tv_1080p"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");

        setContentView(R.layout.activity_video);

        context = this;

        startWindowWithCheckPermission();
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
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "===== onDestroy() =====");
        MyWindowManager.getInstance(context).release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RESULT_CODE_CHECK_PERMISSION: {
                boolean denied = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                        denied = true;
                        break;
                    }
                }
                if (denied) {
                    Toast.makeText(context, "Permission denied!", Toast.LENGTH_LONG).show();
                } else {
                    startWindowWithCheckPermission();
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.v(TAG, "===== onActivityResult() =====");
        switch (requestCode) {
            case RESULT_CODE_CHECK_PERMISSION: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        //若用户开启了overlay权限,则打开window
                        startWindowWithCheckPermission();
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

    private void startWindowWithCheckPermission() {
        LogUtils.v(TAG, "===== startWindowWithCheckPermission() =====");
        boolean denied = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ActivityCompat.checkSelfPermission(this, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, RESULT_CODE_CHECK_PERMISSION);
                    denied = true;
                }
            }
            if (denied) {
                return;
            }
            if (!Settings.canDrawOverlays(VideoActivity.this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())), RESULT_CODE_CHECK_PERMISSION);
                denied = true;
            }
            if (denied) {
                return;
            }
        }
        openWindow();
    }

    private void openWindow() {
        LogUtils.v(TAG, "===== openWindow() =====");

        WindowViewInterface windowView = null;
        MyWindowManager.getInstance(context).setListiner(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(context, "VideoView...", Toast.LENGTH_LONG).show();
            windowView = new VideoWindowView(this, uris);
        } else {
            Toast.makeText(context, "ExoPlayer...", Toast.LENGTH_LONG).show();
            windowView = new ExoWindowView(this, uris);
        }

        MyWindowManager.getInstance(context).openWindow(windowView);
    }

}
