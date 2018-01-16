package com.rengh.study.study.java;

/**
 * Base包:设计模式、Java基础、Android基础、数据结构等等。
 * Created by rengh on 18-1-15.
 */
public class JavaHelper {
    private static JavaHelper sInstance = null;

    private JavaHelper() {
    }

    public static JavaHelper getInstance() {
        if (null == sInstance) {
            synchronized (JavaHelper.class) {
                if (null == sInstance) {
                    sInstance = new JavaHelper();
                }
            }
        }
        return sInstance;
    }
}
