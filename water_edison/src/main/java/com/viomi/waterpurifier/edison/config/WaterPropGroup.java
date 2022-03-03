package com.viomi.waterpurifier.edison.config;

import android.util.Log;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.util.WaterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by xinqi on 2021-07-14.
 * @describe 净水器ero屬性组
 */
public class WaterPropGroup {
    private static final String TAG = "WaterPropGroup";
    //    public static Map<Integer, int[]> propGroup = new HashMap<>();
    public static WaterPropEnum[] otherDefaultProps = new WaterPropEnum[]{
            WaterPropEnum.FILTER_ID, WaterPropEnum.WATER_OUT_STATUS, WaterPropEnum.TEMP_MODE,
            WaterPropEnum.CUP_MODE, WaterPropEnum.SET_TEMP, WaterPropEnum.SET_FLOW,
            WaterPropEnum.WATER_QUALITY, WaterPropEnum.SELF_CLEAN_MODE, WaterPropEnum.FACTORY,
            WaterPropEnum.FACTORY_DISINFECT, WaterPropEnum.CISTERN, WaterPropEnum.CHANGE_WATER,
            WaterPropEnum.CUP_STOP, WaterPropEnum.UV_STATUE, WaterPropEnum.FIRM_MANUFACTURE,
            WaterPropEnum.FIRM_MODEL, WaterPropEnum.FIRM_SERIAL_NO, WaterPropEnum.FIRM_VERSION

    };
    public static Object[] otherDefaultValues = new Object[]{
            21, "Idle112", WaterConstant.DEFAULT_TEMPERATURE_MODE,
            WaterConstant.DEFAULT_CUP_MODE, WaterConstant.DEFAULT_WARM_WATER_TEMP, 100,
            WaterConstant.DEFAULT_WATER_QUALITY, 0, 0,
            0, 0, 0,
            false, 0, "viomi",
            "waterEdison", "1234", "00001"
    };
    public static WaterPropEnum[] localProps = new WaterPropEnum[]{WaterPropEnum.CHILD_LOCK, WaterPropEnum.STANDBY};
    public static Object[] otherLocalValue = new Object[]{true, 2};


    public static Map<Integer, int[]> getMcuPlugGrup() {
        Map<Integer, int[]> mucPropGruop = new HashMap<>();
        // 设备故障，用于运行
        mucPropGruop.put(WaterPropEnum.EQUIPMENT_FAULT.siid, new int[]{
                WaterPropEnum.EQUIPMENT_FAULT.piid,
                WaterPropEnum.EQUIPMENT_STATE.piid
        });
        // 滤芯  用于插件
        mucPropGruop.put(WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.siid,
                new int[]{WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.piid,
                        WaterPropEnum.FILTER_4IN1_LIFE_TIME.piid,
                        WaterPropEnum.FILTER_4IN1_USED_TIME.piid,
                        WaterPropEnum.FILTER_4IN1_LEFT_FLOW.piid,
                        WaterPropEnum.FILTER_4IN1__USED_FLOW.piid
                });
        //滤芯  用于插件
        mucPropGruop.put(WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.siid,
                new int[]{WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.piid,
                        WaterPropEnum.FILTER_CARBON_LIFE_TIME.piid,
                        WaterPropEnum.FILTER_CARBON_USED_TIME.piid,
                        WaterPropEnum.FILTER_CARBON__LEFT_FLOW.piid,
                        WaterPropEnum.FILTER_CARBON__USED_FLOW.piid
                });
        //
        mucPropGruop.put(WaterPropEnum.WATER_OUT_STATUS.siid,
                new int[]{
                        WaterPropEnum.TDS_IN.piid,
                        WaterPropEnum.TDS_OUT.piid,
                        WaterPropEnum.RINSE.piid,
                        WaterPropEnum.FILTER_RESET_PROGRESS.piid,
                        WaterPropEnum.USE_TIME.piid,
                        WaterPropEnum.USE_FLOW_IN.piid,
                        WaterPropEnum.USE_FLOW_OUT.piid,
                        WaterPropEnum.CUMU_TIME.piid,
                        WaterPropEnum.CUMU_FLOW_IN.piid,
                        WaterPropEnum.CUMU_FLOW_OUT.piid,
                        WaterPropEnum.WATER_OUT_STATUS.piid,
                        WaterPropEnum.TEMPERATURE_WATER_IN.piid});

        mucPropGruop.put(WaterPropEnum.FAULT.siid,
                new int[]{
                        WaterPropEnum.FAULT.piid
                });

        mucPropGruop.put(WaterPropEnum.TEMP_WARM.siid,
                new int[]{WaterPropEnum.TEMP_WARM.piid,
                        WaterPropEnum.TEMP_HOT.piid,
                        WaterPropEnum.TEMP_BOILING.piid,
                        WaterPropEnum.SMALL_CUP_FLOW.piid,
                        WaterPropEnum.MIDDLE_CUP_FLOW.piid,
                        WaterPropEnum.BIG_CUP_FLOW.piid,
                });

        mucPropGruop.put(WaterPropEnum.MINERAL_TYPE.siid,
                new int[]{
                        WaterPropEnum.MINERAL_TYPE.piid
                });
        return mucPropGruop;
    }

    public static List<PropertyEntity> getMcuLocalProps() {
        List<PropertyEntity> mcuLocalPropertyList = new ArrayList<>();
        for (int i = 0; i < otherDefaultProps.length; i++) {
            PropertyEntity propertyEntity = new PropertyEntity();
            WaterPropEnum waterPropEnum = otherDefaultProps[i];
            propertyEntity.setSid(waterPropEnum.siid);
            propertyEntity.setPid(waterPropEnum.piid);
            Object defaultVaule = otherDefaultValues[i];
            Object localValue = PropertyPreferenceManager.getInstance().getProperty(waterPropEnum.siid, waterPropEnum.piid, defaultVaule);
            propertyEntity.setContent(localValue);
            mcuLocalPropertyList.add(propertyEntity);
        }
        Log.i(TAG, "getMcuLocalProps: size  " + mcuLocalPropertyList.size());
        return mcuLocalPropertyList;
    }

    public static List<PropertyEntity> getPropertyFromLocal() {
        List<PropertyEntity> localPropertyList = new ArrayList<>();
        for (int i = 0; i < localProps.length; i++) {
            PropertyEntity propertyEntity = new PropertyEntity();
            WaterPropEnum waterPropEnum = localProps[i];
            propertyEntity.setSid(waterPropEnum.siid);
            propertyEntity.setPid(waterPropEnum.piid);
            Object defaultVaule = otherLocalValue[i];
            Object localValue = WaterUtils.getLocalWaterProps(waterPropEnum, defaultVaule);
            propertyEntity.setContent(localValue);
            localPropertyList.add(propertyEntity);
        }
        Log.i(TAG, "getPropertyFromLocal: size  " + localPropertyList.size());
        return localPropertyList;
    }
}
