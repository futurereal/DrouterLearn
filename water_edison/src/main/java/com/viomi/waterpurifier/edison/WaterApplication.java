package com.viomi.waterpurifier.edison;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.ProcessUtils;
import com.viomi.common.VLogUtil;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.router.core.ViomiRouter;
import com.viomi.waterpurifier.edison.config.WaterPropGroup;
import com.viomi.waterpurifier.edison.util.WaterActionUtils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 净水器 applicaiton
 */
public class WaterApplication extends Application {
    private static final String TAG = "WaterApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        VLogUtil.init(this);
        Log.i(TAG, "onCreate: ");
        if (ProcessUtils.isMainProcess()) {
            initCommonSet(this);
        }
    }

    // 作为模块的时候，初始化只会调用这里
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.i(TAG, "attachBaseContext: " + base);
        initCommonSet(base);
    }

    private void initCommonSet(Context base) {
        Log.i(TAG, "initCommonSet: " + base);
        SerialControl.setAllPropertyMap(WaterPropGroup.getMcuPlugGrup());
        ViomiRouter.init(this, true);
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate: ");
        super.onTerminate();
        WaterActionUtils.stopOutWater();
    }
}
