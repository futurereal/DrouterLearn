package com.viomi.modulesetting.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.vlog.Vlog;

/**
 * 设置缓存
 * Created by William on 2018/2/22.
 */
public class SettingPreference {
    private static final String TAG = "SettingPreference";
    private static volatile SettingPreference mInstance;
    private static final String SETTING_SP_NAME = "modulesetting";
    public static final String UPDATE_DESC = "updateDesc";// 更新版本说明
    public static final String MCU_UPGRADING = "isMcuNeedUpdateGrading";
    public static final String DEVICE_IDLE = "deviceIdle";
    public static final String SPEECH_ENABLE = "SPEECH_ENABLE";

    public static SettingPreference getInstance() {
        if (mInstance == null) {
            synchronized (SettingPreference.class) {
                if (mInstance == null) {
                    mInstance = new SettingPreference();
                }
            }
        }
        return mInstance;
    }

    private SharedPreferences getSharedPreferences() {
        return Utils.getApp().getSharedPreferences(
                SETTING_SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 设置设备空闲状态
     *
     * @param isIdle
     */
    public void setDeviceIdle(boolean isIdle) {
        setValue(DEVICE_IDLE, isIdle);
    }


    /**
     * 存储属性
     *
     * @param siid
     * @param piid
     * @param value
     */
    public void saveProp(int siid, int piid, Object value) {
        Log.d(TAG, "saveProp " + siid + "." + piid + "  value:" + value);
        setValueSync(ModuleSettingConstants.SP_PROP + siid + "." + piid, value);
    }

    public void setValue(String name, Object value) {
        Vlog.i(TAG, "setValue name = " + name + "   value = " + value);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value).apply();
        } else if (value instanceof String) {
            editor.putString(name, (String) value).apply();
        } else if (value instanceof Integer) {
            editor.putInt(name, (Integer) value).apply();
        } else if (value instanceof Long) {
            editor.putLong(name, (Long) value).apply();
        } else if (value instanceof Float) {
            editor.putFloat(name, (Float) value).apply();
        }
    }

    public void setValueSync(String name, Object value) {
        Vlog.i(TAG, "setValueSync name = " + name + "   value = " + value);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value).commit();
        } else if (value instanceof String) {
            editor.putString(name, (String) value).commit();
        } else if (value instanceof Integer) {
            editor.putInt(name, (Integer) value).commit();
        } else if (value instanceof Long) {
            editor.putLong(name, (Long) value).commit();
        } else if (value instanceof Float) {
            editor.putFloat(name, (Float) value).commit();
        }
    }

    public long getLong(String key, long defaultValue) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getLong(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultV) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean(key, defaultV);
    }

    public float getFloat(String key, float defaultValue) {
        SharedPreferences sp = getSharedPreferences();
        return sp.getFloat(key, defaultValue);
    }

    public void remove(String key) {
        SharedPreferences sp = getSharedPreferences();
        sp.edit().remove(key).apply();
    }
}
