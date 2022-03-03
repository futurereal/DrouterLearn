package com.viomi.ovenso.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/1/8
 **/
public class TimeUtil {
    private static final SimpleDateFormat mmssFormat = new SimpleDateFormat("mm:ss");// mm:ss
    private static final SimpleDateFormat hhmmssFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
    private static final SimpleDateFormat hhmmFormat = new SimpleDateFormat("HH:mm");// HH:mm
    private static final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm
    private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH");// HH
    private static final SimpleDateFormat minuFormat = new SimpleDateFormat("mm");// mm

    public static String getTimeMMSS() {
        return mmssFormat.format(new Date(System.currentTimeMillis()));
    }

    public static String getTimeMMSS(Date date) {
        return mmssFormat.format(date);
    }

    public static String getTimeHHMMSS(Date date) {
        return hhmmssFormat.format(date);
    }

    public static String getTimeHHMM(Date date) {
        return hhmmFormat.format(date);
    }

    public static String getTimeHHMM(long timeMills) {
        return hhmmFormat.format(new Date(timeMills));
    }

    public static String getTimeYyyyMMdd(long timeMills) {
        return yyyyMMddFormat.format(new Date(timeMills));
    }

    public static String getDiffTimeStr(int diffTimeMinu) {
        long curTimeMills = System.currentTimeMillis();
        long finishTimeMills = curTimeMills + diffTimeMinu * 60 * 1000;
        String timeHHmm = getTimeHHMM(finishTimeMills);
        if (getTimeYyyyMMdd(curTimeMills).equals(getTimeYyyyMMdd(finishTimeMills)))
            return "今天" + timeHHmm;
        else return "明天" + timeHHmm;
    }

    public static String getTimeHHMM(int totalSecond) {

        int hour = totalSecond / 60 / 60;
        int min = totalSecond / 60 % 60;
        return (hour > 9 ? hour : "0" + hour) + ":" + (min > 9 ? min : "0" + min);
    }

    public static String getTimeMMSS(int totalSec) {
        if (totalSec <= 0)
            return "00:00";
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
    }

    public static String getHour(int totalSecond) {
        int hour = totalSecond / 60 / 60;
        return (hour > 9 ? String.valueOf(hour) : "0" + hour);

    }

    public static String getMin(int totalSecond) {
        int min = totalSecond / 60 % 60;
        return (min > 9 ? String.valueOf(min) : "0" + min);

    }

    public static String getSecond(int totalSecond) {
        int second = totalSecond % 60;
        return (second > 9 ? String.valueOf(second) : "0" + second);
    }


    public static int getHour() {
        return Integer.parseInt(hourFormat.format(new Date(System.currentTimeMillis())));
    }

    public static int getMinute() {
        return Integer.parseInt(minuFormat.format(new Date(System.currentTimeMillis())));
    }

    public static String getDayTip(Calendar target) {
        Calendar mCurrentTime = Calendar.getInstance();
        mCurrentTime.setTimeInMillis(System.currentTimeMillis());
        return geDayTip(mCurrentTime, target);
    }

    public static String geDayTip(Calendar cur, Calendar target) {
        if (target.get(Calendar.DAY_OF_YEAR) != cur.get(Calendar.DAY_OF_YEAR)) {
            if ((target.get(Calendar.DAY_OF_YEAR) - cur.get(Calendar.DAY_OF_YEAR)) == 1) {
                return "明天";
            } else if ((target.get(Calendar.DAY_OF_YEAR) - cur.get(Calendar.DAY_OF_YEAR)) == 2) {
                return "后天";
            } else if ((target.get(Calendar.DAY_OF_YEAR) - cur.get(Calendar.DAY_OF_YEAR)) == -1) {
                return "昨天";
            } else if ((target.get(Calendar.DAY_OF_YEAR) - cur.get(Calendar.DAY_OF_YEAR)) == -2) {
                return "前天";
            }
        }
        return "今天";
    }
}
