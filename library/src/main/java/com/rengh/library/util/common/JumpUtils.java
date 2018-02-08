
package com.rengh.library.util.common;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 应用安全启动方法封装
 *
 * @author lap
 * @since All
 */
public class JumpUtils {
    private static final String TAG = "LapRemoteStartUtils";

    private JumpUtils() {
    }

    /**
     * 乐视电视、盒子通用跳转，广播跳转。
     *
     * @param context 上下文
     * @param jump    跳转链接
     * @param from    来源：包名
     * @return true或false
     */
    public static boolean jump2OtherAppByBroadcast(Context context, String jump, String from) {
        LogUtils.i(TAG, "jumLinker=" + jump);
        if (null == context) {
            return false;
        }
        Intent intent = getJumpIntent(context, jump, from);
        if (null != intent) {
            context.sendBroadcast(intent);
            return true;
        }
        return false;
    }

    /**
     * 乐视电视、盒子通用跳转，不兼容广播形式。
     *
     * @param context 上下文
     * @param jump    跳转链接
     * @param from    来源：包名
     * @return true或false
     */
    public static boolean jump2OtherApp(Context context, String jump, String from) {
        LogUtils.i(TAG, "jumLinker=" + jump);
        if (null == context) {
            return false;
        }
        boolean success = false;
        Intent intent = getJumpIntent(context, jump, from);
        if (null != intent) {
            success = SystemUtils.startActivitySafely(context, intent);
        }
        return success;
    }

    /**
     * 乐视电视、盒子通用跳转(支持跳转返回指定应用)，不兼容广播形式。
     *
     * @param context 上下文
     * @param intents 从跳转地返回时的指定应用intent
     * @param jump    跳转链接
     * @param from    通用跳转来源：包名
     * @return true或false
     */
    public static boolean jump2OtherApp(Context context, Intent[] intents, String jump, String from) {
        LogUtils.i(TAG, "jumLinker=" + jump);
        if (null == context) {
            return false;
        }
        boolean success = false;
        if (TextUtils.isEmpty(jump)) {
            success = SystemUtils.startActivitysSafely(context, intents);
        } else {
            Intent intent = getJumpIntent(context, jump, from);
            if (null != intent) {
                if (null != intents && intents.length > 0) {
                    Intent[] intents2 = new Intent[intents.length + 1];
                    for (int i = 0; i < intents2.length - 1; i++) {
                        intents2[i] = intents[i];
                    }
                    intents2[intents2.length - 1] = intent;
                    success = SystemUtils.startActivitysSafely(context, intents2);
                } else {
                    success = SystemUtils.startActivitySafely(context, intent);
                }
            }
        }
        return success;
    }

    /**
     * 通过跳转链接生成对应的Intent
     *
     * @param context 上下文
     * @param linker  跳转链接
     * @param from    来源
     * @return Intent
     */
    public static Intent getJumpIntent(Context context, String linker, String from) {
        if (null == context || TextUtils.isEmpty(linker)) {
            return null;
        }
        try {
            JSONObject json = new JSONObject(linker);
            JSONObject params = json.optJSONObject("params");
            if (null == params) {
                String text = json.getString("params");
                params = new JSONObject(text);
            }
            String action = params.optString("action");
            String type = params.optString("type");
            String value = params.optString("value");
            if (TextUtils.isEmpty(from)) {
                // from = params.optString("from");
                from = context.getPackageName();
            }

            if (!TextUtils.isEmpty(action)) {
                Intent intent = new Intent();
                intent.setAction(action);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", Integer.parseInt(type));
                intent.putExtra("value", value);
                intent.putExtra("from", from);
                return intent;
            }
        } catch (JSONException e) {
            LogUtils.i(TAG, "JSONException=" + e.getMessage());
        } catch (NullPointerException e) {
            LogUtils.i(TAG, "NullPointerException=" + e.getMessage());
        } catch (Exception e) {
            LogUtils.i(TAG, "Exception=" + e.getMessage());
        }
        return null;
    }

}
