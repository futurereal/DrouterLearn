package com.viomi.waterpurifier.edison.viewmodule;

import android.app.Application;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.SingleLiveEvent;
import com.viomi.ovensocommon.ViomiBaseViewModel;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.config.TemperatureModeEnum;
import com.viomi.waterpurifier.edison.config.WaterFlowEnum;
import com.viomi.waterpurifier.edison.config.WaterQualityEnum;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.WaterMainActivity;
import com.viomi.waterpurifier.edison.ui.fragment.ChangeConfirmFragment;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 主界面ViewModel
 */
public class WaterMainViewModel extends ViomiBaseViewModel {
    private static final String TAG = "MainActivityViewModel";
    public SingleLiveEvent<Boolean> wifiStatusSingleLiveEvent = new SingleLiveEvent<>();

    // 温度
    public SingleLiveEvent<Integer> waterTemperatureSingleLiveEvent = new SingleLiveEvent<>();
    // 当前出水的量
    public SingleLiveEvent<Integer> currentWaterOutAmount = new SingleLiveEvent<>();
    //  出水状态
    public SingleLiveEvent<Boolean> waterOutStatusSingleLiveEvent = new SingleLiveEvent<>();
    // 水质类型
    public SingleLiveEvent<WaterQualityEnum> waterQualitySingleLiveEvent = new SingleLiveEvent<>();
    // 水量模式
    public SingleLiveEvent<WaterFlowEnum> flowModeLiveEvent = new SingleLiveEvent<>();
    //  水温模式
    public SingleLiveEvent<TemperatureModeEnum> temperatureModeLiveEvent = new SingleLiveEvent<>();
    private WaterMainActivity.WaterMainHandler waterMainHandler;

    public WaterMainViewModel(@NonNull Application application) {
        super(application);
        Log.i(TAG, "WaterMainViewModel: ");
        PropertyPreferenceManager propertyPreferenceManager = PropertyPreferenceManager.getInstance();
        int cupSmall = (int) propertyPreferenceManager.getProperty(WaterPropEnum.SMALL_CUP_FLOW.siid, WaterPropEnum.SMALL_CUP_FLOW.piid, WaterConstant.DEFAULT_SMALL_CUP_FLOW);
        int cupMiddle = (int) propertyPreferenceManager.getProperty(WaterPropEnum.MIDDLE_CUP_FLOW.siid, WaterPropEnum.MIDDLE_CUP_FLOW.piid, WaterConstant.DEFAULT_MID_CUP_FLOW);
        int cupBig = (int) propertyPreferenceManager.getProperty(WaterPropEnum.BIG_CUP_FLOW.siid, WaterPropEnum.BIG_CUP_FLOW.piid, WaterConstant.DEFAULT_BIG_CUP_FLOW);
        int[] flowAmounts = new int[]{Integer.MAX_VALUE, cupSmall, cupMiddle, cupBig};
    }

