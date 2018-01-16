package com.rengh.study.study;

/**
 * Base包:设计模式、Java基础、Android基础、数据结构等等。
 * Created by rengh on 18-1-15.
 */
public class StudyHelper {
    private static StudyHelper sInstance = null;

    private StudyHelper() {
    }

    public static StudyHelper getInstance() {
        if (null == sInstance) {
            synchronized (StudyHelper.class) {
                if (null == sInstance) {
                    sInstance = new StudyHelper();
                }
            }
        }
        return sInstance;
    }
}
