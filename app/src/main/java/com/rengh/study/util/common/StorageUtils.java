
package com.rengh.study.util.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class StorageUtils {
    private static final String TAG = "StorageUtils";
    /** 应用文件根目录，更新为缓存目录 */
    private static String FILE_DIR_APP;
    /** 应用Log保存目录 */
    private static String FILE_DIR_APP_LOG;
    /** 应用crash保存目录 */
    private static String FILE_DIR_APP_CRASH;
    private static String FILE_DIR_APP_APK;

    private static boolean sIsInit = false;

    public static void initExtDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File fileDir = context.getExternalFilesDir(null);
            if (null != fileDir) {
                FILE_DIR_APP = fileDir.getAbsolutePath() + File.separator;
            }
            File logDir = context.getExternalFilesDir("log");
            if (null != logDir) {
                FILE_DIR_APP_LOG = logDir.getAbsolutePath() + File.separator;
            }
            File crashDir = context.getExternalFilesDir("crash");
            if (null != crashDir) {
                FILE_DIR_APP_CRASH = crashDir.getAbsolutePath() + File.separator;
            }
            File apkDir = context.getExternalFilesDir("apk");
            if (null != apkDir) {
                FILE_DIR_APP_APK = apkDir.getAbsolutePath() + File.separator;
            }
            sIsInit = true;
        } else {
            LogUtils.w(TAG, "The external storage state is not MEDIA_MOUNTED.");
        }
    }

    /**
     * app 文件存储根目录
     */
    public static String getAppDir(Context context) {
        if (null == context) {
            return null;
        }
        if (!sIsInit) {
            initExtDir(context);
        }
        if (!sIsInit) {
            return null;
        }
        return FILE_DIR_APP;
    }

    /**
     * Log dir
     */
    public static String getLogDir(Context context) {
        if (null == context) {
            return null;
        }
        if (!sIsInit) {
            initExtDir(context);
        }
        if (!sIsInit) {
            return null;
        }
        return FILE_DIR_APP_LOG;
    }

    /**
     * Crash dir
     */
    public static String getCrashDir(Context context) {
        if (null == context) {
            return null;
        }
        if (!sIsInit) {
            initExtDir(context);
        }
        if (!sIsInit) {
            return null;
        }
        return FILE_DIR_APP_CRASH;
    }

    /**
     * 更新包存放位置
     */
    public static String getApkDir(Context context) {
        if (null == context) {
            return null;
        }
        if (!sIsInit) {
            initExtDir(context);
        }
        if (!sIsInit) {
            return null;
        }
        return FILE_DIR_APP_APK;
    }

    public static File getInternalFileDir(Context context) {
        if (null == context) {
            return null;
        }
        if (!sIsInit) {
            initExtDir(context);
        }
        if (!sIsInit) {
            return null;
        }
        return context.getFilesDir();
    }

}
