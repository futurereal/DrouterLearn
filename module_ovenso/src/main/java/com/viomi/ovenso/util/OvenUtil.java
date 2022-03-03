package com.viomi.ovenso.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.bean.OvenDeviceFaultEnum;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.serial.OvenPropertyListWrite;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.ui.activity.OvenSoCameraActivity;
import com.viomi.ovenso.ui.activity.custommode.ModeSelectFragment;
import com.viomi.ovenso.ui.fragment.AppointFragment;
import com.viomi.ovensocommon.CommonAffirmFragment;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/10/6
 */
public class OvenUtil {
    private static final String TAG = "OvenUtil";
    private static CommonAffirmFragment deviceFaultDialog;

    /**
     * 开始/继续烹饪，结束烹饪，暂停
     */
    private static void setOvenStatus(OvenActionEnum actionEnum) {
        Log.i(TAG, "actionEnum:" + actionEnum);
        if (actionEnum != OvenActionEnum.ACTION_CANCLE_PREPARE && actionEnum != OvenActionEnum.ACTION_APPOINT) {
            List<PropertyEntity> paramsProp = new ArrayList<>();
            // 启动烹饪必须传 0  1  1 属性，否则无法启动
            PropertyEntity propertyEntity = new PropertyEntity(0, 1, 1);
            paramsProp.add(propertyEntity);
            SerialControl.setAction(actionEnum.name, actionEnum.siid, actionEnum.aiid, paramsProp);
        }
    }

    /**
     * 预约
     */
    private static void startAppoint(int dishId, int appointTime) {
        Log.i(TAG, "startAppoint: appointTime: " + appointTime + " dishId: " + dishId);
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        PropertyEntity propertyDish = new PropertyEntity(1, OvenPropEnum.DISHID.piid, dishId);
        propertyEntities.add(propertyDish);
        PropertyEntity propertyTime = new PropertyEntity(1, OvenPropEnum.APPOINT_TOTAL_TIME.piid, appointTime);
        propertyEntities.add(propertyTime);
        SerialControl.setAction(OvenActionEnum.ACTION_APPOINT.name, OvenActionEnum.ACTION_APPOINT.siid, OvenActionEnum.ACTION_APPOINT.aiid, propertyEntities);
    }

