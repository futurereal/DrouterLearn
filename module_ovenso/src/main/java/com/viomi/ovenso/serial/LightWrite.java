package com.viomi.ovenso.serial;

import android.util.Log;

import com.viomi.ovensocommon.serialcontrol.PropertyWrite;
import com.viomi.ovensocommon.spec.OvenPropEnum;

/**
 * 灯的开关，属性变化
 * 测试是封装 不变的， 变的单独处理
 */
public class LightWrite extends PropertyWrite {
    private static final String TAG = "LightWrite";

    @Override
    public void initPropertyEntity() {
        Log.i(TAG, "initPropertyEntity: ");
        this.sid = OvenPropEnum.LIGHT.siid;
        this.pid = OvenPropEnum.LIGHT.piid;
    }
}
