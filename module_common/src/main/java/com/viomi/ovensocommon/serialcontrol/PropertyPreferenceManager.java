package com.viomi.ovensocommon.serialcontrol;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.viomi.vlog.Vlog;

/**
 * Created by Ljh on 2020/8/24.
 * Description:
 */
public class PropertyPreferenceManager {
    private static final String TAG = "PropertyPreference";
    private static final String SP_NAME_PROPERTY = "property_serial";
    private static final String SP_KEY_PREFIX = "property_";
    private static volatile PropertyPreferenceManager instance;
    private final SPUtils spUtils;


    public static PropertyPreferenceManager getInstance() {
        if (instance == null) {
            synchronized (PropertyPreferenceManager.class) {
                if (instance == null) {
                    instance = new PropertyPreferenceManager();
                }
            }
        }
        return instance;
    }

    private PropertyPreferenceManager() {
        spUtils = SPUtils.getInstance(SP_NAME_PROPERTY);
    }


    /**
     * 根据key获取本地SP的缓存属性
     *
     * @param siid         一级属性id
     * @param piid         二级属性id
     * @param defaultValue 默认值
     */
    public Object getProperty(int siid, int piid, Object defaultValue) {
        String key = SP_KEY_PREFIX + siid + "." + piid;
        Log.i(TAG, "getProperty: key:  " + key + "  defaultVaule: " + defaultValue);
        Object finalValue = null;
        if (defaultValue instanceof Long) {
            finalValue = spUtils.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof String) {
            finalValue = spUtils.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            finalValue = spUtils.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            finalValue = spUtils.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            finalValue = spUtils.getFloat(key, (Float) defaultValue);
        }
        Log.i(TAG, "getProperty: " + key + "  finalValue: " + finalValue);
        return finalValue;
    }

    /**
     * 存储属性
     *
     * @param siid
     * @param piid
     * @param value
     */
    public void setProperty(int siid, int piid, Object value) {
        String keyProperty = SP_KEY_PREFIX + siid + "." + piid;
        Log.d(TAG, "saveProp " + siid + "." + piid + "  value:" + value);
        setPropety(keyProperty, value);
    }

    /**
     * 根据key获取本地SP的缓存属性
     *
     * @param sid pid 默认的键
     */
    public boolean judgePropertyChange(int sid, int pid, Object currentValue) {
        String key = SP_KEY_PREFIX + sid + "." + pid;
        if (currentValue instanceof Long) {
            long longValue = spUtils.getLong(key, -1);
            Log.i(TAG, "judgePropertyChange: longVaule " + currentValue);
            return longValue != (long) currentValue;
        } else if (currentValue instanceof String) {
            String value = spUtils.getString(key, "");
            return !TextUtils.equals(value, currentValue.toString());
        } else if (currentValue instanceof Integer) {
            int intVaule = spUtils.getInt(key, -1);
            return intVaule != (int) currentValue;
        } else if (currentValue instanceof Boolean) {
            boolean saveVaule = spUtils.getBoolean(key, false);
            return true;
        } else if (currentValue instanceof Float) {
            float floatVaule = spUtils.getFloat(key, -1);
            return floatVaule != (float) currentValue;
        }
        return false;
    }


    private void setPropety(String name, Object value) {
        Vlog.i(TAG, "setValue name = " + name + "   value = " + value);
        if (value instanceof Boolean) {
            spUtils.put(name, (Boolean) value);
        } else if (value instanceof String) {
            spUtils.put(name, (String) value);
        } else if (value instanceof Integer) {
            spUtils.put(name, (Integer) value);
        } else if (value instanceof Long) {
            spUtils.put(name, (Long) value);
        } else if (value instanceof Float) {
            spUtils.put(name, (Float) value);
        }
    }
}

