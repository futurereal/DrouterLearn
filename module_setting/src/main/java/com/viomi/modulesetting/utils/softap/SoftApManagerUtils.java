package com.viomi.modulesetting.utils.softap;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bailing
 * date 2020/9/8
 * description：设备热点管理
 */
public class SoftApManagerUtils {
    private static final String TAG = "SoftApManagerUtils";

    /**
     * 设置softAp的configuration
     *
     * @param wifiManager wifiManager
     * @param wifiConfig  wifiConfiguration
     * @return boolean
     */
    public static boolean setWifiApConfiguration(WifiManager wifiManager, WifiConfiguration wifiConfig) {
        boolean flag = false;
        try {
            Method method = WifiManager.class.getMethod("setWifiApConfiguration", WifiConfiguration.class);
            flag = (Boolean) method.invoke(wifiManager, wifiConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 开启SoftAp,第二个参数为空时，先调用setWifiApConfiguration()方法
     *
     * @param wifiManager wifiManager
     * @param wifiConfig  null
     * @return boolean
     */
    public static boolean startSoftAp(WifiManager wifiManager, WifiConfiguration wifiConfig) {
        Log.i(TAG, "startSoftAp: ");
        boolean flag = false;
        try {
            Method method = WifiManager.class.getMethod("startSoftAp", WifiConfiguration.class);
            flag = (Boolean) method.invoke(wifiManager, wifiConfig);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "startSoftAp: Exception : " + e.getMessage());
        }
        return flag;
    }

    /**
     * 关闭SoftAp
     *
     * @param wifiManager wifiManager
     * @return boolean
     */
    public static boolean stopSoftAp(WifiManager wifiManager) {
        Log.i(TAG, "stopSoftAp: ");
        boolean flag = false;
        try {
            Method method = WifiManager.class.getMethod("stopSoftAp");
            flag = (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            Log.i(TAG, "stopSoftAp:Exception：" + e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 热点是否开启
     *
     * @param wifiManager wifiManager
     * @return boolean
     */
    public static boolean isWifiApEnabled(WifiManager wifiManager) {
        boolean flag = false;
        try {
            Method method = WifiManager.class.getMethod("isWifiApEnabled");
            flag = (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 设置热点开关状态
     *
     * @param enabled 开关
     */
    public static void setWifiApEnabled(Context context, boolean enabled) {
        Log.i(TAG, "setWifiApEnabled: enabled : " + enabled);
        Intent intent = new Intent();
        intent.setAction("com.viomi.device.action.softap.onoff");
        intent.putExtra("onoff", enabled);
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }

    /**
     * 切换当前热点频段
     *
     * @param hertz 1:2.4G hz，2:5G hz
     */
    public static void changeHertz(Context context, int hertz) {
        Intent intent = new Intent();
        intent.setAction("com.viomi.device.action.softap.freqmode");
        intent.putExtra("freqmode", hertz);
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }

    /**
     * 获取当前连接的设备列表
     */
    public static List<HotSpotDevice> getDeviceList(Context context) {
        List<HotSpotDevice> list = new ArrayList<>();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse("content://com.viomi.device.provider/clientinfo"), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    HotSpotDevice softApDeviceData = new HotSpotDevice();
                    softApDeviceData.setDevName(cursor.getString(cursor.getColumnIndex("dev_name")));
                    softApDeviceData.setDevMac(cursor.getString(cursor.getColumnIndex("dev_mac")));
                    softApDeviceData.setDevIp(cursor.getString(cursor.getColumnIndex("dev_ip")));
                    list.add(softApDeviceData);
                }
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 设置wifi名称及密码
     *
     * @param context  context
     * @param ssid     ssid
     * @param password pwd
     */
    public static void setSsidPwd(Context context, String ssid, String password) {
        Intent intent = new Intent();
        intent.setAction("com.viomi.device.action.softap.ssidpwd");
        intent.putExtra("ssid", ssid);
        intent.putExtra("password", password);
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }

    /**
     * 获取wifi热点数据
     *
     * @param context Context
     */
    public static SoftApData getSoftApData(Context context) {
        SoftApData softApData = new SoftApData();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse("content://com.viomi.device.provider/solftap"), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    softApData.setWifiSsid(cursor.getString(cursor.getColumnIndex("wifi_ssid")));
                    softApData.setWifiPwd(cursor.getString(cursor.getColumnIndex("wifi_pwd")));
                    softApData.setWifiIp(cursor.getString(cursor.getColumnIndex("wifi_ip")));
                    softApData.setWifiMac(cursor.getString(cursor.getColumnIndex("wifi_mac")));
                    softApData.setWifiOnOff(cursor.getInt(cursor.getColumnIndex("wifi_onoff")));
                }
                cursor.close();
            }
        }
        return softApData;
    }

    public static final String BLE_MESH_SP_NAME = "module_ble_mesh";

    public static String getBleMeshSpString(final Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(BLE_MESH_SP_NAME, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(defaultValue)) {
            return sp.getString(key, "");
        } else {
            return sp.getString(key, defaultValue);
        }
    }

    /**
     * 返回当前连接的wifi是2.4  还是  5
     * 1：2.4     2：5
     *
     * @param context
     * @return
     */
    public static int getWifiHertzMode(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int frequency = wifiInfo.getFrequency();
        boolean is24G = is24GHzWifi(frequency);
        boolean is5G = is5GHzWifi(frequency);
        if (is5G) {
            return 2;
        } else if (is24G) {
            return 1;
        } else {
            return 1;
        }
    }

    /**
     * 判断是否2.4Gwifi
     *
     * @param frequency
     * @return
     */
    private static boolean is24GHzWifi(int frequency) {
        return frequency > 2400 && frequency < 2500;
    }

    /**
     * 判断是否5Gwifi
     *
     * @param frequency
     * @return
     */
    private static boolean is5GHzWifi(int frequency) {
        return frequency > 4900 && frequency < 5900;
    }
}
