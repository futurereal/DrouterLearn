package com.viomi.iotdevice.main.iot_device_lib.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.viomi.iotdevice.main.iot_device_lib.ViomiApplication;


/**
 * 公共工具类
 *
 * @author William
 * @date 2018/1/20
 */
public class ToolUtil {
    private static final String TAG = ToolUtil.class.getSimpleName();

    /**
     * 上传米家版本号
     */
    public static String getMiVersion() {
        try {
            PackageManager manager = ViomiApplication.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(ViomiApplication.getContext().getPackageName(), 0);
            return "3.0.3_0" + info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * int ip 地址格式化
     */
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 网络是否已连接
     */
    public static boolean isNetworkConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ViomiApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isConnected() && networkinfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取屏幕厂家
     */
    public static String getFridgeFactory() {
        String type = null;
        try {
            type = SystemPropertiesProxy.get(ViomiApplication.getContext(), "ro.hw.info");
        } catch (Exception e) {
            Log.e(TAG, "getFridgeFactory error!msg=" + e.getMessage());
            e.printStackTrace();
        }
        Log.i("getFridgeFactory", "factory=" + type);
        return type;
    }
}
