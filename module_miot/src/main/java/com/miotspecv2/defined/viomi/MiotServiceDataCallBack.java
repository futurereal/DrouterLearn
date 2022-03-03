package com.miotspecv2.defined.viomi;

import android.util.Base64;
import android.util.Log;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.device.OvenDevice;
import com.miotspecv2.defined.service.Custommode;
import com.miotspecv2.defined.service.Customoven;
import com.miotspecv2.defined.service.Customrecipes;
import com.miotspecv2.defined.service.Deviceinformation;
import com.miotspecv2.defined.service.Oven;
import com.miotspecv2.defined.service.Safetycheck;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.ovenso.IOvensoService;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.Event;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.property.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:Miot 属性上报  下发  action 的下发
 */
public class MiotServiceDataCallBack implements MiotDataCallback {
    private static final String TAG = "MiotServiceDataCallBack";
    // 默认是录制结束的
    public static final int RECORD_FINISH = 0;
    private final OvenDevice mDevice;
    private PropertyPreferenceManager propertyManaer;

    public MiotServiceDataCallBack(OvenDevice mDevice) {
        this.mDevice = mDevice;
    }

    @Override
    public void setAllPropertyHandler() {
        propertyManaer = PropertyPreferenceManager.getInstance();
        IOvensoService ovenService = OvensoServiceFactory.getInstance().getOvenService();
        Log.i(TAG, "registerGetSetAction:1 ");
        //服务1 获取固件的属性
        mDevice.deviceinformation().setHandler(new Deviceinformation.ActionHandler() {
        }, new Deviceinformation.PropertyGetter() {
            @Override
            public String getManufacturer() {
                String value = mDevice.deviceinformation().manufacturer().getValue();
                Log.i(TAG, "getManufacturer: " + value);
                return value;
            }

            @Override
            public String getModel() {
                String value = mDevice.deviceinformation().model().getValue();
                Log.i(TAG, "getModel: " + value);
                return value;
            }

            @Override
            public String getSerialnumber() {
                String value = mDevice.deviceinformation().serialnumber().getValue();
                Log.i(TAG, "getSerialnumber: " + value);
                return value;
            }

            @Override
            public String getFirmwarerevision() {
                String value = mDevice.deviceinformation().firmwarerevision().getValue();
                Log.i(TAG, "getFirmwarerevision: " + value);
                return value;
            }
        }, new Deviceinformation.PropertySetter() {
        });
        Log.i(TAG, "registerGetSetAction:2 ");
        //服务2  oven    PropertyGetter  是插件端 轮询获取 android 屏幕保存的 状态 和属性
        mDevice.oven().setHandler(new Oven.ActionHandler() {
            @Override
            public void onStartcook(int status) {
                setOvenStatus(OvenActionEnum.ACTION_START, status);
            }

            @Override
            public void onCancelcooking(int status) {
                setOvenStatus(OvenActionEnum.ACTION_OVER, status);
            }

            @Override
            public void onPause(int status) {
                setOvenStatus(OvenActionEnum.ACTION_PAUSE, status);
            }

        }, new Oven.PropertyGetter() {
            @Override
            public int getStatus() {
                int cookStatus = (int) propertyManaer.getProperty(OvenPropEnum.WORK_STATUS.siid, OvenPropEnum.WORK_STATUS.piid, WorkStatusType.IDLE.value);
                Log.i(TAG, "getStatus: cookStatus: " + cookStatus);
                return cookStatus;
            }

            @Override
            public int getFault() {
                // 通讯故障 和其他故障不一样。 其他故障是 下发属性变化存入sp，通讯故障执有事件回调所以要另外区分处理
                int faultValue = (int) propertyManaer.getProperty(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, 0);
                // 判断是否通讯故障
                boolean isSerialDisconnect = CommonPreference.getInstance().getSerialDisconnect();
                Log.i(TAG, "getFault faltValle: " + faultValue + " isSerialDisconnect: " + isSerialDisconnect);
                if (isSerialDisconnect) {
                    // 通讯故障，和设备故障搞了一起 有问题
                    faultValue = faultValue | CommonConstant.OVENSO_DEVICE_FALUT_DISCONNECT;
                    Log.i(TAG, "getFault faltValle: connectError   " + faultValue);
                }
                return faultValue;
            }

            @Override
            public int getLefttime() {
                int leftTime = (int) propertyManaer.getProperty(OvenPropEnum.LEFT_TIME.siid, OvenPropEnum.LEFT_TIME.piid, 0);
                Log.i(TAG, "getLefttime: leftTime: " + leftTime);
                return leftTime;
            }

            @Override
            public int getWorkingtime() {
                int workTime = (int) propertyManaer.getProperty(OvenPropEnum.WORKING_TIME.siid, OvenPropEnum.WORKING_TIME.piid, 0);
                Log.i(TAG, "getWorkingtime: workTime: " + workTime);
                return workTime;
            }

            @Override
            public int getTemperature() {
                int temperature = (int) propertyManaer.getProperty(OvenPropEnum.TEMPER.siid, OvenPropEnum.TEMPER.piid, 0);
                Log.i(TAG, "getTemperature: temperature: " + temperature);
                return temperature;
            }
        }, new Oven.PropertySetter() {

        });
        Log.i(TAG, "registerGetSetAction:3 ");
        //服务3 custom-oven
        mDevice.customoven().setHandler(new Customoven.ActionHandler() {
            @Override
            public void onPrepare(int dishId, int appointTotalTime) {
                // 预约
                Log.i(TAG, "action:onPrepare dishid:" + dishId + " preparetime:" + appointTotalTime);
                List<PropertyEntity> paramsProp = new ArrayList<>();
                PropertyEntity propertyDish = new PropertyEntity(1, OvenPropEnum.DISHID.piid, dishId);
                paramsProp.add(propertyDish);
                PropertyEntity propertyTime = new PropertyEntity(1, OvenPropEnum.APPOINT_TOTAL_TIME.piid, appointTotalTime);
                paramsProp.add(propertyTime);
                OvensoServiceFactory.getInstance().getOvenService().doActionFromPlug(OvenActionEnum.ACTION_APPOINT.siid, OvenActionEnum.ACTION_APPOINT.aiid, paramsProp);
            }

            @Override
            public void onCancelprepare() {
                Log.i(TAG, "cancleAppoint: ");
                ArrayList<PropertyEntity> propertyEntityArrayList = new ArrayList<PropertyEntity>();
                PropertyEntity propertyEntity = new PropertyEntity(CommonConstant.ACTION_SID_NOVALUE,
                        CommonConstant.ACTION_PID_NOVALUE, CommonConstant.ACTION_VALUE_NOVALUE);
                propertyEntityArrayList.add(propertyEntity);
                OvensoServiceFactory.getInstance().getOvenService().doActionFromPlug(OvenActionEnum.ACTION_CANCLE_PREPARE.siid, OvenActionEnum.ACTION_CANCLE_PREPARE.aiid, propertyEntityArrayList);
            }

            @Override
            public void onClearfault() {
                // 插件 清除故障位
                Log.i(TAG, "onClearfault: ");
                ArrayList<PropertyEntity> propertyEntityArrayList = new ArrayList<PropertyEntity>();
                PropertyEntity propertyEntity = new PropertyEntity(CommonConstant.ACTION_SID_NOVALUE,
                        CommonConstant.ACTION_PID_NOVALUE, CommonConstant.ACTION_VALUE_NOVALUE);
                propertyEntityArrayList.add(propertyEntity);
                OvensoServiceFactory.getInstance().getOvenService().doActionFromPlug(OvenActionEnum.ACTION_CLEAR_FAULT.siid, OvenActionEnum.ACTION_CLEAR_FAULT.aiid, propertyEntityArrayList);
            }
        }, new Customoven.PropertyGetter() {
            @Override
            public int getDishid() {
                int dishId = (int) propertyManaer.getProperty(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0);
                Log.i(TAG, "getDishid: dishId: " + dishId);
                return dishId;
            }

            @Override
            public String getDishname() {
                String dishName = ovenService.getRecipeName();
                dishName = "null";
                String encodeDishName = Base64.encodeToString(dishName.getBytes(), Base64.DEFAULT);
                Log.i(TAG, "getDishname: dishName " + dishName + " encodeDishName: " + encodeDishName);
                return encodeDishName;
            }

            @Override
            public int getMode() {
                int mode = (int) propertyManaer.getProperty(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, 0);
                Log.i(TAG, "getMode: modeId " + mode);
                return mode;
            }

            @Override
            public int getTempz() {
                return (int) propertyManaer.getProperty(OvenPropEnum.TEMPZ.siid, OvenPropEnum.TEMPZ.piid, 0);
            }

            @Override
            public int getTimez() {
                return (int) propertyManaer.getProperty(OvenPropEnum.TIMEZ.siid, OvenPropEnum.TIMEZ.piid, 0);
            }

            @Override
            public int getTempk() {
                return (int) propertyManaer.getProperty(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 0);
            }

            @Override
            public int getTimek() {
                return (int) propertyManaer.getProperty(OvenPropEnum.TIMEK.siid, OvenPropEnum.TIMEK.piid, 0);
            }

            @Override
            public boolean getDoorisopen() {
                return (boolean) propertyManaer.getProperty(OvenPropEnum.DOOR_OPEN.siid, OvenPropEnum.DOOR_OPEN.piid, false);
            }

            @Override
            public int getWorktotaltime() {
                return (int) propertyManaer.getProperty(OvenPropEnum.WORK_TOTAL_TIME.siid, OvenPropEnum.WORK_TOTAL_TIME.piid, 0);
            }

            @Override
            public int getFinishtime() {
                return (int) propertyManaer.getProperty(OvenPropEnum.FINISH_TIME.siid, OvenPropEnum.FINISH_TIME.piid, 0);
            }

            @Override
            public boolean getWatertank() {
                boolean isLockWater = (boolean) propertyManaer.getProperty(OvenPropEnum.LACK_WATER.siid, OvenPropEnum.LACK_WATER.piid, false);
                Log.i(TAG, "getWatertank: " + isLockWater);
                return isLockWater;
            }

            @Override
            public int getPreparetime() {
                int prepareTime = (int) propertyManaer.getProperty(OvenPropEnum.APPOINT_TOTAL_TIME.siid, OvenPropEnum.APPOINT_TOTAL_TIME.piid, 0);
                Log.i(TAG, "getPreparetime: prepareTime: " + prepareTime);
                return prepareTime;
            }

            @Override
            public String getOvenhardwarever() {
                return (String) propertyManaer.getProperty(OvenPropEnum.HARD_VERSION.siid, OvenPropEnum.HARD_VERSION.piid, "");
            }

            @Override
            public boolean getOvenlight() {
                boolean ovenLight = (boolean) propertyManaer.getProperty(OvenPropEnum.LIGHT.siid, OvenPropEnum.LIGHT.piid, false);
                Log.i(TAG, "getOvenlight: " + ovenLight);
                return ovenLight;
            }

            @Override
            public int getRecipestep() {
                return (int) propertyManaer.getProperty(OvenPropEnum.RECIPE_STEP.siid, OvenPropEnum.RECIPE_STEP.piid, 0);
            }

            @Override
            public int getRecord() {
                int recordStatus = (int) propertyManaer.getProperty(OvenPropEnum.VIDEO_RECORD.siid, OvenPropEnum.VIDEO_RECORD.piid, RECORD_FINISH);
                Log.i(TAG, "getRecord: " + recordStatus);
                return recordStatus;
            }

            @Override
            public int getMicrolevel() {
                int microlLevel = (int) propertyManaer.getProperty(OvenPropEnum.MICRO_LEVEL.siid, OvenPropEnum.MICRO_LEVEL.piid, 0);
                return microlLevel;
            }

            // 获取自定义模式的名字,烹饪的时候会取这个名字
            @Override
            public String getModename() {
                String modeName = ovenService.getRecipeName();
                modeName = "null";
                String encodeModeName = Base64.encodeToString(modeName.getBytes(), Base64.DEFAULT);
                Log.i(TAG, "getDishname: modeName " + modeName + " encodeModeName: " + encodeModeName);
                return encodeModeName;
            }

            @Override
            public int getModestep() {
                int modeStep = (int) propertyManaer.getProperty(OvenPropEnum.MODE_STEP.siid, OvenPropEnum.MODE_STEP.piid, 0);
                return modeStep;
            }

            @Override
            public int getMicrotime() {
                return (int) propertyManaer.getProperty(OvenPropEnum.MICRO_TIME.siid, OvenPropEnum.MICRO_TIME.piid, 0);
            }

            public int getMixmodeindicator() {
                // 组合模式的步骤
                return (int) propertyManaer.getProperty(OvenPropEnum.CUSTOM_MODE_STEP.siid, OvenPropEnum.CUSTOM_MODE_STEP.piid, 0);
            }

            @Override
            public boolean getWatertankisclose() {
                boolean isWaterTankClose = (boolean) propertyManaer.getProperty(OvenPropEnum.WATERTANK_ISCLOSE.siid, OvenPropEnum.WATERTANK_ISCLOSE.piid, false);
                Log.i(TAG, "getWatertankisclose: isWotaterTankClose : " + isWaterTankClose);
                return isWaterTankClose;
            }

            @Override
            public String getScreencusrecipes() {
                String customModeContentStr = (String) propertyManaer.getProperty(OvenPropEnum.CUSTOM_MODE_CONTENT.siid, OvenPropEnum.CUSTOM_MODE_CONTENT.piid, "null");
                Log.i(TAG, "getWatertankisclose: customModeContentStr : " + customModeContentStr);
                return customModeContentStr;
            }
        }, new Customoven.PropertySetter() {
            @Override
            public void setDishid(int value) {
                // 设置菜谱的属性固件 需要 插件 再次下发start_cook 指令，才启动烹饪
                Log.i(TAG, "setDishMode:  dishId " + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setMode(int value) {
                // 设置属性给固件，需要 插件 再次下发start_cook 指令
                Log.i(TAG, "setDishMode: modeId " + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setTempz(int value) {
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.TEMPZ.siid, OvenPropEnum.TEMPZ.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setTimez(int value) {
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.TIMEZ.siid, OvenPropEnum.TIMEZ.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setTempk(int value) {
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setTimek(int value) {
                Log.i(TAG, "setTimek: value: " + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.TIMEK.siid, OvenPropEnum.TIMEK.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setPreparetime(int value) {
                Log.i(TAG, "setPreparetime: value: " + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.APPOINT_TOTAL_TIME.siid, OvenPropEnum.APPOINT_TOTAL_TIME.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setMicrolevel(int value) {
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.MICRO_LEVEL.siid, OvenPropEnum.MICRO_LEVEL.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setModename(String value) {
                Log.i(TAG, "setModename: " + value);
            }

            @Override
            public void setMicrotime(int value) {
                Log.i(TAG, "setMicrotime: " + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.MICRO_TIME.siid, OvenPropEnum.MICRO_TIME.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setScreencusrecipes(String value) {
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.CUSTOM_MODE_CONTENT.siid, OvenPropEnum.CUSTOM_MODE_CONTENT.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setOvenlight(boolean value) {
                Log.w(TAG, "setOvenlight:" + value);
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.LIGHT.siid, OvenPropEnum.LIGHT.piid, value);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setRecord(int recordActionValue) {
                // 视频的相关操作
                Log.i(TAG, "setRecord: value:  " + recordActionValue);
                // 只有启动 和结束
                PropertyEntity propertyEntity = new PropertyEntity(OvenPropEnum.VIDEO_RECORD.siid, OvenPropEnum.VIDEO_RECORD.piid, recordActionValue);
                ovenService.dealPropertyFromPlug(propertyEntity);
            }

            @Override
            public void setDishname(String value) {
                // 废弃不用处理
                Log.i(TAG, "setDishname:" + value);
            }
        });
        Log.i(TAG, "registerGetSetAction:4 ");
        //服务4   custom-recipes
        mDevice.customrecipes().setHandler(null, new Customrecipes.PropertyGetter() {
            @Override
            public String getRecipeone() {
                String recipeOne = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE1.piid);
                Log.i(TAG, "getRecipeone: recipeOne: " + recipeOne);
                return recipeOne;
            }

            @Override
            public String getRecipetwo() {
                String recipeTwo = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE2.piid);
                Log.i(TAG, "getRecipeone: recipeTwo: " + recipeTwo);
                return recipeTwo;
            }

            @Override
            public String getRecipethree() {
                String recipeThree = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE3.piid);
                Log.i(TAG, "getRecipeone: recipeThree: " + recipeThree);
                return recipeThree;
            }

            @Override
            public String getRecipefour() {
                String recipeFour = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE4.piid);
                Log.i(TAG, "getRecipeone: recipeFour: " + recipeFour);
                return recipeFour;
            }

            @Override
            public String getRecipefive() {
                String recipeFive = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE5.piid);
                Log.i(TAG, "getRecipeone: recipeFive: " + recipeFive);
                return recipeFive;
            }

            @Override
            public String getRecipesix() {
                String recipeSix = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE6.piid);
                Log.i(TAG, "getRecipeone: recipeSix: " + recipeSix);
                return recipeSix;
            }

            @Override
            public String getRecipeseven() {
                String recipeSeven = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE7.piid);
                Log.i(TAG, "getRecipeone: recipeSeven: " + recipeSeven);
                return recipeSeven;
            }

            @Override
            public String getRecipeeight() {
                String recipeEight = ovenService.getCombineRecipeInfo(OvenPropEnum.RECIPE8.piid);
                Log.i(TAG, "getRecipeone: recipeEight: " + recipeEight);
                return recipeEight;
            }
        }, new Customrecipes.PropertySetter() {
            @Override
            public void setRecipeone(String value) {
                Log.i(TAG, "setRecipeone: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE1, value);
            }

            @Override
            public void setRecipetwo(String value) {
                Log.i(TAG, "setRecipetwo: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE2, value);
            }

            @Override
            public void setRecipethree(String value) {
                Log.i(TAG, "setRecipethree: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE3, value);
            }

            @Override
            public void setRecipefour(String value) {
                Log.i(TAG, "setRecipefour: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE4, value);
            }

            @Override
            public void setRecipefive(String value) {
                Log.i(TAG, "setRecipefive: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE5, value);
            }

            @Override
            public void setRecipesix(String value) {
                Log.i(TAG, "setRecipesix: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE6, value);
            }

            @Override
            public void setRecipeseven(String value) {
                Log.i(TAG, "setRecipeseven: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE7, value);
            }

            @Override
            public void setRecipeeight(String value) {
                Log.i(TAG, "setRecipeeight: " + value);
                ovenService.setCombinedModeInfo(OvenPropEnum.RECIPE8, value);
            }
        });
        // 服务5 custom-mode
        mDevice.custommode().setHandler(null, new Custommode.PropertyGetter() {
            @Override
            public String getModeone() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE1.piid);
            }

            @Override
            public String getModetwo() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE2.piid);

            }

            @Override
            public String getModethree() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE3.piid);
            }

            @Override
            public String getModefour() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE4.piid);

            }

            @Override
            public String getModefive() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE5.piid);

            }

            @Override
            public String getModesix() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE6.piid);

            }

            @Override
            public String getModeseven() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE7.piid);

            }

            @Override
            public String getModeeight() {
                return ovenService.getCombinedModeInfo(OvenPropEnum.COMBINED_MODE8.piid);

            }

        }, new Custommode.PropertySetter() {
            @Override
            public void setModeone(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE1, value);
            }

            @Override
            public void setModetwo(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE2, value);

            }

            @Override
            public void setModethree(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE3, value);

            }

            @Override
            public void setModefour(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE4, value);

            }

