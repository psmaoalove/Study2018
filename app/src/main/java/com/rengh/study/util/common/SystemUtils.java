
package com.rengh.study.util.common;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 应用工具，用来判断应用状态、获取其他应用上下文、启动其他应用等操作。
 * 
 * @author rengh
 * @see "need <uses-permission android:name="android.permission.GET_TASKS"/>"
 * @see "only can be use by system app on new android system."
 */
public class SystemUtils {
    private static final String TAG = "SystemUtils";

    /**
     * 获取顶端activity的包名，Android新版本需要系统应用才可判断
     *
     * @param context 上下文
     * @return String pkg或null
     * @Description 获取顶端activity的包名
     */
    public static String getTopPkg(Context context) {
        String pkg = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (null != tasks && tasks.size() > 0) {
            RunningTaskInfo info = tasks.get(0);
            if (null != info) {
                ComponentName base = info.topActivity;
                if (null != base) {
                    pkg = base.getPackageName();
                }
            }
        }
        return pkg;
    }

    /**
     * 判断应用是否在最前端显示，Android新版本需要系统应用才可判断
     * 
     * @param context 上下文
     * @param destPkgName 目标包名
     * @return boolean true为在最顶层，false为否
     * @Description: 判断activity是否在最顶层
     */
    public static boolean isTop(Context context, String destPkgName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (null != tasks && tasks.size() > 0) {
            RunningTaskInfo info = tasks.get(0);
            if (null != info) {
                ComponentName base = info.topActivity;
                if (null != base) {
                    String pkgName = base.getPackageName();
                    if (!TextUtils.isEmpty(pkgName) && pkgName.equals(destPkgName)) {
                        isRunning = true;
                    }
                }
            }
        }
        return isRunning;
    }

    /**
     * 判断应用是否有Activity活着，Android新版本需要系统应用才可判断
     * 
     * @param context 上下文
     * @param destPkgName 要判断的应用
     * @return boolean true为在运行，false为已结束
     * @Description: 判断应用是否有activity在运行
     */
    public static boolean isRuning(Context context, String destPkgName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<RunningTaskInfo> tasks = am.getRunningTasks(100);
        if (null != tasks && tasks.size() > 0) {
            for (RunningTaskInfo info : tasks) {
                if (null == info) {
                    continue;
                }
                ComponentName base = info.baseActivity;
                if (null == base) {
                    continue;
                }
                String pkgName = base.getPackageName();
                if (!TextUtils.isEmpty(pkgName) && pkgName.equals(destPkgName)) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    /**
     * 启动某个应用
     * 
     * @param context 调用者上下文
     * @param pkgName 目标应用包名
     * @return true或false
     */
    public static boolean startApplication(Context context, String pkgName) {
        return startApplication(context, pkgName, null);
    }

    /**
     * 启动某个应用
     * 
     * @param context 调用者上下文
     * @param pkgName 目标应用包名
     * @param options 启动选项
     * @return true或false
     */
    public static boolean startApplication(Context context, String pkgName, Bundle options) {
        if (null == context || TextUtils.isEmpty(pkgName)) {
            return false;
        }
        Intent intent = null;
        PackageManager packageManager = context.getPackageManager();
        if (null != packageManager) {
            intent = packageManager.getLaunchIntentForPackage(pkgName);
        }
        return startActivity(context, intent, options);
    }

    private static boolean startActivity(Context context, Intent intent, Bundle options) {
        if (null != intent) {
            try {
                intent.putExtra("from", "com.stv.bootadmanager");
                // context.startActivity(intent, options);
                context.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
            } catch (SecurityException e) {
            }
        }
        return false;
    }

    /**
     * 强制结束指定应用
     * 
     * @see <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
     * @see <uses-permission android:name="android.permission.FOCE_STOP_PACKAGES" />
     */
    public static void forceStopPackage(Context context, String pkgName) {
        LogUtils.i(TAG, "forceStopPackage: " + pkgName);
        try {
            ActivityManager mActivityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod(
                    "forceStopPackage", String.class);
            method.invoke(mActivityManager, pkgName);
        } catch (IllegalArgumentException e) {
            LogUtils.e(TAG, "forceStopPackage() IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.e(TAG, "forceStopPackage() Exception: " + e.getMessage());
        }
    }

    /**
     * 获取指定应用的上下文
     * 
     * @param context 调用者的上下文
     * @param destPkgName 目标应用的包名
     * @return Context 目标应用上下文，找不到包名时返回null。
     */
    public static Context getOtherContext(Context context, String destPkgName) {
        Context otherContext = null;
        try {
            otherContext = context.createPackageContext(destPkgName,
                    Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            LogUtils.e(TAG, "NameNotFoundException: " + e.getMessage());
        } catch (SecurityException e) {
            LogUtils.e(TAG, "FileNotFoundException: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return otherContext;
    }
}