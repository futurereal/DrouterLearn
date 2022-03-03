package com.viomi.modulesetting;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.viomi.common.VLogUtil;
import com.viomi.modulesetting.service.ModuleSettingService;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.router.core.ViomiRouter;

import java.util.concurrent.TimeUnit;

public class ModuleSettingApplicaiton extends Application implements Configuration.Provider {
    private static final String TAG = "ModuleSettingApp";
    private static UserInfoDb userInfoDb;

    @Override
    public void onCreate() {
        super.onCreate();
        VLogUtil.init(this);
        ViomiRouter.init(this, true);
        Fresco.initialize(this);
        Log.i(TAG, "onCreate: ");
        initCommonSetting(this);
    }

    public static void setUserInfoDb(UserInfoDb userInfoDbOrg) {
        Log.i(TAG, "setUserInfoDb: ");
        userInfoDb = userInfoDbOrg;
    }

    public static UserInfoDb getUserInfoDb() {
        Log.i(TAG, "getUserInfoDb: ");
        return userInfoDb;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "attachBaseContext: " + base);
        initCommonSetting(base);
    }

    private void initCommonSetting(Context context) {
        Log.i(TAG, "initCommonSetting: " + context);
        // 这个初始化要比较早MainActivity 要用
        ModuleSettingServiceFactory.getInstance().setViotService(new ModuleSettingService());
        new Handler().post(() -> {
            // 启动任务 加急工作
            OneTimeWorkRequest moduleSettingRequest = new OneTimeWorkRequest.Builder(ModuleSettingWork.class)
//                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)  // 加了启动不了要查找原因
                    .build();
            Constraints viotConstraints = new Constraints.Builder()
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            OneTimeWorkRequest viotWorkRequest = new OneTimeWorkRequest.Builder(ViotWork.class)
                    .setInitialDelay(300, TimeUnit.MILLISECONDS)
                    .setConstraints(viotConstraints)
                    .build();
            WorkManager.getInstance(context).beginWith(moduleSettingRequest).then(viotWorkRequest).enqueue();
        });
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        Configuration configuration = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.VERBOSE)
                .build();
        return configuration;
    }
}
