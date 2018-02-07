package com.rengh.study.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rengh.library.util.common.LogUtils;
import com.rengh.study.service.AppService;

/**
 * Created by rengh on 18-1-29.
 */

public class MyReceiver extends BroadcastReceiver {
    private final String TAG = "AppService";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "===== onReceive() =====");
        Intent service = new Intent();
        service.setClass(context, AppService.class);
        context.startService(service);
    }
}
