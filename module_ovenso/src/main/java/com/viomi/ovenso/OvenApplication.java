package com.viomi.ovenso;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.ProcessUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.viomi.common.ApplicationUtils;
import com.viomi.common.VLogUtil;
import com.viomi.ovenso.enumType.OvenPropGroup;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.utils.ApplicationManager;
import com.viomi.router.core.ViomiRouter;

/**
 * Created with Android Studio
 * Author:Ljh
 * Date:2020/1/7
 **/
public class OvenApplication extends Application {
    private static final String TAG = "OvenApplication";
    private static Context applicationContext;

    public static Context getInstance() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VLogUtil.init(this);
        Log.i(TAG, "onCreate: ");
        if (ProcessUtils.isMainProcess()) {
            Fresco.initialize(this);
            initCommonSet();
            ApplicationManager.initOtherModuleApplication(this);
        }
    }


    // 空闲处理
    @Override
    protected void attachBaseContext(Context base) {
        Log.i(TAG, "attachBaseContext: ");
        super.attachBaseContext(base);
        applicationContext = base;
        initCommonSet();
    }

    private void initCommonSet() {
        ViomiRouter.init(this, true);
        Log.i(TAG, "initCommonSet");
        SerialControl.setAllPropertyMap(OvenPropGroup.propertyCollection);
        applicationContext = this;
    }

    public static Context getContext() {
        if (applicationContext == null) {
            applicationContext = ApplicationUtils.getContext();
        }
        return applicationContext;
    }


}
