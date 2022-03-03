package com.viomi.common;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommonUtil {
    private static final String TAG = "CommonUtil";

    /**
     * 打印堆栈信息 会闪退
     */
    public static void printStack() {
        try {
            Exception exception = new Exception(TAG);
            exception.printStackTrace();
        } catch (Exception e) {

        }
    }

    /**
     * 打印堆栈信息
     */
    public static void printStack(String tag) {
        Throwable throwable = new Throwable();
        StackTraceElement[] stackElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackElements) {
//            Log.i(TAG, "printStackTwo: "+stackTraceElement.getClassName());
            String fileName = stackTraceElement.getFileName();
            if (TextUtils.equals(fileName, "ObservableCreate.java") || TextUtils.equals(fileName, "Handler.java")) {
                break;
            }
            Log.e(tag, "printStack: " + stackTraceElement.getFileName() + " method: " + stackTraceElement.getMethodName() + " line : " + stackTraceElement.getLineNumber());
        }
    }

    /**
     * 当前时间是否和构建事件保持一致
     **/
    public static boolean isBuildTime() {
        // 反射获取系统属性：编译时间
        String buildTime = "";
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            buildTime = (String) getMethod.invoke(classType, new Object[]{"ro.build.date.utc"});
            Log.i(TAG, "isBuildTime: buildTime");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.i(TAG, "isBuildTime: exception : " + e.getMessage());
        }
        if (TextUtils.isEmpty(buildTime)) {
            return false;
        }
        long buildTimeLong = Long.parseLong(buildTime);
        long currentTime = System.currentTimeMillis() / 1000;
        long diffTime = currentTime - buildTimeLong;
        Log.d(TAG, "isBuildTime: curTme : " + currentTime + "  buildTime: " + buildTimeLong + "  diffTime: " + diffTime);
        return diffTime <= 500;
    }
}
