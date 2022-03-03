package com.viomi.ovenso.ui.activity.running;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.bean.OvenRecipeStepEnum;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.OvenSoCameraActivity;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.camera.CameraServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2020/11/24.
 * Description:
 */
public class CookRunningPresenter extends RunContract.Presenter<RunContract.View> {
    private static final String TAG = "CookRunningPresenter";
    private CompositeDisposable mCompositeDisposable;
    private String currentActionName;
    private int statusEnumValue;

    public CookRunningPresenter() {
        Log.i(TAG, "CookRunningPresenter: ");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        mCompositeDisposable = new CompositeDisposable();
        subscrebeData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }


    @Override
    public void cmdRunOpt(OvenActionEnum actionEnum) {
        currentActionName = actionEnum.name;
        Log.i(TAG, "cmdRunOpt: actionName: " + currentActionName);
        if (TextUtils.equals(currentActionName, OvenActionEnum.ACTION_CANCLE_PREPARE.name)) {
            OvenSerialManager.getInstance().doOtherCustomAction(actionEnum, true, null);
        } else {
            OvenSerialManager.getInstance().doStandardAction(actionEnum);
        }
    }

    /**
     * 监听烤箱状态的变化
     */
    public void subscrebeData() {
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(ovenBusEvent -> {
            if (mView == null) {
                Log.i(TAG, "subscrebeData: mView is null return ");
                return;
            }
            mView.updatePropertyView();
            Object content = ovenBusEvent.getMsgObject();
            int msgId = ovenBusEvent.getMsgId();
            Log.i(TAG, "subscrebeData:  msgId: " + msgId + "  content:" + content);
            switch (msgId) {
                case OvenBusEventConstants.MSG_COOK_STATUSCHANGE:
                    statusEnumValue = (int) content;
                    mView.updateWorkStatus(statusEnumValue);
                    break;
                case OvenBusEventConstants.MSG_UPDATE_LEFTTIME:
                    int leftTIme = (int) content;
                    mView.updateLeftTime(leftTIme);
                    break;
                case OvenBusEventConstants.MSG_UPDATE_WORKTIME:
                    int recipeStepOne = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.RECIPE_STEP.siid, OvenPropEnum.RECIPE_STEP.piid, 0);
                    boolean isPreheatOne = recipeStepOne == OvenRecipeStepEnum.STEP_PREHEARTING.stepValue;
                    // 预约状态 或者  是预热 更新已用时间
                    if (statusEnumValue == OvenWorkStatusEnum.BOOKED.value || isPreheatOne) {
                        int workTime = (int) content;
//                        mView.updateTime(workTime);
                    }
                    break;
                case OvenBusEventConstants.MSG_UPDATE_TEMPERATURE:
                    int temperature = (int) content;
                    mView.updateCurrentTemperature(temperature);
                    break;
                case OvenBusEventConstants.MSG_UPDATE_RECORD_STATUS:
                    int recordStatus = (int) content;
                    Activity topActiivty = ActivityUtils.getTopActivity();
                    if (recordStatus == CommonConstant.RECORD_STATE_PAUSE
                            && TextUtils.equals(topActiivty.getClass().getName(), OvenSoCameraActivity.class.getName())) {
                        Log.i(TAG, "subscrebeData: finish cameraActivivity");
                        topActiivty.finish();
                    }
                    Log.i(TAG, "subscrebeData:MSG_DEAL_RECORD  " + content);
                    mView.updateRecordStatus(recordStatus);
                    break;
                case OvenBusEventConstants.MSG_DEAL_RECORD_STATUS:
                    int dealRecordStauts = (int) content;
                    Log.i(TAG, "subscrebeData:MSG_DEAL_RECORD_STATUS  " + dealRecordStauts);
                    String topActivity = ActivityUtils.getTopActivity().getClass().getName();
                    Log.i(TAG, "subscrebeData:MSG_DEAL_RECORD_STATUS topActivity:  " + topActivity);
                    if (!TextUtils.equals(topActivity, OvenSoCameraActivity.class.getName()) && dealRecordStauts == CommonConstant.RECORD_STATE_RECORDING) {
                        Log.i(TAG, "subscrebeData:MSG_DEAL_RECORD_STATUS startCamera:  ");
                        mView.startCameraActivity();
                    } else {
                        Log.i(TAG, "subscrebeData:MSG_DEAL_RECORD_STATUS updateRecordStatus:  ");
                        CameraServiceFactory.getInstance().getCameraService().updateRecordStatus(dealRecordStauts);
                    }
                    break;
                case CommonConstant.MSG_FINISH_ACTIVITY:
                    mView.finishSelft();
                    break;
                case OvenBusEventConstants.MSG_CUSTOM_STEP:
                    int customStep = (int) content;
                    Log.i(TAG, "subscrebeData: " + customStep);
                    mView.updateCookStep(customStep);
                    break;
                case OvenBusEventConstants.MSG_MODE_STEP:
                    int modeStep = (int) content;
                    Log.i(TAG, "subscrebeData: " + modeStep);
                    mView.updateModeStep(modeStep);
                    break;
                case OvenBusEventConstants.MSG_WATER_TANK_CLOSE:
                    boolean isClose = (boolean) content;
                    Log.i(TAG, "subscrebeData: waterTankState: " + isClose);
                    mView.waterTankState(isClose);
                    break;
                case CommonConstant.MSG_SHOW_EDITE_FRAGMENT:
                    mView.showEidtFragment();
                    break;
            }
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        mCompositeDisposable.dispose();
        mCompositeDisposable = null;
    }

    /**
     * 初始状态的赋值
     *
     * @param statusEnumValue
     */
    public void setStatusEnumValue(int statusEnumValue) {
        this.statusEnumValue = statusEnumValue;
    }


}

