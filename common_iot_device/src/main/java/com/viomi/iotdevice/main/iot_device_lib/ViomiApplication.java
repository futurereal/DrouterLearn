package com.viomi.iotdevice.main.iot_device_lib;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.viomi.iotdevice.main.iot_device_lib.miotdeivce.device.MiotManager;
import com.viomi.iotdevice.main.newmiot.Miotv2Manager;

import java.util.HashSet;
import java.util.Set;

public class ViomiApplication extends Application {

    private static ViomiApplication mViomiApplication;

    public static ViomiApplication getInstance() {
        return mViomiApplication;
    }
    boolean isSpec = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mViomiApplication = this;

        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(requestListeners)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        if(isSpec)
            Miotv2Manager.getInstance().bindMiotService(this,null);
        else
            MiotManager.getInstance().bindMiotService(this, null);

    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }
}
