package com.viomi.waterpurifier.edison.service;

import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.IWaterService;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.entity.WaterErrorEntity;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.WaterMainActivity;
import com.viomi.waterpurifier.edison.util.DeviceFaultErrorParser;
import com.viomi.waterpurifier.edison.util.WaterUtils;

import java.util.List;

public class WaterService implements IWaterService {
    private static final String TAG = "WaterService";

    @Override
    public boolean getChildLock() {
        return (boolean) WaterUtils.getLocalWaterProps(WaterPropEnum.CHILD_LOCK, true);
    }

    @Override
    public String getMineralLevel() {
        int mineralLevel = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.MINERAL_TYPE.siid,
                WaterPropEnum.MINERAL_TYPE.piid, WaterConstant.DEFAULT_MINERAL_TYPE);
        Log.i(TAG, "getMineralLevel: mineralLever: " + mineralLevel);
        int[] mineralType = new int[]{R.string.mineral_low, R.string.mineral_middle, R.string.mineral_high};
        String mineralLevelString = ApplicationUtils.getContext().getResources().getString(mineralType[mineralLevel - 1]);
        return mineralLevelString;
    }

    @Override
    public void reportStandByTime(int standByIndex) {
        Log.i(TAG, "reportStandByTime: standByTime: " + standByIndex);
        PropertyEntity standByPropertyEntity = new PropertyEntity();
        standByPropertyEntity.setSid(WaterPropEnum.STANDBY.siid);
        standByPropertyEntity.setPid(WaterPropEnum.STANDBY.piid);
        standByPropertyEntity.setContent(standByIndex);
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(standByPropertyEntity);
        WaterUtils.setLocalWaterProps(WaterPropEnum.STANDBY, standByIndex);
    }

    @Override
    public void isPowerOffLauncher() {
        Log.i(TAG, "isPowerOffLauncher: isPowerOffLauncher");
        WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_FIRST_OUT_TIME, WaterMainActivity.DEFAULT_FIRST_OUT_TIME);
    }

    @Override
    public void MiotLoginStatusChange(boolean isBind) {

    }

    @Override
    public void ViotLoginStatusChange(boolean isBind) {

    }

    @Override
    public void dealPropertyFromPlug(PropertyEntity propertyEntity) {
        // ????????????
        Log.i(TAG, "setPropertyFromPlug : propertyEntity : " + propertyEntity);
        // ??????????????????????????????????????????????????????
        ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntity);
        // ???????????????????????????
        if (propertyEntity.getSid() == WaterPropEnum.STANDBY.siid && propertyEntity.getPid() == WaterPropEnum.STANDBY.piid) {
            int keepScreenTimeIndex = (int) propertyEntity.getContent();
            Log.i(TAG, "setPropertyFromPlug: keepScreenTimeIndex : " + keepScreenTimeIndex);
            int screenOffArrayId = ModuleSettingServiceFactory.getInstance().getViotService().getScreenOffArrayId();
            Log.i(TAG, "initScreenSleep: screenOffArrayId: " + screenOffArrayId);
            int[] screenOffArray = ApplicationUtils.getContext().getResources().getIntArray(screenOffArrayId);
            int keepScreenTime = screenOffArray[keepScreenTimeIndex];
            CommonPreference.getInstance().setScreenTime(keepScreenTime);
            ModuleSettingServiceFactory.getInstance().getViotService().setScreenOffTime(keepScreenTime);
            return;
        }
        // ?????? ????????????????????????
        if (propertyEntity.getSid() == WaterPropEnum.CHILD_LOCK.siid && propertyEntity.getPid() == WaterPropEnum.CHILD_LOCK.piid) {
            boolean isChildLockOpen = (boolean) propertyEntity.getContent();
            Log.i(TAG, "setPropertyFromPlug propertyChanges: isChildLock: " + isChildLockOpen);
            WaterUtils.setLocalWaterProps(WaterPropEnum.CHILD_LOCK, isChildLockOpen);
            ViomiRxBus.getInstance().post(CommonConstant.MSG_CHILDLOCK_SWITCH, isChildLockOpen);
            return;
        }
        // ???????????????????????????????????????????????????sp?????????
        WaterSerialManager.getInstance().writeProperty(propertyEntity);
    }

    @Override
    public void doActionFromPlug(int siid, int aiid, List<PropertyEntity> propertyEntities) {

    }

    @Override
    public void dealPropertyChangeFromFirm(PropertyEntity propertyEntity) {
        // ???????????? ??????????????????????????????
        int sid = propertyEntity.getSid();
        int pid = propertyEntity.getPid();
        Object content = propertyEntity.getContent();
        Log.i(TAG, "dealPropertyChangeFromFirm: propertyEntity:" + propertyEntity);
        // ??????????????????
        boolean isCarbonLifeLevel = sid == WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.siid && pid == WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.piid;
        boolean isInLifeLevel = sid == WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.siid && pid == WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.piid;
        if (isCarbonLifeLevel || isInLifeLevel) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_FILTER_PROPERTY, propertyEntity);
            int filterLifeLevel = (int) content;
            if (filterLifeLevel < WaterConstant.FILTER_ERROR_MARGIN) {
                MessageDialogManager.getInstance().showFilterErrorDialog(propertyEntity);
            } else {
                MessageDialogManager.getInstance().dismissFilterErrorDialog();
            }
            return;
        }
        // ????????????
        if (sid == WaterPropEnum.RINSE.siid && pid == WaterPropEnum.RINSE.piid) {
            boolean isFlushFileter = (boolean) content;
            Log.i(TAG, "dealPropertyChangeFromFirm: isFlushFileter: " + isFlushFileter);
            if (isFlushFileter) {
                MessageDialogManager.getInstance().showFilterWashFragment();
            }
            return;
        }
        // ?????????????????????
        if (sid == WaterPropEnum.FILTER_RESET_PROGRESS.siid && pid == WaterPropEnum.FILTER_RESET_PROGRESS.piid) {
            int resetProgress = (int) content;
            Log.i(TAG, "dealPropertyChangeFromFirm: resetProgress: " + resetProgress);
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_FILTER_WASH_PROGRESS, resetProgress);
            return;
        }
        // ??????????????????
        if (sid == WaterPropEnum.EQUIPMENT_FAULT.siid && pid == WaterPropEnum.EQUIPMENT_FAULT.piid && (int) content > 0) {
            WaterErrorEntity waterErrorEntity = DeviceFaultErrorParser.getNewError((int) content);
            if (waterErrorEntity == null) {
                Log.i(TAG, "dealPropertyChangeFromFirm: not in appError");
                return;
            }
            MessageDialogManager.getInstance().showDeviceFaultDialog(waterErrorEntity);
            return;
        }
        // ??????????????????
        if (sid == WaterPropEnum.CISTERN.siid && pid == WaterPropEnum.CISTERN.piid) {
            boolean isLockWater = (int) content == WaterConstant.STATE_LACK_WATER;
            if (isLockWater) {
                MessageDialogManager.getInstance().showLackWaterDialog();
            } else {
                MessageDialogManager.getInstance().dismissLackWaterDialog();
            }
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_CISTERN_CHANGE, isLockWater);
            return;
        }
        // ???????????????,?????????????????????????????????????????????
        if (sid == WaterPropEnum.SELF_CLEAN_MODE.siid && pid == WaterPropEnum.SELF_CLEAN_MODE.piid) {
            int selfCleanStep = (int) content;
            if (selfCleanStep == WaterConstant.SELFCLEAN_BEGAIN) {
                //????????????????????????
                ViomiRxBus.getInstance().post(WaterBusEvent.MSG_SELFCLEAN_COUNTDOWN);
            } else if (selfCleanStep == WaterConstant.SELFCLEAN_DOING) {
                MessageDialogManager.getInstance().showSelfCleanFragment();
            } else if (selfCleanStep == WaterConstant.SELFCLEAN_IDLE) {
                MessageDialogManager.getInstance().dissMissSelfFragment();
            }
            return;
        }
        // ???????????????????????????
        if (sid == WaterPropEnum.SMALL_CUP_FLOW.siid && pid >= WaterPropEnum.SMALL_CUP_FLOW.piid && pid <= WaterPropEnum.BIG_CUP_FLOW.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_CUPFLOW, propertyEntity);
            return;
        }
        // ????????????????????????
        if (sid == WaterPropEnum.SET_TEMP.siid && pid == WaterPropEnum.SET_TEMP.piid) {
            // ?????? ????????????????????????
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_TEMPERATURE_CUSTM, content);
            return;
        }
        // ?????????????????????
        if (sid == WaterPropEnum.TEMP_WARM.siid && pid >= WaterPropEnum.TEMP_WARM.piid && pid <= WaterPropEnum.TEMP_BOILING.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_TEMPERATURE, propertyEntity);
            return;
        }
        // ????????????????????????
        if (sid == WaterPropEnum.MINERAL_TYPE.siid && pid == WaterPropEnum.MINERAL_TYPE.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_MINERAL, content);
            return;
        }
        // ????????????
        if (sid == WaterPropEnum.WATER_QUALITY.siid && pid == WaterPropEnum.WATER_QUALITY.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_MINERAL_TYPE, content);
            return;
        }
        // ??????????????????
        if (sid == WaterPropEnum.TEMP_MODE.siid && pid == WaterPropEnum.TEMP_MODE.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_TEMP_MODE, propertyEntity);
            return;
        }
        //??????????????????
        if (sid == WaterPropEnum.CUP_MODE.siid && pid == WaterPropEnum.CUP_MODE.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_CUPMODE, propertyEntity);
            return;
        }
        // ???????????????
        if (sid == WaterPropEnum.USE_FLOW_OUT.siid && pid == WaterPropEnum.USE_FLOW_OUT.piid) {
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_WATER_FLOW, content);
            return;
        }
        // ?????????????????????
        if (sid == WaterPropEnum.WATER_OUT_STATUS.siid && pid == WaterPropEnum.WATER_OUT_STATUS.piid) {
            String status = (String) content;
            Log.i(TAG, "dealPropertyChangeFromFirm: waterOutStatus: " + status);
            boolean isWaterOut = status.contains(WaterConstant.WATEROUT_PURING);
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_PROPERTY_WATER_STATUS, isWaterOut);
        }

    }

    @Override
    public void dealEventChangeFromFirm(PropertyEntity propertyEntity) {

    }

}
