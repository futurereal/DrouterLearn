package com.viomi.modulesetting.utils;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.R;
import com.viomi.ovensocommon.CommonAffirmFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.router.core.ViomiRouter;

/**
 * @description:
 * @data:2021/11/12
 */
public class ModuleSettingUtil {
    private static final String TAG = "ModuleSettingUtil";
    public static final int DEFAULT_STEP_VOLUME = 6;
    public static final int DEFAULT_SELECTED_POSITION = 0;
    public static final int TIME_UNIT = 1000;
    public static String secondStr = ApplicationUtils.getContext().getResources().getString(R.string.sound_lock_second);
    public static String minuteStr = ApplicationUtils.getContext().getResources().getString(R.string.minute);
    public static String closeStr = ApplicationUtils.getContext().getResources().getString(R.string.common_close);

    /**
     * 通过时间 来获取 时间的名称
     * 1 、小于 1分钟 以秒为   2 、大于1分钟 以分钟为单位 3 Integer.max 显示关闭
     *
     * @param screenOffTime
     * @return
     */
    public static String getScreenOffTimeName(int screenOffTime) {
        Log.i(TAG, "getScreenOffTimeName: screenOffTime: " + screenOffTime);
        String screenOffTimeName = "";
        if (screenOffTime <CommonConstant.ONE_MINITE_SECOND * TIME_UNIT) {
            screenOffTimeName = screenOffTime / TIME_UNIT + secondStr;
        } else if (screenOffTime == Integer.MAX_VALUE || screenOffTime == Integer.MAX_VALUE / TIME_UNIT) {
            screenOffTimeName = closeStr;
        } else {
            screenOffTimeName = screenOffTime / TIME_UNIT / CommonConstant.ONE_MINITE_SECOND + minuteStr;
        }
        Log.i(TAG, "getScreenOffTimeName:  screenOffTime: " + screenOffTime + "  screenOffTimeName: " + screenOffTimeName);
        return screenOffTimeName;
    }

    public static int getScreenOffTime(String name) {
        Log.i(TAG, "getScreenOffTime: name: " + name);
        int screenOffTime = 0;
        if (name.contains(secondStr)) {
            int nameIndex = name.indexOf(secondStr);
            screenOffTime = Integer.parseInt(name.substring(0, nameIndex)) * TIME_UNIT;
        } else if (name.contains(minuteStr)) {
            int nameIndex = name.indexOf(minuteStr);
            screenOffTime = Integer.parseInt(name.substring(0, nameIndex)) * CommonConstant.ONE_MINITE_SECOND * TIME_UNIT;
        } else if (name.contains(closeStr)) {
            screenOffTime = Integer.MAX_VALUE;
        }
        return screenOffTime;
    }

    public static boolean checkNetAndShowTip() {
        boolean isNetNotConnected = !NetworkUtils.isConnected();
        Log.i(TAG, "checkNetAndShowTip: isNetNotConnected: " + isNetNotConnected);
        if (isNetNotConnected) {
            CommonAffirmFragment commonAffirmFragment = new CommonAffirmFragment();
            Resources applicationResource = ApplicationUtils.getContext().getResources();
            Bundle bundle = CommonAffirmFragment.getBundle(applicationResource.getString(R.string.net_title),
                    applicationResource.getString(R.string.net_content), applicationResource.getString(R.string.notsure),
                    applicationResource.getString(R.string.sure), false, 0);
            commonAffirmFragment.setArguments(bundle);
            commonAffirmFragment.setPositiveClickListener(dialog -> {
                if (ScreenUtils.isLandscape()) {
                    ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_COMMON_SETTING)
                            .withInt(ViomiRouterConstant.SETTING_KEY_MENUINDEX, CommonConstant.MENU_INDEX_WIFI)
                            .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                            .navigation();
                } else {
                    ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                            .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                            .navigation();
                }
                dialog.dismiss();
            });
            commonAffirmFragment.setNegativeClickListener(dialog -> {
                dialog.dismiss();
            });
            FragmentActivity topActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            commonAffirmFragment.show(topActivity.getSupportFragmentManager(), "msgList");
        }
        return isNetNotConnected;
    }

    public static String getFirmVersionStr(int firmVersionCode) {
        String firmVersionStr = String.valueOf(firmVersionCode);
        int firmVersionLength = firmVersionStr.length();
        if (firmVersionLength < 4) {
            for (int i = firmVersionLength; i < 4; i++) {
                firmVersionStr = "0" + firmVersionStr;
            }
        }
        return firmVersionStr;
    }

}
