package com.rengh.study.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rengh.study.util.common.LogUtils;
import com.rengh.study.util.common.ServiceUtils;

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
        LogUtils.i(TAG, "===== onCreate() =====");
        ServiceUtils.startForeground(this, 1);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.i(TAG, "===== onStart() =====");
        // TODO
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "===== onStartCommand() =====");
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "===== onDestroy() =====");
        ServiceUtils.stopForeground(this, false);
    }

}
