package com.viomi.ovensocommon.utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Application
 */
public class ApplicationManager {
    private static final String TAG = "ApplicationManager";

    // moduelSetting  application
    public static boolean MODULESET_LOAD = true;
    public static String MODULESET_APPLICATION = "com.viomi.modulesetting.ModuleSettingApplicaiton";
    // water   applicaiotn
    public static boolean WATER_LOAD = true;
    public static String WATER_PACKAGE = "com.viomi.waterpurifier.edison";
    public static String WATER_APPLICATON = "com.viomi.waterpurifier.edison.WaterApplication";
    // ovenso  applicaiotn
    public static boolean OVENSO_LOAD = true;
    public static String OVENSO_APPLICATION = "com.viomi.ovenso.OvenApplication";
    public static String OVENSO_PACKAGE = "com.viomi.ovenso.microwave";
    // ovenso  applicaiotn
    public static boolean CAMERA_LOAD = true;
    public static String CAMERA_PACKAGE = "com.viomi.ovenso.microwave";
    public static String CAMERA_APPLICATION = "com.viomi.camera.CameraApplicaiton";
    // miot applicaiton
    public static boolean MIOT_LOAD = true;
    public static String MIOT_APPLICATION = "com.miotspecv2.defined.viomi.MiotApplicaiton";

    public static void initOtherModuleApplication(Context context) {
        Log.i(TAG, "initOtherModuleApplication: context : " + context);
        String packageName = context.getPackageName();
        Log.i(TAG, "initOtherModuleApplication: " + packageName);
        if (TextUtils.equals(packageName, OVENSO_PACKAGE) && OVENSO_LOAD) {
            attachModuleApplicaiton(context, OVENSO_APPLICATION);
        }
        if (CAMERA_LOAD) {
            attachModuleApplicaiton(context, CAMERA_APPLICATION);
        }
        // 净水器 不需要上米家
        if (TextUtils.equals(packageName, WATER_PACKAGE) && WATER_LOAD) {
            attachModuleApplicaiton(context, WATER_APPLICATON);
            MIOT_LOAD = false;
        }
        // 最后初始化，有些数据需要依赖之前模块
        if (MODULESET_LOAD) {
            attachModuleApplicaiton(context, MODULESET_APPLICATION);
        }
        if (MIOT_LOAD) {
            attachModuleApplicaiton(context, MIOT_APPLICATION);
        }
    }
    //映射获取ModuleApplication
    private static void attachModuleApplicaiton(Context context, String applicaitonName) {
        Log.i(TAG, "attachModuleApplicaiton: " + applicaitonName);
        Application moduleApplication = null;
        try {
            ClassLoader classLoader = context.getClassLoader();
            if (classLoader != null) {
                Class<?> mClass = classLoader.loadClass(applicaitonName);
                if (mClass != null)
                    moduleApplication = (Application) mClass.newInstance();
            }
        } catch (Exception e) {
            Log.i(TAG, "attachModuleApplicaiton: classLoader exception: " + e.getMessage());
        }
        Log.i(TAG, "attachModuleApplicaiton: " + moduleApplication);
        if (moduleApplication == null) {
            Log.i(TAG, "attachModuleApplicaiton: application is null return");
            return;
        }
        try {
            //通过反射调用moduleApplication的attach方法
            Method method = Application.class.getDeclaredMethod("attach", Context.class);
            Log.i(TAG, "attachBaseContext: mathod ： " + method);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(moduleApplication, context);
            }
        } catch (Exception e) {
            Log.i(TAG, "attachModuleApplicaiton: invokeException: ");
        }
    }
}
