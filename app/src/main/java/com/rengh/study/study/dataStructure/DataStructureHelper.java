package com.rengh.study.study.dataStructure;

/**
 * Base包:设计模式、Java基础、Android基础、数据结构等等。
 * Created by rengh on 18-1-15.
 */
public class DataStructureHelper {
    private static DataStructureHelper sInstance = null;

    private DataStructureHelper() {
    }

    public static DataStructureHelper getInstance() {
        if (null == sInstance) {
            synchronized (DataStructureHelper.class) {
                if (null == sInstance) {
                    sInstance = new DataStructureHelper();
                }
            }
        }
        return sInstance;
    }
}
