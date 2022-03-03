package com.viomi.waterpurifier.edison.util;

import android.content.res.Resources;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.waterpurifier.edison.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/1/8
 **/
public class TimeUtil {
    private static final String TAG = "TimeUtil";
    private static final SimpleDateFormat mmssFormat = new SimpleDateFormat("mm:ss");// mm:ss
    private static final SimpleDateFormat hhmmssFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
    private static final SimpleDateFormat hhmmFormat = new SimpleDateFormat("hh:mm");// HH:mm
    private static final SimpleDateFormat hHmmFormat = new SimpleDateFormat("HH:mm");// HH:mm
    private static final SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm
    private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH");// HH
    private static final SimpleDateFormat minuFormat = new SimpleDateFormat("mm");// mm

    private static final int[] dateIds = new int[]{R.string.date_change_one, R.string.date_change_two, R.string.date_change_three,
            R.string.date_change_four, R.string.date_change_five, R.string.date_change_six};

    private static final int[] dateTimes = new int[]{12 * 60, 1 * 24 * 60, 3 * 24 * 60, 7 * 24 * 60, 15 * 24 * 60, 30 * 24 * 60};

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
        return hHmmFormat.format(date);
    }

    public static String getTimehhmm(long timeMills) {
        return hhmmFormat.format(new Date(timeMills));
    }

    public static String getTimeHHMM(long timeMills) {
        return hHmmFormat.format(new Date(timeMills));
    }

    public static String getTimeYyyyMMdd(long timeMills) {
        return yyyyMMddFormat.format(new Date(timeMills));
    }

    public static String getDiffTimeStr(int diffTimeMinu) {
        long curTimeMills = System.currentTimeMillis();
        long finishTimeMills = curTimeMills + diffTimeMinu * 60 * 1000;
        String timeHHmm = getTimeHHMM(finishTimeMills);
        if (getTimeYyyyMMdd(curTimeMills).equals(getTimeYyyyMMdd(finishTimeMills))) {
            return "今天" + timeHHmm;
        } else {
            return "明天" + timeHHmm;
        }
    }

    public static String getTimeHHMM(int totalMinu) {
        int hour = totalMinu / 60;
        int min = totalMinu % 60;
        return (hour > 9 ? hour : "0" + hour) + ":" + (min > 9 ? min : "0" + min);
    }

    public static String getTimeMMSS(int totalSec) {
        if (totalSec <= 0) {
            return "00:00";
        }
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
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

    public static List<String> getAllChangeDate() {
        Resources resources = ApplicationUtils.getContext().getResources();
        List<String> changeDateList = new ArrayList<>(dateIds.length);
        for (int dateId : dateIds) {
            changeDateList.add(resources.getString(dateId));
        }
        Log.i(TAG, "getAllChangeDate: " + changeDateList.size());
        return changeDateList;
    }

    public static String getChangeDateName(int index) {
        List<String> changeDateList = getAllChangeDate();
        return changeDateList.get(index);
    }

    public static int getChangeDate(int index) {
        return dateTimes[index];
    }
}
