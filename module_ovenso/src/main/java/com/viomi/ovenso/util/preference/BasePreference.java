package com.viomi.ovenso.util.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.viomi.common.ApplicationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/6/4
 **/
public abstract class BasePreference {
    private final String TAG = "BasePreference";

    //
    public abstract String getPreName();

    //remove key
    public void remove(String key) {
        SharedPreferences pref = getPreferences();
        pref.edit().remove(key).apply();
    }

    public SharedPreferences getPreferences() {
        return ApplicationUtils.getContext().getSharedPreferences(getPreName(), Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getEditor() {
        SharedPreferences pref = getPreferences();
        return pref.edit();
    }

    public void setValue(String name, Object value) {
        Log.d(TAG, "setValue name = " + name + "   value = " + value);
        SharedPreferences.Editor editor = getEditor();
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
        SharedPreferences.Editor editor = getEditor();
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

    ////    String类型
    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String def) {
        SharedPreferences pref = getPreferences();
        String s = pref.getString(key, def);
        return s;
    }

    //// boolean类型
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        SharedPreferences pref = getPreferences();
        boolean value = pref.getBoolean(key, def);
        return value;
    }

    ////    Int类型
    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        SharedPreferences pref = getPreferences();
        int value = pref.getInt(key, def);
        return value;
    }

    ////    long类型
    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long def) {
        SharedPreferences pref = getPreferences();
        long value = pref.getLong(key, def);
        return value;
    }

    ////    float类型
    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public float getFloat(String key, float def) {
        SharedPreferences pref = getPreferences();
        float value = pref.getFloat(key, def);
        return value;
    }

    ////    Set<String>类型
    public Set<String> getStringSet(String key) {
        return getStringSet(key, null);
    }

    public Set<String> getStringSet(String key, Set<String> def) {
        SharedPreferences pref = getPreferences();
        Set<String> value = pref.getStringSet(key, def);
        return value;
    }

    public boolean setValue(String key, Set<String> value) {
        SharedPreferences pref = getPreferences();
        return pref.edit().putStringSet(key, value).commit();
    }

    public void setValueAsync(String key, Set<String> value) {
        SharedPreferences pref = getPreferences();
        pref.edit().putStringSet(key, value).apply();
    }

    ////    ArrayList<String>
    public ArrayList<String> getStringArrayList(String key, String divider) {
        String[] array = getStringArray(key, divider);
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(array));
        return list;
    }

    public void setValue(String key, ArrayList<String> values, String divider) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value);
            sb.append(divider);
        }
        setValue(key, sb.substring(0, sb.length() - divider.length()));
    }

    /////   String[]
    public String[] getStringArray(String key, String divider) {
        String value = getString(key);
        if (value == null) {
            return null;
        }

        String[] values = value.split(divider);
        return values;
    }

    public void setValue(String key, String[] values, String divider) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value);
            sb.append(divider);
        }
        setValue(key, sb.substring(0, sb.length() - divider.length()));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //图片


    //对象


}
