package com.viomi.ovenso;

import android.util.Log;

import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenPropEnum;

/**
 * Created by Ljh on 2020/3/9.
 * 全局变量
 */
public class PropertyUtil {
    private static final String TAG = "PropertyUtil";
    //属性值
    public static int dishid = 0;
    public static String dishname = "";
    public static int mode = 0;
    public static int tempz = 0;
    public static int timez = 0;
    public static int tempk = 0;
    public static int timek = 0;
    public static int microTime = 0;
    public static int microLevel = 0;
    public static boolean doorOpen = false;
    public static boolean watertankClosed = false;
    public static int worktotaltime = 0;
    public static String finishtime = "";
    public static boolean isLockWater;
    public static int preparetime = 0;
    public static String hardver = "";
    public static boolean light = false;
    public static int cookStep = 0;
    public static int mixModeIndicator = 0;

    public static int status = OvenWorkStatusEnum.IDLE.value;
    public static int fault = 0;
    public static int lefttime = 0;
    public static int workingtime = 0;
    public static int currentTemperature = 0;

    /**
     * 获取状态的属性
     * 更新全局的属性变化
     *
     * @return
     */
    public static void updateGlobalPropAndGetStatusEntity(PropertyEntity propertyEntity) {
        Log.d(TAG, "updateAppParAndGetDeviceProp");
        int siid = propertyEntity.getSid();
        int piid = propertyEntity.getPid();
        Object value = propertyEntity.getContent();
        if (value == null) {
            Log.i(TAG, "updateAppParAndGetDeviceProp: is null");
            return;
        }
        int valueInt = 0;
        boolean valueBoolean = false;
        if (value instanceof Integer) {
            valueInt = (int) value;
        }
        if (value instanceof Boolean) {
            valueBoolean = (boolean) value;
        }
        Log.d(TAG, "updateAppParAndGetDeviceProp: Siid:" + siid + "  Piid:" + piid + " value:" + value);
        if (checkEqual(propertyEntity, OvenPropEnum.FIRM_VERSION)) {
            PropertyPreferenceManager.getInstance().setProperty(OvenPropEnum.FIRM_VERSION.siid, OvenPropEnum.FIRM_VERSION.piid, Integer.parseInt(propertyEntity.getContent().toString()));
        } else if (checkEqual(propertyEntity, OvenPropEnum.DISHID)) {
            PropertyUtil.dishid = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.DISHNAME)) {
            PropertyUtil.dishname = String.valueOf(propertyEntity.getContent());
        } else if (checkEqual(propertyEntity, OvenPropEnum.MODE)) {
            PropertyUtil.mode = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.TEMPZ)) {
            PropertyUtil.tempz = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.TIMEZ)) {
            PropertyUtil.timez = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.TEMPK)) {
            PropertyUtil.tempk = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.TIMEK)) {
            PropertyUtil.timek = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.MICRO_LEVEL)) {
            PropertyUtil.microLevel = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.MICRO_TIME)) {
            PropertyUtil.microTime = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.MODE_STEP)) {
            PropertyUtil.cookStep = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.DOOR_OPEN)) {
            PropertyUtil.doorOpen = valueBoolean;
        } else if (checkEqual(propertyEntity, OvenPropEnum.WORK_TOTAL_TIME)) {
            PropertyUtil.worktotaltime = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.FINISH_TIME)) {
            PropertyUtil.finishtime = String.valueOf(propertyEntity.getContent());
        } else if (checkEqual(propertyEntity, OvenPropEnum.LACK_WATER)) {
            PropertyUtil.isLockWater = valueBoolean;
        } else if (checkEqual(propertyEntity, OvenPropEnum.APPOINT_TOTAL_TIME)) {
            PropertyUtil.preparetime = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.HARD_VERSION)) {
            PropertyUtil.hardver = String.valueOf(propertyEntity.getContent());
        } else if (checkEqual(propertyEntity, OvenPropEnum.LEFT_TIME)) {
            PropertyUtil.lefttime = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.WORKING_TIME)) {
            PropertyUtil.workingtime = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.TEMPER)) {
            PropertyUtil.currentTemperature = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.DEVICE_FAULT) && valueInt != PropertyUtil.fault) {
            PropertyUtil.fault = valueInt;
            Log.i(TAG, "updateAppParAndGetDeviceProp:" + PropertyUtil.fault);
        } else if (checkEqual(propertyEntity, OvenPropEnum.LIGHT)) {
            PropertyUtil.light = valueBoolean;
        } else if (checkEqual(propertyEntity, OvenPropEnum.WORK_STATUS)) {
            PropertyUtil.status = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.CUSTOM_MODE_STEP)) {
            PropertyUtil.mixModeIndicator = valueInt;
        } else if (checkEqual(propertyEntity, OvenPropEnum.WATERTANK_ISCLOSE)) {
            PropertyUtil.watertankClosed = valueBoolean;
        }
    }

    private static boolean checkEqual(PropertyEntity item, OvenPropEnum propFirmVer) {
        return item.getSid() == propFirmVer.siid && item.getPid() == propFirmVer.piid;
    }


    public static String getTestProperty() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dishId: ").append(PropertyUtil.dishid)
                .append("  modeId:").append(PropertyUtil.mode)
                .append("  tempz:").append(PropertyUtil.tempz)
                .append("  timez:").append(PropertyUtil.timez)
                .append("  tempk:").append(PropertyUtil.tempk)
                .append("  timek:").append(PropertyUtil.timek)
                .append("  microLevel:").append(PropertyUtil.microLevel)
                .append("  microTime:").append(PropertyUtil.microTime)
                .append("  doorOpen:").append(PropertyUtil.doorOpen)
                .append("  waterTankClose:").append(PropertyUtil.watertankClosed)
                .append("  preparetime:").append(PropertyUtil.preparetime)
                .append("  worktotaltime:").append(PropertyUtil.worktotaltime)
                .append("  finishtime:").append(PropertyUtil.finishtime)
                .append("  workingtime:").append(PropertyUtil.workingtime)
                .append("  lefttime:").append(PropertyUtil.lefttime)
                .append("  isLockWater:").append(PropertyUtil.isLockWater)
                .append("  light:").append(PropertyUtil.light)
                .append("  workStatus:").append(PropertyUtil.status)
                .append("  deviceFault:").append(PropertyUtil.fault)
                .append("  temperature:").append(PropertyUtil.currentTemperature)
                .append("  cookStep :").append(PropertyUtil.cookStep)
                .append("  customModeStep:").append(PropertyUtil.mixModeIndicator);
        String props = stringBuilder.toString();
        return props;
    }
}
