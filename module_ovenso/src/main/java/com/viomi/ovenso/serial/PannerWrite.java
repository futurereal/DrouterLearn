package com.viomi.ovenso.serial;

import com.viomi.ovensocommon.serialcontrol.PropertyWrite;

/**
 * 灯的开关，属性变化
 * 测试是封装 不变的， 变的单独处理
 */
public class PannerWrite extends PropertyWrite {
    private static final String TAG = "LightWrite";

    @Override
    public void initPropertyEntity() {
       /* this.sid = OvenPropEnum.PANNEL.siid;
        this.pid = OvenPropEnum.PANNEL.piid;*/
    }
}
