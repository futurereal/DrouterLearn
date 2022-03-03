package com.viomi.camera;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.viomi.common.VLogUtil;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.router.core.ViomiRouter;

/**
 * @description:
 * @data:2021/9/15
 */
public class CameraApplicaiton extends Application {
    private static final String TAG = "CameraApplicaiton";

    @Override
    public void onCreate() {
        super.onCreate();
        VLogUtil.init(this);
        ViomiRouter.init(this, true);
        initCamera();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initCamera();
    }

    private void initCamera() {
        Log.i(TAG, "initCamera: ");
        CameraServiceFactory.getInstance().setCameraService(new CameraModuleService());
    }
}
