
package com.rengh.study.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rengh.study.R;
import com.rengh.study.util.common.LogUtils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class TencentLocationActivity extends AppCompatActivity implements TencentLocationListener {
    private final String TAG = TencentLocationActivity.class.getName();
    private TextView mTvLocationInfo;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tencent_location);
        LogUtils.i(TAG, "===== onCreate() =====");

        mTvLocationInfo = (TextView) findViewById(R.id.tv_location);

        if (Build.VERSION.SDK_INT < 23) {
            startRequestLocation();
        } else {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            } else {
                startRequestLocation();
            }
        }
    }

    @Override
    public void onRestart() {
        LogUtils.i(TAG, "onRestart(): isFinishing=" + isFinishing());
        super.onRestart();
    }

    @Override
    public void onStart() {
        LogUtils.i(TAG, "onStart(): isFinishing=" + isFinishing());
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtils.i(TAG, "onResume(): isFinishing=" + isFinishing());
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.i(TAG, "onPause(): isFinishing=" + isFinishing());
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.i(TAG, "onStop(): isFinishing=" + isFinishing());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "onDestroy(): isFinishing=" + isFinishing());
        endRequestLocation();
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 可在此继续其他操作。
        LogUtils.i(TAG, "request=" + requestCode + " permissions=" + permissions + " results="
                + grantResults);
        switch (requestCode) {
            case 0: {
                if (permissions != null && grantResults != null
                        && permissions.length == grantResults.length) {
                    for (int i = 0; i < permissions.length; i++) {
                        LogUtils.i(TAG, "permission=" + permissions[i] + " result="
                                + grantResults[i]);
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            endRequestLocation();
            LogUtils.i(TAG, "onLocationChanged() 纬度=" + location.getLatitude()); // 纬度
            LogUtils.i(TAG, "onLocationChanged() 经度=" + location.getLongitude()); // 经度
            LogUtils.i(TAG, "onLocationChanged() 海拔=" + location.getAltitude()); // 海拔
            LogUtils.i(TAG, "onLocationChanged() 精度=" + location.getAccuracy()); // 精度
            LogUtils.i(TAG, "onLocationChanged() 名称=" + location.getName()); // 名称
            LogUtils.i(TAG, "onLocationChanged() 地址=" + location.getAddress()); // 地址
            StringBuilder locationInfo = new StringBuilder();
            locationInfo.append("纬度=" + location.getLatitude());
            locationInfo.append("\n经度=" + location.getLongitude());
            locationInfo.append("\n海拔=" + location.getAltitude());
            locationInfo.append("\n精度=" + location.getAccuracy());
            locationInfo.append("\n名称=" + location.getName());
            locationInfo.append("\n地址=" + location.getAddress());
            mTvLocationInfo.setText(locationInfo.toString());
        } else {
            // 定位失败
            LogUtils.i(TAG, "onLocationChanged() error=" + error + " reason=" + reason);
            mTvLocationInfo.setText("onLocationChanged() 定位失败 error=" + error + "reson=" + reason);
        }
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        LogUtils.i(TAG, "onStatusUpdate() name=" + name + " status=" + status + " reason=" + desc);
    }

    private void startRequestLocation() {
        LogUtils.i(TAG, "startRequestLocation()");
        // 请求定位
        TencentLocationManager locationManager = TencentLocationManager.getInstance(getBaseContext());
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
        int error = locationManager.requestLocationUpdates(request, this);
        LogUtils.i(TAG, "startRequestLocation() error=" + error);
        if (0 == error) {
            LogUtils.i(TAG, "startRequestLocation() registe success.");
            mTvLocationInfo.setText("注册监听器成功");
        } else {
            LogUtils.i(TAG, "startRequestLocation() registe failed.");
            mTvLocationInfo.setText("注册监听器失败: " + error);
        }
    }

    private void endRequestLocation() {
        LogUtils.i(TAG, "endRequestLocation()");
        // 停止定位
        TencentLocationManager locationManager = TencentLocationManager
                .getInstance(getBaseContext());
        locationManager.removeUpdates(this);
    }

}
