package com.viomi.waterpurifier.edison.util;

import android.util.Log;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.spec.WaterActionEnum;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.WaterConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/10/26
 */
public class WaterActionUtils {
    private static final String TAG = "WaterActionUtils";

    public static void startOutWater(int waterTemperature, int waterAmount) {
        Log.i(TAG, "startOutWater: waterTemperature: " + waterTemperature + "  waterAmount: " + waterAmount);
        PropertyEntity temperatureProperty = new PropertyEntity(WaterPropEnum.SET_FLOW.siid, WaterPropEnum.SET_FLOW.piid, waterAmount);
        PropertyEntity amountProperty = new PropertyEntity(WaterPropEnum.SET_TEMP.siid, WaterPropEnum.SET_TEMP.piid, waterTemperature);
        List<PropertyEntity> propertyEntityList = new ArrayList<PropertyEntity>(2);
        propertyEntityList.add(temperatureProperty);
        propertyEntityList.add(amountProperty);
        SerialControl.setAction("", WaterActionEnum.ACTION_SYS_START.siid, WaterActionEnum.ACTION_SYS_START.aiid, propertyEntityList);
    }

    public static void stopOutWater() {
        Log.i(TAG, "stopOutWater: ");
        String waterOutState = (String) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.WATER_OUT_STATUS.siid, WaterPropEnum.WATER_OUT_STATUS.piid, WaterConstant.WATEROUT_IDLE);
        Log.d(TAG, "stopWaterOut: waterOutState: " + waterOutState);
        //当前正在出水
        if (!waterOutState.contains(WaterConstant.WATEROUT_PURING)) {
//            return;
        }
        SerialControl.setAction("", WaterActionEnum.ACTION_SYS_STOP.siid, WaterActionEnum.ACTION_SYS_STOP.aiid, null);
    }

    public static void resetFilter(String name, int actionSid) {
        Log.i(TAG, "resetFilter:  name: " + name + " acitonSid: " + actionSid);
        int aiid = 0;
        WaterActionEnum waterActionEnum = null;
        WaterPropEnum waterPropEnumUseTime = null;
        WaterPropEnum waterPropEnumUseFlow = null;
        if (actionSid == WaterActionEnum.RESET_FILTER_CARBON.siid) {
            waterActionEnum = WaterActionEnum.RESET_FILTER_CARBON;
            waterPropEnumUseTime = WaterPropEnum.FILTER_CARBON_USED_TIME;
            waterPropEnumUseFlow = WaterPropEnum.FILTER_CARBON__USED_FLOW;
        } else if (actionSid == WaterActionEnum.RESET_FILTER_41.siid) {
            waterActionEnum = WaterActionEnum.RESET_FILTER_41;
            waterPropEnumUseTime = WaterPropEnum.FILTER_4IN1_USED_TIME;
            waterPropEnumUseFlow = WaterPropEnum.FILTER_4IN1__USED_FLOW;
        }
        ArrayList<PropertyEntity> propertyEntityArrayList = new ArrayList<>(2);
        int wateruseTime = (int) PropertyPreferenceManager.getInstance().getProperty(waterPropEnumUseTime.siid, waterPropEnumUseTime.piid, 0);
        PropertyEntity propertyEntity = new PropertyEntity(waterPropEnumUseTime.siid, waterPropEnumUseTime.piid, wateruseTime);
        Log.i(TAG, "resetFilter: userTimeProperty: " + propertyEntity);
        propertyEntityArrayList.add(propertyEntity);
        int waterUseFlow = (int) PropertyPreferenceManager.getInstance().getProperty(waterPropEnumUseFlow.siid, waterPropEnumUseFlow.piid, 0);
        propertyEntity = new PropertyEntity(waterPropEnumUseFlow.siid, waterPropEnumUseFlow.piid, waterUseFlow);
        Log.i(TAG, "resetFilter: userFlowProperty: " + propertyEntity);
        propertyEntityArrayList.add(propertyEntity);
        SerialControl.setAction(name, waterActionEnum.siid, aiid, propertyEntityArrayList);
    }
}
