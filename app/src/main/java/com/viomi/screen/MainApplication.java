package com.viomi.screen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Configuration;

import com.viomi.common.VLogUtil;
import com.viomi.ovensocommon.utils.ApplicationManager;

import java.util.Iterator;

/**
 * @description:
 * @data:2021/7/26
 */
public class MainApplication extends Application implements Configuration.Provider {
    private static final String TAG = "MainApplication";
    private static MainApplication mainApplication;

    public static MainApplication getInstance() {
        return mainApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
        Log.i(TAG, "onCreate");
        registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }
    //初始化主题，主题从模块获取；，初始化日志，执行绑定逻辑，有些需要主界面加载之后在线程里面，服务初始化，处理器空闲初始化。
    public static Context getContext() {
        return mainApplication;
    }
    @Override
    protected void attachBaseContext(Context baseContext) {
        super.attachBaseContext(baseContext);
        Log.i(TAG, "attachBaseContext: " + baseContext);
        VLogUtil.init(baseContext);
        if (isMainProcess(baseContext)) {
            ApplicationManager.initOtherModuleApplication(baseContext);
        }
    }

    private boolean isMainProcess(Context context) {
        Log.i(TAG, "isMainProcess: " + context);
        String packageName = context.getPackageName();
        Log.i(TAG, "isMainProcess: packageName " + packageName);
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Service.ACTIVITY_SERVICE);
        Iterator var3 = activityManager.getRunningAppProcesses().iterator();
        ActivityManager.RunningAppProcessInfo processInfo;
        do {
            if (!var3.hasNext()) {
                return false;
            }
            processInfo = (ActivityManager.RunningAppProcessInfo) var3.next();
        } while (processInfo.pid != pid);
        Log.i(TAG, "isMainProcess: processName： " + processInfo.processName);
        return TextUtils.equals(processInfo.processName, packageName);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        Log.i(TAG, "getWorkManagerConfiguration: ");
        Configuration configuration = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.VERBOSE)
                .build();
        return configuration;
    }

    private final ActivityLifecycleCallbacks mLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            Log.i(TAG, "onActivityCreated: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            Log.i(TAG, "onActivityStarted: ");
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            Log.i(TAG, "onActivityResumed: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            Log.i(TAG, "onActivityPaused: " + activity.getClass().getSimpleName());

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            Log.i(TAG, "onActivityStopped: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            Log.i(TAG, "onActivitySaveInstanceState: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            Log.i(TAG, "onActivityDestroyed: " + activity.getClass().getSimpleName());
        }
    };


}
