
package com.rengh.library.util.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by rengh on 17-10-23.
 */

public class BroadcastUtils {

    /**
     * 判断是否有应用注册指定的广播（仅支持静态注册）
     *
     * @param context 上下文
     * @param intent 需要查询的intent，需要设置action
     * @return true或false
     */
    public static boolean isRegisteBroadcast(Context context, Intent intent) {
        List<ResolveInfo> list = getRegisteBroadcastList(context, intent);
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 获取注册指定intent的接受者列表，仅支持静态注册。
     *
     * @param context 上下文
     * @param intent 需要查询的intent
     * @return null或list
     */
    public static List<ResolveInfo> getRegisteBroadcastList(Context context, Intent intent) {
        if (null == context || null == intent || TextUtils.isEmpty(intent.getAction())) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> results = pm.queryBroadcastReceivers(intent, 0);
        return results;
    }

}
