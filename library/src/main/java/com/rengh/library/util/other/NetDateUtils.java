
package com.rengh.library.util.other;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimeZone;

/**
 * 网络时间获取工具类。
 * 
 * @since 2017.01.24
 * @author rengh
 * 该工具从各大网站同步时间，某个网站可以获取到时间时，不会继续获取其他网站的时间。由于各网站的时间精度不一致，获取的毫秒值精度可能只能精确到秒。
 */
public class NetDateUtils {
    private static final int COUNT = 2; // 为了得到准确的时间，该值最少为2
    private static final int TIME_OUT = 1000;
    private static final String[] NTP_HOST = {
            "http://ark.cp21.ott.cibntv.net/", "http://www.baidu.com/", "http://www.qq.com/",
            "http://www.sohu.com/", "http://www.163.com/", "http://www.mi.com/",
            "http://www.google.com/", "http://www.apple.com/", "http://www.youtube.com/",
            "http://www.facebook.com/"
    };

    private static boolean mDebug = false;
    private static String mUrl = null;

    public static void enableDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * 获取校验时间的Url
     * 
     * @return 返回成功获取时间的url，或者返回null，当未成功获取时间时。
     */
    public static String getUrl() {
        return mUrl;
    }

    /**
     * 获取某个时区的时间
     * 
     * @param timeZone "GMT+8" 指定时区。
     * @return 返回毫秒为单位的时间戳，但精度为秒。-1: 网站时间不正确。0: 网站无法访问或不支持获取时间。
     */
    public static long getNetworkTime(String timeZone) {
        if (null != timeZone) {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        }
        return getNetworkTime();
    }

    /**
     * 获取网站时间
     * 
     * @return 返回毫秒为单位的时间戳，但精度为秒。-1: 网站时间不正确。0: 网站无法访问或不支持获取时间。
     */
    public static long getNetworkTime() {
        long ld = 0;

        for (int i = 0; i < NTP_HOST.length; i++) {
            try {
                ld = getNetworkTime(new URL(NTP_HOST[i]));
                if (ld > 0) {
                    if (mDebug) {
                        System.out.println("Success: " + ld + " " + NTP_HOST[i]);
                    }
                    mUrl = NTP_HOST[i];
                    break;
                } else {
                    if (mDebug) {
                        System.out.println("Fail: " + ld + " " + NTP_HOST[i]);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ld;
    }

    /**
     * 获取网站时间的具体实现
     * 
     * @return 返回毫秒为单位的时间戳，但精度为秒。-1: 网站时间不正确。0: 网站无法访问或不支持获取时间。
     */
    private static long getNetworkTime(URL url) {
        long bak0 = 0;
        long ld = 0;

        for (int i = 0; i < COUNT; i++) {
            try {
                URLConnection uc;
                uc = url.openConnection();
                uc.setConnectTimeout(TIME_OUT);
                ld = uc.getDate();

                if (mDebug) {
                    System.out.println("Count: " + i + ", Time: " + ld);
                }

                if (ld == 0) {
                    break;
                }

                if (i == 0) {
                    bak0 = ld;
                } else if (ld == bak0) {
                    ld = -1;
                    break;
                }

                if (i == COUNT - 1) {
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ld;
    }
}
