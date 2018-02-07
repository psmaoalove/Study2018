
package com.rengh.library.util.common;

import android.os.Looper;

/**
 * Created by rengh on 17-5-29.
 */
public class ThreadUtils {

    public static boolean isMainThread() throws IllegalStateException {
        Looper mainLooper = Looper.getMainLooper();
        if (null == mainLooper) {
            throw new IllegalStateException();
        }
        Thread mainThread = mainLooper.getThread();
        Thread currThread = Thread.currentThread();
        if (null == mainThread || null == currThread) {
            throw new IllegalStateException();
        }
        long mainThreadId = mainThread.getId();
        long currThreadId = currThread.getId();

        return mainThreadId == currThreadId;
    }

    public static boolean sleep(long mills) {
        try {
            Thread.sleep(mills);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (NoSuchMethodError e) { // 大数据统计到的异常
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
