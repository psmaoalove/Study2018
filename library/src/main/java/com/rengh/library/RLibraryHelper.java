package com.rengh.library;

/**
 * Created by rengh on 18-2-7.
 */

public class RLibraryHelper {
    private static RLibraryHelper instance = null;

    public static RLibraryHelper getInstance() {
        if (null == instance) {
            synchronized (RLibraryHelper.class) {
                if (null == instance) {
                    instance = new RLibraryHelper();
                }
            }
        }
        return instance;
    }

    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public String getLibraryId() {
        return BuildConfig.APPLICATION_ID;
    }
}
