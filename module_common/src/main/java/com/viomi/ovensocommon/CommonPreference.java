package com.viomi.ovensocommon;

import android.provider.Settings;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;

/**
 * 公共缓存区
 */
public class CommonPreference {
    private static final String TAG = "OvenCommonPreference";
    private static final String OVENCOMMON_SP_NAME = "sp_ovencommon";
    // 自动息屏时间
    public static final String KEY_KEEP_SCREEN_TIME = "keepScreenTime";
    // 是否进行语音播报
    public static final String KEY_IS_VOICEREPROT = "keyIsVoiceReportl";
    public static final String SERIAL_DISCONNECT = "serialIsDisconnect";
    public static final String MCU_UPGRADING = "isMcuNeedUpdateGrading";
    private static final int DEFAULT_SCREEN_OFFTIME = Integer.MAX_VALUE;
    private static CommonPreference mInstance;
    private final SPUtils commonSpUtils;

    private CommonPreference() {
        commonSpUtils = SPUtils.getInstance(OVENCOMMON_SP_NAME);
    }

    public static CommonPreference getInstance() {
        if (mInstance == null) {
            synchronized (CommonPreference.class) {
                if (mInstance == null) {
                    mInstance = new CommonPreference();
                }
            }
        }
        return mInstance;
    }

    public void setSpeakEnabel(boolean isOpenSpeak) {
        commonSpUtils.put(KEY_IS_VOICEREPROT, isOpenSpeak);
    }

    public boolean getSpeakEnable() {
        return commonSpUtils.getBoolean(KEY_IS_VOICEREPROT);
    }

    /**
     * 缓存自动设置时间
     *
     * @param auto
     */
    public void saveTimeAuto(boolean auto) {
        try {
            Settings.Global.putInt(Utils.getApp().getContentResolver(),
                    Settings.Global.AUTO_TIME, auto ? 1 : 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean getTimeAuto() {
        boolean auto = true;
        try {
            auto = Settings.Global.getInt(
                    Utils.getApp().getContentResolver(),
                    Settings.Global.AUTO_TIME) == 1;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return auto;
    }

    /**
     * 当前是否处于升级状态
     *
     * @return
     */
    public boolean getUpgrading() {
//        getBoolean(UPGRADING, false)
        return false;
    }

    /**
     * 设置设备空闲状态
     *
     * @param isIdle
     */
    public void setDeviceIdle(boolean isIdle) {
//        setValue(DEVICE_IDLE, isIdle);
    }


    public void setMcuUpgrade(boolean isMucUpgrade) {
        commonSpUtils.put(MCU_UPGRADING, isMucUpgrade);
    }

    public boolean getMcuUpgrade() {
        return commonSpUtils.getBoolean(MCU_UPGRADING, false);
    }

    public void setMcuMode(String model) {
    }

    public void setSerialDisconnect(boolean isDisconnect) {
        commonSpUtils.put(SERIAL_DISCONNECT, isDisconnect);
    }

    public Boolean getSerialDisconnect() {
        return commonSpUtils.getBoolean(SERIAL_DISCONNECT, true);
    }


    public boolean getIsVoiceReprot() {
        boolean isVoiceReport = commonSpUtils.getBoolean(KEY_IS_VOICEREPROT, true);
        return isVoiceReport;
    }

    public int getKeepScreenTime() {
        int keepScreenTime = commonSpUtils.getInt(CommonPreference.KEY_KEEP_SCREEN_TIME, DEFAULT_SCREEN_OFFTIME);
        return keepScreenTime;
    }


    public void setScreenTime(int screenOffTime) {
        commonSpUtils.put(CommonPreference.KEY_KEEP_SCREEN_TIME, screenOffTime);
    }

}
