
package com.rengh.study.sdk.jniApp;

/**
 * Created by rengh on 17-5-29.
 */
public class JniAppUtils {
    private static final String TAG = "JniAppUtils";

    static {
        try {
            System.loadLibrary("native-lib");
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static native void stringFromJNI(String jobject);
}
