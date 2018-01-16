package com.rengh.study.study.android;

/**
 * Base包:设计模式、Java基础、Android基础、数据结构等等。
 * Created by rengh on 18-1-15.
 */
public class AndroidHelper {
    private static AndroidHelper sInstance = null;

    private AndroidHelper() {
    }

    public static AndroidHelper getInstance() {
        if (null == sInstance) {
            synchronized (AndroidHelper.class) {
                if (null == sInstance) {
                    sInstance = new AndroidHelper();
                }
            }
        }
        return sInstance;
    }
}
