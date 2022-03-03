package com.miotspecv2.defined.viomi;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.miotspecv2.defined.viomi.service.MiotService;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @data:2021/10/7
 */
public class MiotApplicaiton extends Application {
    private static final String TAG = "MiotApplicaiton";

    @Override
    public void onCreate() {
        super.onCreate();
        initMiotWork();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "attachBaseContext: ");
        MiotServiceFactory.getInstance().setMiotService(new MiotService());
        initMiotWork();
    }

    private void initMiotWork() {
        Log.i(TAG, "initMiotWork: ");
        new Handler(Looper.getMainLooper()).post(() -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresDeviceIdle(false)
                    .setRequiresStorageNotLow(true)
                    .build();
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MiotInitWork.class)
                    .setInitialDelay(1000, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(ApplicationUtils.getContext()).enqueue(oneTimeWorkRequest);
        });
    }
}
