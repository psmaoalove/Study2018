
package com.rengh.study.util.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

/**
 * Created by rengh on 17-5-29.
 */
public class AppInfo {
    private Context mContext = null;
    private PackageManager mPackageManager = null;
    private PackageInfo mPackageInfo = null;
    private ApplicationInfo mApplicationInfo = null;

    private PackageInfo mOtherPackageInfo = null;

    /**
     * 初始化AppInfo对象。
     * 
     * @param context 应用上下文，AppInfo自动切换为全局上下文。
     */
    public AppInfo(Context context) {
        mContext = context.getApplicationContext();
        mPackageManager = mContext.getPackageManager();
        mApplicationInfo = mContext.getApplicationInfo();
        try {
            mPackageInfo = mPackageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 设置指定包名，将持有该包名的应用信息。
     * 
     * @param packageName 指定应用的包名
     * @return 对应包名的应用的PackageInfo对象。若未找到指定的包名，返回null.
     */
    public PackageInfo setOtherPackageName(String packageName) {
        mOtherPackageInfo = null;
        try {
            mOtherPackageInfo = mPackageManager.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return mOtherPackageInfo;
    }

    /**
     * 判断当前是否持有指定应用的信息。
     * 
     * @return 是返回true，不是返回false.
     */
    public boolean isOtherApp() {
        if (null == mOtherPackageInfo) {
            return false;
        }
        return true;
    }

    /**
     * 销毁持有的指定包名的应用对象，将自动切换为当前应用。
     */
    public void destroyOtherPackageInfo() {
        mOtherPackageInfo = null;
    }

    /**
     * 获取应用的应用名称
     * 
     * @return 返回应用的应用名称，若isOtherApp()返回true，优先返回指定应用的名称，无法获取时返回null.
     */
    public String getAppName() {
        if (null == mOtherPackageInfo) {
            return (String) mPackageManager.getApplicationLabel(mApplicationInfo);
        } else {
            if (null == mOtherPackageInfo.applicationInfo) {
                return null;
            }
            return (String) mPackageManager.getApplicationLabel(mOtherPackageInfo.applicationInfo);
        }
    }

    /**
     * 获取应用的包名
     * 
     * @return 返回应用的包名，若isOtherApp()返回true，优先返回指定应用的包名。.
     */
    public String getPackageName() {
        if (null == mOtherPackageInfo) {
            return mApplicationInfo.packageName;
        }
        return mOtherPackageInfo.packageName;
    }

    /**
     * 获取应用的版本号
     * 
     * @return 返回应用的版本号，若isOtherApp()返回true，优先返回指定应用的版本号，无法获取时返回当null。
     */
    public String getVersionName() {
        if (null == mOtherPackageInfo) {
            if (null == mPackageInfo) {
                return null;
            }
            return mPackageInfo.versionName;
        }
        return mOtherPackageInfo.versionName;
    }

    /**
     * 获取应用的内部版本号
     * 
     * @return 返回应用的内部版本号，若isOtherApp()返回true，优先返回指定应用的内部版本号，无法获取时返回-1.
     */
    public int getVersionCode() {
        if (null == mOtherPackageInfo) {
            if (null == mPackageInfo) {
                return -1;
            }
            return mPackageInfo.versionCode;
        }
        return mOtherPackageInfo.versionCode;
    }

    /**
     * 获取应用图标资源ID。
     * 
     * @return 返回应用图标的资源ID，应用未设置图标时返回0，若isOtherApp()返回true，优先返回指定应用图标的资源ID，方法执行失败时返回 -1.
     */
    public int getAppIconResId() {
        if (null == mOtherPackageInfo) {
            if (null == mPackageInfo) {
                return -1;
            }
            return mPackageInfo.applicationInfo.icon;
        }
        return mOtherPackageInfo.applicationInfo.icon;
    }

    /**
     * 获取应用图标的Drawable对象。
     * 
     * @return 返回应用图标的Drawable对象，应用未设置图标时返回默认图标，若isOtherApp()返回true， 优先返回指定应用图标的Drawable对象，方法执行失败时返回null .
     */
    public Drawable getAppIconDrawable() {
        if (null == mOtherPackageInfo) {
            if (null == mPackageInfo) {
                return null;
            }
            return mPackageInfo.applicationInfo.loadIcon(mPackageManager);
        }
        return mOtherPackageInfo.applicationInfo.loadIcon(mPackageManager);
    }

}
