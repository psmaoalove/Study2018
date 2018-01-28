package com.rengh.study.receiver;

import com.rengh.study.util.common.LogUtils;
import com.rengh.study.window.MyWindowManager;
import com.rengh.study.window.view.ExoWindowView;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AppService extends Service {
    private final String TAG = "AppService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "===== onCreate() =====");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.v(TAG, "===== onStart() =====");
        // TODO
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.v(TAG, "===== onStartCommand() =====");
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "===== onCreate() =====");
    }

}
