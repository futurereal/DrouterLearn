package com.viomi.waterpurifier.edison;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.vlog.Vlog;

/**
 * Copyright (C), 2014-2019, 佛山云米科技有限公司
 *
 * @ProjectName: viomi_face
 * @Package: com.viomi.apm.library.utils
 * @ClassName: WaterPreference
 * @Description: Preference工具类
 * @Author: randysu
 * @CreateDate: 2019-05-23 17:34
 * @UpdateUser:
 * @UpdateDate: 2019-05-23 17:34
 * @UpdateRemark:
 * @Version: 1.0
 */
public class WaterPreference {
    public static final String KEY_THEME_CHANGETIME = "keyThemeChangeTime";
    private static final String TAG = "WaterPreference";
    public static final String WATER_SP_NAME = "waterPurifier_sp";
    public static final String KEY_24HOUR_SWITCH = "sp_key_24hour_switch";
    public static final String KEY_KEEP_SCREEN = "keep_screen"; // 保持屏幕，阻止锁屏
    public static final String KEY_FIRST_OUT_TIME = "first_out_time";

    public static final String KEY_THEME_CURRENT_INDEX = "currentThemeIndex";
    public static final String KEY_THEME_AUTO_INDEX = "autoThemeIndexes";
    public static final String KEY_THEME_SWITCH = "keyIsThemeAutoSwitch";
    public static final String KEY_CUSTOMMODE_INDEX = "keyCustomIndex";
    private static WaterPreference mInstance;
    private final SPUtils waterSpUtils;
    private WaterPreference() {
        waterSpUtils = SPUtils.getInstance(WATER_SP_NAME);
    }
    public static WaterPreference getInstance() {
        if (mInstance == null) {
            synchronized (CommonPreference.class) {
                if (mInstance == null) {
                    mInstance = new WaterPreference();
                }
            }
        }
        return mInstance;
    }
    public void setWaterProperty(String name, Object value) {
        Vlog.i(TAG, "setValue name = " + name + "   value = " + value);
        if (value instanceof Boolean) {
            waterSpUtils.put(name, (Boolean) value);
        } else if (value instanceof String) {
            waterSpUtils.put(name, (String) value);
        } else if (value instanceof Integer) {
            waterSpUtils.put(name, (Integer) value);
        } else if (value instanceof Long) {
            waterSpUtils.put(name, (Long) value);
        } else if (value instanceof Float) {
            waterSpUtils.put(name, (Float) value);
        }
    }

    /**
     * 根据key获取本地SP的缓存属性
     */
    public Object getWaterProperty(String key, Object defValue) {
        Object finalValue = null;
        if (defValue instanceof Long) {
            finalValue = waterSpUtils.getLong(key, (Long) defValue);
        } else if (defValue instanceof String) {
            finalValue = waterSpUtils.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            finalValue = waterSpUtils.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            finalValue = waterSpUtils.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            finalValue = waterSpUtils.getFloat(key, (Float) defValue);
        }
        Log.i(TAG, "getWaterProperty: " + key + "  value: " + finalValue);
        return finalValue;
    }

    public void clear() {
        waterSpUtils.clear();
    }

}
