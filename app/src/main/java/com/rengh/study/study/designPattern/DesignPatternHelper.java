package com.rengh.study.study.designPattern;

/**
 * Base包:设计模式、Java基础、Android基础、数据结构等等。
 * Created by rengh on 18-1-15.
 */
public class DesignPatternHelper {
    private static DesignPatternHelper sInstance = null;

    private DesignPatternHelper() {
    }

    public static DesignPatternHelper getInstance() {
        if (null == sInstance) {
            synchronized (DesignPatternHelper.class) {
                if (null == sInstance) {
                    sInstance = new DesignPatternHelper();
                }
            }
        }
        return sInstance;
    }
}
