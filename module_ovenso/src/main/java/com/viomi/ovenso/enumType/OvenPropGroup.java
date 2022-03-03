package com.viomi.ovenso.enumType;

import com.viomi.ovensocommon.spec.OvenPropEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 集合：从单片机请求属性
 */
public class OvenPropGroup {
    public static Map<Integer, int[]> propertyCollection = new HashMap<>();

    static {
        propertyCollection.put(OvenPropEnum.FIRM_VERSION.siid,
                new int[]{
                        OvenPropEnum.FIRM_VERSION.piid
                });
        propertyCollection.put(OvenPropEnum.WORK_STATUS.siid,
                new int[]{
                        OvenPropEnum.DEVICE_FAULT.piid,
                        OvenPropEnum.TEMPER.piid
                });
        propertyCollection.put(OvenPropEnum.DISHID.siid,
                new int[]{
                        OvenPropEnum.DISHID.piid,
                        OvenPropEnum.MODE.piid,
                        OvenPropEnum.DOOR_OPEN.piid,
                        OvenPropEnum.WATERTANK_ISCLOSE.piid,
                        OvenPropEnum.HARD_VERSION.piid,
                        OvenPropEnum.LIGHT.piid,
                        OvenPropEnum.VIDEO_RECORD.piid,
                        OvenPropEnum.MODE_STEP.piid,
                        OvenPropEnum.CUSTOM_MODE_STEP.piid,
                        OvenPropEnum.CUSTOM_MODE_CONTENT.piid,
                });
    }
}
