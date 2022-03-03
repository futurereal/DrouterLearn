package com.viomi.ovensocommon.utils;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.viomi.common.ApplicationUtils;

/**
 * @description:
 * @data:2022/1/17
 */
public class TimeFormateUtil {
    private static final String TAG = "TimeFormateUtil";
    private static final String FORMATE_AUTO = null;
    private static final String FORMATE_TWENTY_FOUR = "24";
    private static final String FORMATE_TWELVE = "12";
    private static final int TIME_AUTO = 1;
    private static final int TIME_AUTO_CLOSE = 0;

    public static boolean isTwentyFourFormate() {
        boolean twentyFourFourFormate = false;
        String timeFormate = Settings.System.getString(ApplicationUtils.getContext().getContentResolver(),
                Settings.System.TIME_12_24);
        if (TextUtils.equals(timeFormate, FORMATE_AUTO) || TextUtils.equals(timeFormate, FORMATE_TWENTY_FOUR)) {
            twentyFourFourFormate = true;
        }
        return twentyFourFourFormate;
    }

    public static boolean setFormateTwentyFour() {
        boolean setResult = Settings.System.putString(ApplicationUtils.getContext().getContentResolver(), Settings.System.TIME_12_24, FORMATE_TWENTY_FOUR);
        return setResult;
    }

    public static boolean setFormateTwelve() {
        boolean setResult = Settings.System.putString(ApplicationUtils.getContext().getContentResolver(), Settings.System.TIME_12_24, FORMATE_TWELVE);
        return setResult;
    }

    public static boolean setTimeAuto(boolean isTimeAuto) {
        boolean setResult = Settings.Global.putInt(Utils.getApp().getContentResolver(),
                Settings.Global.AUTO_TIME, isTimeAuto ? TIME_AUTO : TIME_AUTO_CLOSE);
        return setResult;
    }

    public static boolean isTimeAuto() {
        boolean timeAuto = false;
        try {
            int timeAutoValue = Settings.Global.getInt(Utils.getApp().getContentResolver(), Settings.Global.AUTO_TIME);
            Log.i(TAG, "isTimeAuto: ");
            timeAuto = timeAutoValue == TIME_AUTO;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return timeAuto;
    }


}