            @Override
            public void setModefive(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE5, value);

            }

            @Override
            public void setModesix(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE6, value);

            }

            @Override
            public void setModeseven(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE7, value);

            }

            @Override
            public void setModeeight(String value) {
                ovenService.setCombinedModeInfo(OvenPropEnum.COMBINED_MODE8, value);
            }

        });

        mDevice.safetycheck().setHandler(null, new Safetycheck.PropertyGetter() {
            /**
             * 获取三元组的的accesskey
             * @return
             */
            @Override
            public String getCheckcode() {
                Log.i(TAG, "getCheckcode: ");
                return ViomiProvideUtil.getCameraAccessKey();
            }

        }, new Safetycheck.PropertySetter() {

        });
    }

    @Override
    public void reportProperties(Map prop) {
        Iterator<Map.Entry<String, Object>> iterator = prop.entrySet().iterator();
//        Log.i(TAG, "reportProperties " + prop.keySet().size());
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            reportSingleProperty(mDevice, entry.getKey(), entry.getValue());
        }
    }

/*    @Override
    public void registerEvent(Device device, EventPack eventPack) {
        OvenDevice mDevice = (OvenDevice) device;
        Event event = null;
        String name = eventPack.name.replace("\r", "");
        switch (name) {
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.LOW_WATER_LEVEL_EVENT_IID:
                event = mDevice.oven().getEvent(Miotspecv2Defined.Event.Low_water_level.toEventType());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.WORKRECORD_EVENT_IID:
                event = mDevice.customoven().getEvent(Miotspecv2Defined.Event.WorkRecordEvent.toEventType());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.BOOKSTART_EVENT_IID:
                event = mDevice.customoven().getEvent(Miotspecv2Defined.Event.Bookstart.toEventType());
                break;
            default:
                break;
        }
        if (event == null) {
            return;
        }
        try {
            Log.i(TAG, "registerEvent:miot send event:" + name);
            MiotDataUtil.INSTANCE.sendEvent(event);
        } catch (MiotException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void getAllProperty() {
        Log.i(TAG, "MIOT registerGetAllProp getAllProperties");
    }

    /**
     * 属性上报
     */
    public void reportSingleProperty(OvenDevice mDevice, String prop, Object value) {
        List<Property> properties = new ArrayList<>();
        IOvensoService ovenService = OvensoServiceFactory.getInstance().getOvenService();
        switch (prop) {
            // 服务1
            case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.MANUFACTURER_PROPERTY_IID:
                mDevice.deviceinformation().manufacturer().setValue(value);
                properties.add(mDevice.deviceinformation().manufacturer());
                break;
            case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.MODEL_PROPERTY_IID:
                mDevice.deviceinformation().model().setValue(value);
                properties.add(mDevice.deviceinformation().model());
                break;
            case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.SERIALNUMBER_PROPERTY_IID:
                mDevice.deviceinformation().serialnumber().setValue(value);
                properties.add(mDevice.deviceinformation().serialnumber());
                break;
            case Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID + "." + Miotspecv2Defined.FIRMWAREREVISION_PROPERTY_IID:
                mDevice.deviceinformation().firmwarerevision().setValue(value);
                properties.add(mDevice.deviceinformation().firmwarerevision());
                break;
            // 服务2
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.STATUS_PROPERTY_IID:
                mDevice.oven().status().setValue(value);
                properties.add(mDevice.oven().status());
                break;
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.FAULT_PROPERTY_IID:
                mDevice.oven().fault().setValue(value);
                properties.add(mDevice.oven().fault());
                break;
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.LEFTTIME_PROPERTY_IID:
                mDevice.oven().lefttime().setValue(value);
                properties.add(mDevice.oven().lefttime());
                break;
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.WORKINGTIME_PROPERTY_IID:
                mDevice.oven().workingtime().setValue(value);
                properties.add(mDevice.oven().workingtime());
                break;
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.TEMPERATURE_PROPERTY_IID:
                mDevice.oven().temperature().setValue(value);
                properties.add(mDevice.oven().temperature());
                break;
            // 服务3
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.DISHID_PROPERTY_IID:
                mDevice.customoven().dishid().setValue(value);
                properties.add(mDevice.customoven().dishid());
                String recipeName = ovenService.getRecipeName();
                String recipeNameEncode = Base64.encodeToString(recipeName.getBytes(), Base64.DEFAULT);
                mDevice.customoven().dishname().setValue(recipeNameEncode);
                properties.add(mDevice.customoven().dishname());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.DISHNAME_PROPERTY_IID:
                mDevice.customoven().dishname().setValue(value);
                properties.add(mDevice.customoven().dishname());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MODE_PROPERTY_IID:
                mDevice.customoven().mode().setValue(value);
                properties.add(mDevice.customoven().mode());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.TEMPZ_PROPERTY_IID:
                mDevice.customoven().tempz().setValue(value);
                properties.add(mDevice.customoven().tempz());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.TIMEZ_PROPERTY_IID:
                mDevice.customoven().timez().setValue(value);
                properties.add(mDevice.customoven().timez());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.TEMPK_PROPERTY_IID:
                mDevice.customoven().tempk().setValue(value);
                properties.add(mDevice.customoven().tempk());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.TIMEK_PROPERTY_IID:
                mDevice.customoven().timek().setValue(value);
                properties.add(mDevice.customoven().timek());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.DOORISOPEN_PROPERTY_IID:
                mDevice.customoven().doorisopen().setValue(value);
                properties.add(mDevice.customoven().doorisopen());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.WORKTOTALTIME_PROPERTY_IID:
                mDevice.customoven().worktotaltime().setValue(value);
                properties.add(mDevice.customoven().worktotaltime());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.FINISHTIME_PROPERTY_IID:
                mDevice.customoven().finishtime().setValue(value);
                properties.add(mDevice.customoven().finishtime());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.WATERTANK_PROPERTY_IID:
                mDevice.customoven().watertank().setValue(value);
                properties.add(mDevice.customoven().watertank());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.PREPARETIME_PROPERTY_IID:
                mDevice.customoven().preparetime().setValue(value);
                properties.add(mDevice.customoven().preparetime());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.OVENHARDWAREVER_PROPERTY_IID:
                mDevice.customoven().ovenhardwarever().setValue(value);
                properties.add(mDevice.customoven().ovenhardwarever());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.OVENLIGHT_PROPERTY_IID:
                mDevice.customoven().ovenlight().setValue(value);
                properties.add(mDevice.customoven().ovenlight());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.RECIPESTEP_PROPERTY_IID:
                mDevice.customoven().recipestep().setValue(value);
                properties.add(mDevice.customoven().recipestep());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.RECORD_PROPERTY_IID:
                mDevice.customoven().record().setValue(value);
                properties.add(mDevice.customoven().recipestep());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MICROLEVEL_PROPERTY_IID:
                mDevice.customoven().microlevel().setValue(value);
                properties.add(mDevice.customoven().recipestep());
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MODENAME_PROPERTY_IID:
                mDevice.customoven().modename().setValue(value);
                properties.add(mDevice.customoven().recipestep());
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MODESTEP_PROPERTY_IID:
                mDevice.customoven().modestep().setValue(value);
                properties.add(mDevice.customoven().recipestep());
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MICROTIME_PROPERTY_IID:
                mDevice.customoven().microtime().setValue(value);
                properties.add(mDevice.customoven().recipestep());
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.MIXMODEINDICATOR_PROPERTY_IID:
                mDevice.customoven().mixmodeindicator().setValue(value);
                properties.add(mDevice.customoven().recipestep());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.WATERTANKISCLOSE_PROPERTY_IID:
                mDevice.customoven().watertankisclose().setValue(value);
                properties.add(mDevice.customoven().recipestep());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.SCREENCUSRECIPES_PROPERTY_IID:
                mDevice.customoven().screencusrecipes().setValue(value);
                properties.add(mDevice.customoven().recipestep());
                break;
        }
        if (properties.size() == 0) {
            Log.i(TAG, "reportSingleProperty: properties size is 0 return ");
            return;
        }
        try {
            MiotHostManager.getInstance().sendMiotSpecEvent(properties, null);
        } catch (MiotException e) {
            Log.i(TAG, "reportSingleProperty: MiotException: " + e.getMessage());
        }
    }

    public void reportEvent(String eventName) {
        eventName = eventName.replace("\r", "");
        Log.i(TAG, "reportEvent: eventName: " + eventName);
        Event event = null;
        switch (eventName) {
            // 服务名字 + 服务事件
            case Miotspecv2Defined.OVEN_SERVICE_IID + "." + Miotspecv2Defined.LOW_WATER_LEVEL_EVENT_IID:
                event = mDevice.oven().getEvent(Miotspecv2Defined.Event.LowWaterLevel.toEventType());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.WORKRECORD_EVENT_IID:
                event = mDevice.customoven().getEvent(Miotspecv2Defined.Event.Workrecord.toEventType());
                break;
            case Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID + "." + Miotspecv2Defined.BOOKSTART_EVENT_IID:
                event = mDevice.customoven().getEvent(Miotspecv2Defined.Event.Bookstart.toEventType());
                break;
            default:
                break;
        }
        if (event == null) {
            Log.i(TAG, "reportEvent: event is null return ");
            return;
        }
        try {
            MiotHostManager.getInstance().sendMiotSpecEventNotify(event, null);
        } catch (MiotException e) {
            Log.i(TAG, "reportEvent:  MiotException : " + e.getMessage());
        }
    }

    /**
     * 开始/继续烹饪，结束烹饪，暂停
     * status  0  开始  1 结束  2 暂停
     */
    public void setOvenStatus(OvenActionEnum actionEnum, int targetStatus) {
        Log.i(TAG, "setOvenStatus:" + actionEnum.name);
        if (actionEnum == OvenActionEnum.ACTION_CANCLE_PREPARE) {
            return;
        }
        if (actionEnum == OvenActionEnum.ACTION_APPOINT) {
            return;
        }
        List<PropertyEntity> paramsProp = new ArrayList<>();
        PropertyEntity propertyEntity = new PropertyEntity(actionEnum.siid, 1, targetStatus);
        paramsProp.add(propertyEntity);
        OvensoServiceFactory.getInstance().getOvenService().doActionFromPlug(actionEnum.siid, actionEnum.aiid, paramsProp);
    }
}

