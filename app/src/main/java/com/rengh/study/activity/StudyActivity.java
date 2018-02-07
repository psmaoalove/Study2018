package com.rengh.study.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengh.library.util.common.LogUtils;
import com.rengh.library.util.common.SystemPropProxy;
import com.rengh.study.R;

import java.io.File;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "StudyActivity";
    private Context context;
    private Button btnShell, btnService, btnInstall,
            btnExoPlayer, btnVideoPlayer, btnAdPlayer,
            btnTencentLocation;

    /**
     * 当前屏幕旋转角度
     */
    private int mOrientation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "===== onCreate() =====");
        setContentView(R.layout.activity_study);

        context = this;

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            Toast.makeText(context, "竖屏", Toast.LENGTH_LONG).show();
        } else {
            //横屏
            Toast.makeText(context, "横屏", Toast.LENGTH_LONG).show();
        }

        btnShell = findViewById(R.id.btn_shell);
        btnService = findViewById(R.id.btn_service);
        btnInstall = findViewById(R.id.btn_install);
        btnExoPlayer = findViewById(R.id.btn_exo_player);
        btnVideoPlayer = findViewById(R.id.btn_video_player);
        btnAdPlayer = findViewById(R.id.btn_ad_player);
        btnTencentLocation = findViewById(R.id.btn_tencent_location);

        btnShell.setText("旋转屏幕");
        btnShell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShellUtils.su("mount -o remount,rw /system");
//                ShellUtils.su("setprop service.adb.tcp.port 5555", "stop adbd", "start adbd");
//                ShellUtils.su("am install -t -r /sdcard/sjfwz_1.3.0_dangbei.apk");
//                SystemPropProxy.set(context, "service.adb.tcp.port", "6666");

//                XHApiManager xhApiManager = new XHApiManager();
//                xhApiManager.XHInstallOnBackground("/sdcard/sjfwz_1.3.0_dangbei.apk",
//                        "com.zerogame.byod");

//                ArrayList<String> signInfos = AppSigningUtils.getSingInfo(context,
//                        "com.android.settings", AppSigningUtils.MD5);
//                if (null != signInfos) {
//                    for (String tmp : signInfos) {
//                        LogUtils.v(TAG, "===== " + tmp);
//                    }
//                }

                String serialno = SystemPropProxy.get(context, "ro.serialno", "");
                LogUtils.i(TAG, "ro.serialno=" + serialno);

                /**
                 * 本狮
                 */
                // 静默安装
//                Intent  cmdIntent = new Intent("com.zhsd.setting.syscmd");
//                cmdIntent.putExtra("cmd", "appinstall");
//                cmdIntent.putExtra("parm", "/sdcard/sjfwz_1.3.0_dangbei.apk");
//                sendBroadcast(cmdIntent);

                // 旋转屏幕
                final String ROTATION_0 = "android.intent.rotation_0";
                final String ROTATION_90 = "android.intent.rotation_90";
                final String ROTATION_180 = "android.intent.rotation_180";
                final String ROTATION_270 = "android.intent.rotation_270";
                String action = null;
                switch (mOrientation) {
                    case 0:
                        action = ROTATION_90;
                        mOrientation = 90;
                        break;
                    case 90:
                        action = ROTATION_180;
                        mOrientation = 180;
                        break;
                    case 180:
                        action = ROTATION_270;
                        mOrientation = 270;
                        break;
                    case 270:
                        action = ROTATION_0;
                        mOrientation = 0;
                        break;
                    default:
                        action = ROTATION_90;
                        mOrientation = 90;
                        break;
                }
                Intent cmdIntent = new Intent(action);
                sendBroadcast(cmdIntent);
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
                startVideo("ExoPlayer", false);
            }
        });
        btnVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo("VideoView", false);
            }
        });
        btnAdPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVideo("Ad", false);
            }
        });
        btnTencentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, TencentLocationActivity.class);
                startActivity(intent);
            }
        });

        startOrientationChangeListener();
    }


    /**
     * 启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
     * 手动调整不适用
     */
    private void startOrientationChangeListener() {
        OrientationEventListener mOrEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int rotation) {
                LogUtils.i(TAG, "当前屏幕手持角度方法:" + rotation + "°");
                if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
                    rotation = 0;
                } else if ((rotation > 45) && (rotation <= 135)) {
                    rotation = 90;
                } else if ((rotation > 135) && (rotation <= 225)) {
                    rotation = 180;
                } else if ((rotation > 225) && (rotation <= 315)) {
                    rotation = 270;
                } else {
                    rotation = 0;
                }
                if (rotation == mOrientation) {
                    return;
                }
                mOrientation = rotation;

            }
        };
        mOrEventListener.enable();
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

    private void startVideo(String type, boolean useWindow) {
        Intent intent = new Intent();
        intent.setClass(context, VideoActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("useWindow", useWindow);
        startActivity(intent);
    }

}
