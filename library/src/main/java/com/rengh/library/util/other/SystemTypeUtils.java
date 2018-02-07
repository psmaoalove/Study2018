
package com.rengh.library.util.other;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 系统类型判断工具类
 *
 * @author RenGuanghui
 * @since 2017.01.24
 */
public class SystemTypeUtils {
    private static boolean mDebug = false;
    public static final String DEVICE_TYPE_UNKNOW = "Unkonw";
    public static final String DEVICE_TYPE_WINDOWS = "Windows";
    public static final String DEVICE_TYPE_LINUX = "Linux";
    public static final String DEVICE_TYPE_MAC = "Mac";

    public static void enableDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * 获取当前系统类型
     *
     * @return 返回当前系统的类型
     * 支持Win系统、Linux系统和Mac系统。DEVICE_TYPE_WINDOWS、DEVICE_TYPE_LINUX、DEVICE_TYPE_MAC
     * and DEVICE_TYPE_UNKNOW.
     */
    public static String getSystemType() {
        String type = null;
        String os = System.getProperty("os.name");
        if (null == os) {
            type = "null";
        } else if (os.toLowerCase(Locale.getDefault()).startsWith("win")) {
            type = DEVICE_TYPE_WINDOWS;
        } else if (os.toLowerCase(Locale.getDefault()).startsWith("linux")) {
            type = DEVICE_TYPE_LINUX;
        } else if (os.toLowerCase(Locale.getDefault()).startsWith("mac")) {
            type = DEVICE_TYPE_MAC;
        } else {
            type = DEVICE_TYPE_UNKNOW;
        }

        if (mDebug) {
            System.out.println("Type: " + os);
        }
        return type;
    }

    /**
     * 设置Window系统的时间
     *
     * @param date 要设置的日期
     * @return true or false
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean setSystemDateForWindows(Date date) throws IOException,
            InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return setSystemDateForWindows(sdf.format(date));
    }

    /**
     * 设置Window系统的时间
     *
     * @param time 要设置的日期时间戳，毫秒值类型
     * @return true or false
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean setSystemDateForWindows(long time) throws IOException,
            InterruptedException {
        if (time > 0) {
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return setSystemDateForWindows(sdf.format(date));
        }

        if (mDebug) {
            System.out.println("Set system time: time <= 0");
        }
        return false;
    }

    /**
     * 设置Window系统的时间
     *
     * @param date 要设置的日期，String类型，格式："yyyy-MM-dd HH:mm:ss"
     * @return true or false
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean setSystemDateForWindows(String date) throws IOException,
            InterruptedException {
        boolean success = false;
        String[] timeArr = date.split(" ");
        if (2 == timeArr.length) {
            if (mDebug) {
                System.out.println("Set system time date:" + timeArr[0] + " time:" + timeArr[1]
                        + "...");
            }
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("cmd /c date " + timeArr[0]);
            Thread.sleep(1000);
            runtime.exec("cmd /c time " + timeArr[1]);
            success = true;
            if (mDebug) {
                System.out.println("Set system time success.");
            }
        } else {
            success = false;
            if (mDebug) {
                System.out.println("Set system time error.");
            }
        }

        return success;
    }
}