    // 显示预约的框
    public static void showAppointFragment(float totalCookTime, int dishId, List<PropertyEntity> propertyEntityList) {
        Log.i(TAG, "showAppointFragment: totalTime: " + totalCookTime);
        FragmentActivity mActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        AppointFragment appointFragment = new AppointFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppointFragment.KEY_COOK_TIME, (int) totalCookTime);
        bundle.putInt(AppointFragment.KEY_DISH_ID, dishId);
        appointFragment.setArguments(bundle);
        appointFragment.setPropertyEntityList(propertyEntityList);
        appointFragment.show(mActivity.getSupportFragmentManager(), AppointFragment.class.getSimpleName());
    }

    /**
     * 把录制中的状态传给电控
     */
    public static void setRecordingStatusToMcu(int recordStaus) {
        Log.i(TAG, "setRecordingStatusToMcu: " + recordStaus);
        ArrayList<PropertyEntity> propertyEntityList = new ArrayList<>();
        PropertyEntity recordProperty = new PropertyEntity();
        recordProperty.setSid(OvenPropEnum.VIDEO_RECORD.siid);
        recordProperty.setPid(OvenPropEnum.VIDEO_RECORD.piid);
        recordProperty.setContent(recordStaus);
        propertyEntityList.add(recordProperty);
        OvenPropertyListWrite ovenPropertyListWrite = new OvenPropertyListWrite(propertyEntityList);
        ovenPropertyListWrite.executeWrite();
    }

    public static boolean isShowFaultDialog() {
        Integer errCode = (Integer) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, 0);
        Log.i(TAG, "errCode: " + errCode);
        if (errCode != OvenConstants.CMD_SET_SUCCESS) {
            showDeviceFaultDialog(errCode);
            return true;
        }
        return false;
    }

    public static OvenDeviceFaultEnum showDeviceFaultDialog(int deviceFalutValue) {
        Log.i(TAG, "showDeviceFaultDialog: " + deviceFalutValue);
        for (OvenDeviceFaultEnum ovenDeviceFaultEnum : OvenDeviceFaultEnum.values()) {
            //使用最新的故障，处理有写多余
            if ((ovenDeviceFaultEnum.value & deviceFalutValue) == ovenDeviceFaultEnum.value) {
                showDeviceFaultDialog(ovenDeviceFaultEnum.titleId, ovenDeviceFaultEnum.msgId);
                return ovenDeviceFaultEnum;
            }
        }
        return null;
    }

    public static void showDeviceFaultDialog(int titleResorceId, int contentId) {
        // 判断当前是否在烹饪界面
        Log.i(TAG, "showDeviceFaultDialog");
        deviceFaultDialog = new CommonAffirmFragment();
        deviceFaultDialog.setCancelable(false);
        deviceFaultDialog.setPositiveClickListener(dialog -> {
            OvenSerialManager.getInstance().doOtherCustomAction(OvenActionEnum.ACTION_CLEAR_FAULT, false, null);
            dialog.dismiss();
        });
        String title = ApplicationUtils.getContext().getString(titleResorceId);
        String content = ApplicationUtils.getContext().getString(contentId);
        Bundle bundle = CommonAffirmFragment.getBundle(title, content, "", "我知道了", false, 0);
        deviceFaultDialog.setArguments(bundle);

        String topActivityName = ActivityUtils.getTopActivity().getClass().getName();
        if (TextUtils.equals(topActivityName, OvenSoCameraActivity.class.getName())) {
            Log.i(TAG, "showDeviceFaultDialog: topActivity is Camera");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager supportFragmentManager = ((FragmentActivity) ActivityUtils.getTopActivity()).getSupportFragmentManager();
                    deviceFaultDialog.show(supportFragmentManager, deviceFaultDialog.getClass().getSimpleName());
                }
            }, 1000);
        } else {
            FragmentManager supportFragmentManager = ((FragmentActivity) ActivityUtils.getTopActivity()).getSupportFragmentManager();
            deviceFaultDialog.show(supportFragmentManager, deviceFaultDialog.getClass().getSimpleName());
        }
    }

    public static void dismissDeviceFalutDialog() {
        Log.i(TAG, "dismissDeviceFalutDialog: " + deviceFaultDialog);
        if (deviceFaultDialog != null && deviceFaultDialog.getDialog() != null && deviceFaultDialog.getDialog().isShowing()) {
            Log.i(TAG, "dismissDeviceFalutDialog: dismiss");
            deviceFaultDialog.dismissAllowingStateLoss();
            deviceFaultDialog = null;
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getPowerName(int definePower) {
        String definlePowerStr = "";
        if (definePower > 10) {
            definlePowerStr = definlePowerStr + definePower;
        } else {
            definlePowerStr = getMicroPowerNames().get(definePower - 1);
        }
        return definlePowerStr;
    }


    public static List<String> getMicroPowerNames() {
        int[] microPowerNamesId = new int[]{R.string.microwave_powername_one, R.string.microwave_powername_two,
                R.string.microwave_powername_three, R.string.microwave_powername_four, R.string.microwave_powername_five};
        List<String> microwavePowerNames = new ArrayList<>(microPowerNamesId.length);
        for (int powerNameId : microPowerNamesId) {
            String powerName = OvenApplication.getContext().getString(powerNameId);
            microwavePowerNames.add(powerName);
        }
        return microwavePowerNames;
    }

    public static void showModeSelectFragment(ArrayList<ModeTypeEntity> modeTypeEntityList, int currentPosition) {
        FragmentActivity mActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        Log.i(TAG, "showModeSelectFragment: topActivity: " + mActivity.getLocalClassName());
        ModeSelectFragment modeSelectFragment = new ModeSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ModeSelectFragment.KEY_MODETYPE_ENTITY_LIST, modeTypeEntityList);
        bundle.putInt(ModeSelectFragment.KEY_MODETYPE_POSITION, currentPosition);
        modeSelectFragment.setArguments(bundle);
        modeSelectFragment.setCancelable(false);
        modeSelectFragment.show(mActivity.getSupportFragmentManager(), "PercentDialog");
    }

    public static boolean isMicroMode(@Nullable String modeType) {
        return TextUtils.equals(OvenConstants.MODE_TYPE_MICROWAVE, modeType);
    }

    /**
     * @return 目标工作状态
     */
    public static OvenWorkStatusEnum getTargetWorkStaus(int siid, int aiid) {
        OvenWorkStatusEnum targetWorkStatus = OvenWorkStatusEnum.IDLE;
        Log.i(TAG, "preReportResult: siid: " + siid + " aiid: " + aiid);
        if (OvenActionEnum.ACTION_START.siid == siid && OvenActionEnum.ACTION_START.aiid == aiid) {
            targetWorkStatus = OvenWorkStatusEnum.WORKING;
        } else if (OvenActionEnum.ACTION_PAUSE.siid == siid && OvenActionEnum.ACTION_PAUSE.aiid == aiid) {//暂停烹饪
            targetWorkStatus = OvenWorkStatusEnum.PAUSE;
        } else if (OvenActionEnum.ACTION_OVER.siid == siid && OvenActionEnum.ACTION_OVER.aiid == aiid) {//结束烹饪
            targetWorkStatus = OvenWorkStatusEnum.IDLE;
        } else if (OvenActionEnum.ACTION_CANCLE_PREPARE.siid == siid && OvenActionEnum.ACTION_CANCLE_PREPARE.aiid == aiid) {//预约
            targetWorkStatus = OvenWorkStatusEnum.BOOKED;
        } else if (OvenActionEnum.ACTION_CANCLE_PREPARE.siid == siid && OvenActionEnum.ACTION_CANCLE_PREPARE.aiid == aiid) {//取消预约
            targetWorkStatus = OvenWorkStatusEnum.IDLE;
        }
        return targetWorkStatus;
    }

    public static String getFloatString(float totleTime) {
        String floatStr = "";
        int totalTimeInt = (int) totleTime;
        if (totalTimeInt == totleTime) {
            floatStr = String.valueOf(totalTimeInt);
        } else {
            floatStr = String.valueOf(totleTime);
        }
        return floatStr;
    }

}
