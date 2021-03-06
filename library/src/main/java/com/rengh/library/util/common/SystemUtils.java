
package com.rengh.library.util.common;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
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
     * 获取app version code
     *
     * @return version code
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            return 0;
        }
    }

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
     * @param context     上下文
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
     * @param context     上下文
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
        if (null == context || TextUtils.isEmpty(pkgName)) {
            return false;
        }
        Intent intent = null;
        PackageManager packageManager = context.getPackageManager();
        if (null != packageManager) {
            intent = packageManager.getLaunchIntentForPackage(pkgName);
        }
        return startActivitySafely(context, intent);
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
     * @param context     调用者的上下文
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

    /**
     * 获取进程名
     *
     * @param context 上下文
     * @return null or name
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps != null && !runningApps.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }


    /**
     * 安全启动指定Activity
     *
     * @param context        上下文
     * @param activityIntent 目标Activity的Intent对象
     * @return 成功返回true，失败返回false
     */
    public static boolean startActivitySafely(Context context, Intent activityIntent) {
        if (null == context || null == activityIntent) {
            return false;
        }
        try {
            context.startActivity(activityIntent);
            LogUtils.i(TAG, "startActivitySafely() start activity success." + activityIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            LogUtils.e(TAG, "startActivitySafely() ActivityNotFoundException: " + e.getMessage());
        } catch (NullPointerException e) {
            LogUtils.i(TAG, "startActivitySafely() NullPointerException=" + e.getMessage());
        } catch (Exception e) {
            LogUtils.e(TAG, "startActivitySafely() Exception: " + e.getMessage());
        }
        LogUtils.i(TAG, "startActivitySafely() start activity failed." + activityIntent);
        return false;
    }

    /**
     * 安全启动指定Activity
     *
     * @param context 上下文
     * @param intents 目标Activity数组
     * @return 成功返回true，失败返回false
     */
    public static boolean startActivitysSafely(Context context, Intent[] intents) {
        boolean success = false;
        try {
            if (null != intents && intents.length > 0) {
                context.startActivities(intents);
                success = true;
            }
        } catch (ActivityNotFoundException e) {
            LogUtils.i(TAG, "ActivityNotFoundException=" + e.getMessage());
        } catch (NullPointerException e) {
            LogUtils.i(TAG, "NullPointerException=" + e.getMessage());
        } catch (Exception e) {
            LogUtils.i(TAG, "Exception=" + e.getMessage());
        }
        return success;
    }

    /**
     * 安全启动目标Service
     *
     * @param context       上下文
     * @param serviceIntent 目标Service的Intent对象
     * @return 成功返回true，失败返回false
     */
    public static boolean startServiceSafely(Context context, Intent serviceIntent) {
        if (null == context || null == serviceIntent) {
            return false;
        }
        try {
            ComponentName comName = context.startService(serviceIntent);
            if (null != comName) {
                LogUtils.i(TAG,
                        "startServiceSafely() start service success. {" + comName.getClassName()
                                + "}");
                return true;
            } else {
                LogUtils.i(TAG, "startServiceSafely() service not exists: " + serviceIntent);
            }
        } catch (SecurityException e) {
            LogUtils.e(TAG, "startServiceSafely() SecurityException: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.e(TAG, "startServiceSafely() Exception: " + e.getMessage());
        }
        LogUtils.i(TAG, "startServiceSafely() start service failed." + serviceIntent);
        return false;
    }

}