    /**
     * 更新WIFI 状态并且监听变化
     */
    public void updateNetStatusAndListener() {
        // 首次进入判断是否联网
        boolean isNetConnect = NetworkUtils.isWifiConnected();
        Log.i(TAG, "updateWifiStatusAndListener: isNetConnect " + isNetConnect);
        wifiStatusSingleLiveEvent.postValue(isNetConnect);
        // 防止內存
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            Object msgObject = viomiRxBusEvent.getMsgObject();
            int msgId = viomiRxBusEvent.getMsgId();
            if (msgId == CommonConstant.MSG_WIFI_STATUS_CHANGE) {// WIFI是否打开
                Log.i(TAG, "updateNetStatusAndListener: " + (boolean) msgObject);
                wifiStatusSingleLiveEvent.postValue((boolean) msgObject);
            }
        });
    }

    /**
     * 增加温度
     *
     * @param view
     */
    public void increaseTemperature(View view) {
        Integer temperature = waterTemperatureSingleLiveEvent.getValue();
        Log.i(TAG, "increaseTemperature: temperature: " + temperature);
        updateTemperature(temperature + 1);
    }

    /**
     * 减少温度
     *
     * @param view
     */
    public void decreaseTemperature(View view) {
        Integer temperature = waterTemperatureSingleLiveEvent.getValue();
        Log.i(TAG, "decreaseTemperature: temperature: " + temperature);
        updateTemperature(temperature - 1);
    }

    /**
     * 更新温度
     *
     * @param targetTemperature
     */
    private void updateTemperature(int targetTemperature) {
        Log.i(TAG, "updateTemperature: " + targetTemperature);
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        Log.i(TAG, "updateTemperature : " + isError);
        if (isError) {
            return;
        }
        Message message = waterMainHandler.obtainMessage();
        message.what = WaterMainActivity.MSG_TEMPERATURE_CHANGED;
        message.obj = targetTemperature;
        waterMainHandler.sendMessage(message);
    }

    /**
     * 温度模式
     **/
    public void changeTemperatureMode(View view) {
        // 检查错误并且弹出框
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        Log.i(TAG, "changeTemperatureMode waterOutListener : " + isError);
        if (isError) {
            return;
        }
        TemperatureModeEnum temperatureModeEnum = temperatureModeLiveEvent.getValue();
        int currentTemperatureMode = temperatureModeEnum.ordinal();
        // 如果是自定义模式去之前存的模式
        if (temperatureModeEnum == TemperatureModeEnum.CUSTOM) {
            currentTemperatureMode = (int) WaterPreference.getInstance().getWaterProperty(WaterPreference.KEY_CUSTOMMODE_INDEX, 0);
        }
        Log.d(TAG, "changeTemperatureMode currentTemperatureMode: " + currentTemperatureMode);
        int totalCount = TemperatureModeEnum.values().length - 1;
        int targetModeIndex = currentTemperatureMode + 1;
        if (targetModeIndex == totalCount) {
            targetModeIndex = 0;
        }
        int finalTargetModeIndex = targetModeIndex;
        Log.i(TAG, "changeTemperatureMode: finalTargetIndex: " + finalTargetModeIndex);
        PropertyEntity propertyEntity = new PropertyEntity(WaterPropEnum.TEMP_MODE.siid, WaterPropEnum.TEMP_MODE.piid, targetModeIndex);
        WaterSerialManager.getInstance().writeProperty(propertyEntity);
    }

    // 杯量模式
    public void changeWaterFlowMode(View view) {
        // 检查错误并且弹出框
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        Log.i(TAG, "changeWaterFlowMode : " + isError);
        if (isError) {
            return;
        }
        WaterFlowEnum currentWaterFlow = flowModeLiveEvent.getValue();
        Log.d(TAG, "changeWaterFlowMode currentWaterFlow: " + currentWaterFlow);
        int totalCount = WaterFlowEnum.values().length;
        Log.i(TAG, "changeWaterFlowMode totalCount: " + totalCount);
        int targetModeIndex = currentWaterFlow.ordinal() + 1;
        if (targetModeIndex == totalCount) {
            targetModeIndex = 0;
        }
        Log.i(TAG, "changeWaterFlowMode targetModeIndex : " + targetModeIndex);
        WaterFlowEnum targetWaterFlowEnum = WaterFlowEnum.values()[targetModeIndex];
        PropertyEntity modeProperty = new PropertyEntity(WaterPropEnum.CUP_MODE.siid, WaterPropEnum.CUP_MODE.piid, targetWaterFlowEnum.ordinal());
        ArrayList<PropertyEntity> propertyEntities = new ArrayList<>();
        propertyEntities.add(modeProperty);
        WaterSerialManager.getInstance().writePropertyList(propertyEntities);
    }

    // 水质切换
    public void changeWaterMineralMode(View view) {
        // 检查错误并且弹出框
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        if (isError) {
            Log.i(TAG, "changeWaterMineralListener : isError true  return");
            return;
        }
        ChangeConfirmFragment changeConfirmFragment = new ChangeConfirmFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ChangeConfirmFragment.KEY_CURRENT_QUALITY, waterQualitySingleLiveEvent.getValue().ordinal());
        changeConfirmFragment.setArguments(bundle);
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        changeConfirmFragment.show(fragmentActivity.getSupportFragmentManager(), "CommonDialogFragment");
    }

    public void setMainHandler(WaterMainActivity.WaterMainHandler waterMainHandler) {
        this.waterMainHandler = waterMainHandler;
    }
}
