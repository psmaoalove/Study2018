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
import android.widget.Toast;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;

public class PermissionActivity extends Activity {
    private final String TAG = "PermissionActivity";
    private Context context;

    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int RESULT_CODE_CHECK_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        context = this;

        if (checkPermission()) {
            startVideoActivity();
        }
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
                    if (checkPermission()) {
                        startVideoActivity();
                    }
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
                        if (checkPermission()) {
                            startVideoActivity();
                        }
                    } else {
                        Toast.makeText(this, "未开启Overlay权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    private boolean checkPermission() {
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
                return false;
            }
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())), RESULT_CODE_CHECK_PERMISSION);
                denied = true;
            }
            if (denied) {
                return false;
            }
        }
        return true;
    }

    private void startVideoActivity() {
        Intent videoActivity = new Intent();
        videoActivity.setClass(context, VideoActivity.class);
        startActivity(videoActivity);
        finish();
    }

}
