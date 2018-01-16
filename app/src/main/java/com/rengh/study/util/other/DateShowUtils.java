
package com.rengh.study.util.other;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间展示工具类，如星期一、上周、昨天等
 * 
 * @since 2017.01.24
 * @author rengh
 */
public class DateShowUtils {
    private static DateShowUtils sInstance;

    private SimpleDateFormat mSdfDate;
    private SimpleDateFormat mSdfTime;

    private String mTimeFormatDate;
    private String mTimeFormatTime;

    private String TIME_FORMAT_NOW;
    private String TEXT_YESTERDAY;
    private String[] WEEK_DAY_ARRAY;

    public static void main(String[] args) {
        DateShowUtils dateUtil = DateShowUtils.getInstance();
        long nowTime = System.currentTimeMillis() - 1000l * 60 * 60 * 24 * 9000;
        String text = null;

        /* yyyy/MM/dd HH:mm:ss */
        text = dateUtil.getShow(nowTime);
        System.out.println(text);

        /* 特定表示：刚刚、昨天、星期x、xxxx年xx月xx日 */
        try {
            text = dateUtil.getShowText(nowTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(text);
    }

    public static DateShowUtils getInstance() {
        if (null == sInstance) {
            sInstance = new DateShowUtils();
        }
        return sInstance;
    }

    public DateShowUtils() {
        mTimeFormatDate = "yyyy年MM月dd日";
        mTimeFormatTime = "HH时mm分ss秒";
        TIME_FORMAT_NOW = "刚刚";
        TEXT_YESTERDAY = "昨天";
        WEEK_DAY_ARRAY = new String[] {
                "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",
        };

        mSdfDate = new SimpleDateFormat(mTimeFormatDate, Locale.getDefault());
        mSdfTime = new SimpleDateFormat(mTimeFormatTime, Locale.getDefault());
    }

    /** yyyy/MM/dd HH:mm:ss */
    public String getShow(String milliseconds) throws NumberFormatException {
        if (null == milliseconds || "".equals(milliseconds)) {
            return getShow(System.currentTimeMillis());
        }
        return getShow(Long.valueOf(milliseconds));
    }

    /** yyyy/MM/dd HH:mm:ss */
    public String getShow(long milliseconds) {
        return formatDate(milliseconds) + " " + formatTime(milliseconds);
    }

    /** 传入毫秒值，返回应该显示的文本 */
    public String getShowText(String milliseconds) throws NumberFormatException, ParseException {
        if (null == milliseconds || "".equals(milliseconds)) {
            return getShowText(System.currentTimeMillis());
        }
        return getShowText(Long.valueOf(milliseconds));
    }

    /** 传入毫秒值，返回应该显示的文本 */
    public String getShowText(long milliseconds) throws ParseException {
        String time = null;
        if (isToday(milliseconds)) {
            // 日期中不使用'刚刚'，设置为0
            boolean isNow = (System.currentTimeMillis() - milliseconds) < 1000l * 60 * 1;
            if (isNow) {
                time = TIME_FORMAT_NOW;
            } else {
                time = formatTime(milliseconds);
            }
        } else if (isYesterday(milliseconds)) {
            time = TEXT_YESTERDAY + " " + formatTime(milliseconds);
        } else if (isInWeek(milliseconds)) {
            time = getWeekOfDate(milliseconds) + " " + formatTime(milliseconds);
        } else {
            time = formatDate(milliseconds);
        }
        return time;
    }

    /** 根据时间毫秒值获取格式化的时间，精确到天 */
    private String formatDate(long milliseconds) {
        return mSdfDate.format(milliseconds);
    }

    /** 根据时间毫秒值获取格式化的时间 */
    private String formatTime(long milliseconds) {
        return mSdfTime.format(milliseconds);
    }

    /** 比较日期的大小 */
    private int compareDate(String date1, String date2) throws ParseException {
        Date d1 = null;
        Date d2 = null;
        d1 = mSdfDate.parse(date1);
        d2 = mSdfDate.parse(date2);
        return d1.compareTo(d2);
    }

    /** 判断目标毫秒值的日期是否是今天 */
    private boolean isToday(long milliseconds) throws ParseException {
        String destDate = formatDate(milliseconds);
        String todayDate = getTodayDate();
        if (0 == compareDate(destDate, todayDate)) {
            return true;
        } else {
            return false;
        }
    }

    /** 判断目标毫秒值的日期是否是昨天 */
    private boolean isYesterday(long milliseconds) throws ParseException {
        String destDate = formatDate(milliseconds);
        String yestodayDate = getYesterdayDate();
        if (0 == compareDate(destDate, yestodayDate)) {
            return true;
        } else {
            return false;
        }
    }

    /** 判断日期是否在一周内 */
    private boolean isInWeek(long milliseconds) throws ParseException {
        String destDay = formatDate(milliseconds);
        String weekDay = getWeekFirstDay();
        int result = compareDate(destDay, weekDay);
        if (result >= 0) {
            return true;
        }
        return false;
    }

    /** 获得日期是星期几 */
    private String getWeekOfDate(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return WEEK_DAY_ARRAY[w];
    }

    /** 返回格式：2015-07-08 */
    private String getTodayDate() {
        return mSdfDate.format(System.currentTimeMillis());
    }

    /** 获取昨天的日期，精确到天 */
    private String getYesterdayDate() {
        return mSdfDate.format(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000l);
    }

    /** 获取本周第一天 */
    private String getWeekFirstDay() {
        return mSdfDate.format(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000l);
    }
}
