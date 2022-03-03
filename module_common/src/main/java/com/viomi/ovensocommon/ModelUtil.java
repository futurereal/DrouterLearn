package com.viomi.ovensocommon;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;

public class ModelUtil {
    private static final String TAG = "ModelUtil";
    //蒸烤箱so6
    public static final String DEVICE_MODEL_OVENSO6 = "viomi.oven.so6";
    public static final int DEVICE_SO6_PID = 431;
    //微蒸烤so7
    // miot 的绑定，每次更改model  要全部clean 一下，否则sdk 有缓存 还是原来的型号
    public static final String DEVICE_MODEL_OVENSO7 = "viomi.oven.so7";
    public static final int DEVICE_SO7_PID = 1536;
    //净水器e1   edison
    public static final String DEVICE_MODEL_WATERERO = "viot.water-purifier.e1";
    public static final int DEVICE_WATERE1_PID = 273;
    //烤箱的包名
    private static final String PACKAGENAME_OVENSO = "com.viomi.ovenso.microwave";
    // 净水器ERO的包名
    private static final String PACKAGENAME_WATER_ERO = "com.viomi.waterpurifier.edison";
    //后台申请的升级包的名称
    private static final String APK_NAME_WATER = "WaterEdisonLauncher";
    private static final String APK_NAME_0VENSO = "OvensoLauncher";

    public static int getViomiPid() {
        int productId = 0;
        String productModelName = getModelName();
        Log.i(TAG, "getProductId: productModelName: " + productModelName);
        if (TextUtils.equals(DEVICE_MODEL_OVENSO6, productModelName)) {
            productId = DEVICE_SO6_PID;
        } else if (TextUtils.equals(DEVICE_MODEL_OVENSO7, productModelName)) {
            productId = DEVICE_SO7_PID;
        } else if (TextUtils.equals(DEVICE_MODEL_WATERERO, productModelName)) {
            productId = DEVICE_WATERE1_PID;
        }
        Log.i(TAG, "getProductId: productId: " + productId);
        return productId;
    }

    /**
     * 获取型号
     */
    public static String getModelName() {
        String modelName = (String) PropertyPreferenceManager.getInstance().getProperty(1, 2, "");
        Log.i(TAG, "getModelName: modelName: " + modelName);
        // 如果没有连接电控板，modelName 为空
        if (TextUtils.isEmpty(modelName)) {
            String packageName = AppUtils.getAppPackageName();
            if (TextUtils.equals(packageName, PACKAGENAME_OVENSO)) {
                modelName = DEVICE_MODEL_OVENSO7;
            } else if (TextUtils.equals(packageName, PACKAGENAME_WATER_ERO)) {
                modelName = DEVICE_MODEL_WATERERO;
            }
        }
        Log.i(TAG, "getModelName: modelName: final  " + modelName);
        return modelName;
    }

    public static String getApkName() {
        String modelName = getModelName();
        if (TextUtils.equals(modelName, DEVICE_MODEL_OVENSO7)) {
            return APK_NAME_0VENSO;
        }
        if (TextUtils.equals(modelName, DEVICE_MODEL_WATERERO)) {
            return APK_NAME_WATER;
        }
        return APK_NAME_0VENSO;
    }

}
