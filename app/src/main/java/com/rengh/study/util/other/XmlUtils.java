
package com.rengh.study.util.other;

/**
 * XML简易操作工具类
 * 
 * @since 2017.01.24
 * @author rengh
 */
public class XmlUtils {
    public static String getValueByKey(String result, String key) {
        int start = 0;
        int end = 0;
        String startTag = "<" + key + ">";
        if (result.contains(startTag)) {
            start = result.indexOf(startTag) + (startTag).length();
        }
        String endTag = "</" + key + ">";
        if (result.contains(endTag)) {
            end = result.indexOf(endTag);
        }
        if (start <= 0 || end <= 0 || start > end) {
            return null;
        }
        return result.substring(start, end);
    }

}
