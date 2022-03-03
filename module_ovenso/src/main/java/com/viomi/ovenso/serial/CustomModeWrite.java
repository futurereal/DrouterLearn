package com.viomi.ovenso.serial;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyWrite;
import com.viomi.ovensocommon.spec.OvenPropEnum;

/**
 * @description:
 * @data:2021/12/13
 */
public class CustomModeWrite extends PropertyWrite {
    private static final String TAG = "LightWrite";
    private PropertyEntity propertyEntity;
    // 和固件定义的 自定义组合模式的传值方式， 5  1  + 字符串的方式来设置属性
    public static final int CUTOMMODE_MUC_SIID = OvenPropEnum.CUSTOM_MODE_CONTENT.siid;
    public static final int CUTOMMODE_MUC_PID = OvenPropEnum.CUSTOM_MODE_CONTENT.piid;


    @Override
    public void initPropertyEntity() {
        this.sid = CUTOMMODE_MUC_SIID;
        this.pid = CUTOMMODE_MUC_PID;
    }
}
