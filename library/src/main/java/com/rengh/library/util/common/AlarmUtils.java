package com.rengh.library.util.common;

import android.app.AlarmManager;
import android.app.PendingIntent;

/**
 * Created by rengh on 18-2-7.
 */
public class AlarmUtils {
    private AlarmUtils() {
    }

    @SuppressWarnings("NewApi")
    public static boolean setExact(AlarmManager am, int type, long triggerAtMillis, PendingIntent operation) {
        try {
            am.setExact(type, triggerAtMillis, operation);
            return true;
        } catch (NoSuchMethodError var6) {
            am.set(type, triggerAtMillis, operation);
            return false;
        }
    }
}
