package com.rengh.study.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "StudyActivity";
    private Context context;
    private Button btnPlay;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        context = this;

        btnPlay = findViewById(R.id.btn_play);
        videoView = findViewById(R.id.vv_test);

        btnPlay.setOnClickListener(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);

        Uri uri = Uri.parse("http://g3com.cp21.ott.cibntv.net/vod/v1/MjU1LzQ2Lzc3L2xldHYtZ3VnLzE3LzExMTk3NDU0MDctYXZjLTc3Ny1hYWMtNzctMTUwMDAtNjQyNDcxMi1mNzBmOGFhMTlmNmQ4YzE1YTM0MTljMDQ2MGExN2MwZS0xNTE1NzQyMjY4Nzc5LnRz?platid=100&amp;splatid=10000&amp;gugtype=6&amp;mmsid=66740468&amp;type=tv_1080p");
        videoView.setVideoURI(uri);
        videoView.start();
        LogUtils.i(TAG, "onNewIntent() Play video...");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        //若用户开启了overlay权限,则打开window
                        ExoWindowManager.getInstance(context).openWindow();
                    } else {
                        Toast.makeText(this, "不开启overlay权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    // Activity input
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //版本大于6.0则需要判断是否获取了overlays权限
            if (!Settings.canDrawOverlays(StudyActivity.this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())), 1);
            } else {
                ExoWindowManager.getInstance(context).openWindow();
            }
        }
    }

}
