package com.rengh.study.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rengh.study.service.AppService;

/**
 * Created by rengh on 18-1-29.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent();
        service.setClass(context, AppService.class);
        context.startService(service);
    }
}
