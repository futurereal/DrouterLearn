package com.viomi.waterpurifier.edison.manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.entity.WaterErrorEntity;
import com.viomi.waterpurifier.edison.ui.fragment.FilterErrorFragment;
import com.viomi.waterpurifier.edison.ui.fragment.FilterWashFragment;
import com.viomi.waterpurifier.edison.ui.fragment.FirstOutFragment;
import com.viomi.waterpurifier.edison.ui.fragment.FlushErrorDialog;
import com.viomi.waterpurifier.edison.ui.fragment.SelfCleanFragment;
import com.viomi.waterpurifier.edison.util.WaterActionUtils;
import com.viomi.waterpurifier.edison.util.WaterUtils;
import com.viomi.waterpurifier.edison.widget.MessageTipLayout;

import java.util.List;

/**
 * @description: 消息弹框的管理类
 * @data:2021/8/27
 */
public class MessageDialogManager {
    private static final String TAG = "MessageDialogManager";
    private static volatile MessageDialogManager instance;
    private int currentErrorIndex = 0;
    private FlushErrorDialog deviceErrorDialog;
    private List<WaterErrorEntity> waterErrorEntityList;
    private int currentRoLife;
    private FlushErrorDialog lackWaterDialog;
    private FilterErrorFragment filterErrorFragment;
    private SelfCleanFragment selfCleanFragment;

    private MessageDialogManager() {
    }

    public static MessageDialogManager getInstance() {
        if (instance == null) {
            synchronized (MessageDialogManager.class) {
                if (instance == null) {
                    instance = new MessageDialogManager();
                }
            }
        }
        return instance;
    }

    public void clearInstance() {
        instance = null;
    }

    /**
     * 显示串口故障的弹框
     */
    public void showSerialDisConnectDialog() {
        Log.i(TAG, "showSerialDisConnectDialog: ");
        FlushErrorDialog serialDisconnectDialog = new FlushErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(FlushErrorDialog.KEY_BUNDLE, new WaterErrorEntity(MessageTipLayout.TYPE_SERIAL, MessageTipLayout.TIP_SERIAL, MessageTipLayout.TIP_SERIAL_CONTENT, true));
        serialDisconnectDialog.setArguments(bundle);
        if (serialDisconnectDialog.getDialog() == null || serialDisconnectDialog.getDialog().isShowing() == false) {
            Log.i(TAG, "showSerialDisConnectDialog: show");
            FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            serialDisconnectDialog.show(fragmentActivity.getSupportFragmentManager(), "flushErrorDialog");
        }
    }

    /**
     * 缺水的弹框
     */
    public void showLackWaterDialog() {
        if (true) return;
        Log.i(TAG, "showLackWaterDialog: ");
        if (lackWaterDialog == null) {
            lackWaterDialog = new FlushErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putParcelable(FlushErrorDialog.KEY_BUNDLE, new WaterErrorEntity(MessageTipLayout.TYPE_WATER_LACK, MessageTipLayout.TIP_WATER_LACK, MessageTipLayout.TIP_WATER_LACK_CONTENT, true));
            lackWaterDialog.setArguments(bundle);
        }
        if (lackWaterDialog.getDialog() == null || lackWaterDialog.getDialog().isShowing() == false) {
            FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            lackWaterDialog.show(fragmentActivity.getSupportFragmentManager(), "flushErrorDialog");
        }
    }

    public void dismissLackWaterDialog() {
        if (lackWaterDialog != null && lackWaterDialog.getDialog() != null
                && lackWaterDialog.getDialog().isShowing()) {
            lackWaterDialog.dismiss();
        }
    }

    /**
     * 显示单个 设备故障弹框
     *
     * @param waterErrorEntity
     */
    public void showDeviceFaultDialog(WaterErrorEntity waterErrorEntity) {
        Log.i(TAG, "showDeviceFaultDialog: waterErrorEntity : " + waterErrorEntity);
        if (deviceErrorDialog == null) {
            deviceErrorDialog = new FlushErrorDialog();
            deviceErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.i(TAG, "onDismiss: currentErrorIndex");
                    if (waterErrorEntityList == null || currentErrorIndex >= waterErrorEntityList.size()) {
                        Log.i(TAG, "onDismiss: isLastDialog return ");
                        return;
                    }
                    WaterErrorEntity waterErrorEntity = waterErrorEntityList.get(currentErrorIndex);
                    currentErrorIndex++;
                    showDeviceFaultDialog(waterErrorEntity);
                }
            });
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(FlushErrorDialog.KEY_BUNDLE, waterErrorEntity);
        deviceErrorDialog.setArguments(bundle);
        if (deviceErrorDialog.getDialog() == null || deviceErrorDialog.getDialog().isShowing() == false) {
            FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
            deviceErrorDialog.show(fragmentActivity.getSupportFragmentManager(), "flushErrorDialog");
        }
    }

    public void dismissDeviceFaultDialog() {
        if (deviceErrorDialog != null && deviceErrorDialog.getDialog() != null
                && deviceErrorDialog.getDialog().isShowing()) {
            deviceErrorDialog.dismiss();
        }
    }

    /**
     * 显示多个设备故障的弹框
     *
     * @param waterErrorEntityList 设备故障的集合
     */
    public void showMutilFaultsDialog(List<WaterErrorEntity> waterErrorEntityList) {
        this.waterErrorEntityList = waterErrorEntityList;
        WaterErrorEntity waterErrorEntity = waterErrorEntityList.get(currentErrorIndex);
        currentErrorIndex++;
        showDeviceFaultDialog(waterErrorEntity);

    }

    /**
     * 滤芯故障对话框
     */
    public void showFilterErrorDialog(PropertyEntity propertyEntity) {
        if (filterErrorFragment == null) {
            filterErrorFragment = new FilterErrorFragment();
        }
        if (filterErrorFragment.getDialog() != null && filterErrorFragment.getDialog().isShowing()) {
            return;
        }
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.setLifeLevelSiid(propertyEntity.getSid());
        filterEntity.setLifeLevelPiid(propertyEntity.getPid());
        List<FilterEntity> filterEntityList = WaterUtils.getFilterEntityList();
        int index = filterEntityList.indexOf(filterEntity);
        FilterEntity fullInfoEntity = filterEntityList.get(index);
        Bundle bundle = new Bundle();
        bundle.putParcelable(WaterConstant.KEY_FILTERENTIY, fullInfoEntity);
        filterErrorFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // 再显示RO故障表
            }
        });
        filterErrorFragment.setArguments(bundle);
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        filterErrorFragment.show(fragmentActivity.getSupportFragmentManager(), "FilterErrorTipsDialogFragment");
    }

    public void dismissFilterErrorDialog() {
        if (filterErrorFragment != null && filterErrorFragment.getDialog() != null
                && filterErrorFragment.getDialog().isShowing()) {
            filterErrorFragment.dismiss();
        }
    }


    public void showFirstOutAlarmDialog(int firstOutAlarmTime) {
        FirstOutFragment filterErrorTipsDialog = new FirstOutFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FirstOutFragment.KEY_PROTECT_TIME, firstOutAlarmTime);
        filterErrorTipsDialog.setArguments(bundle);
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        filterErrorTipsDialog.show(fragmentActivity.getSupportFragmentManager(), "FirstWateroutFragment");
    }

    public boolean checkErrorAndShowDialog() {
        boolean serialDisconnect = CommonPreference.getInstance().getSerialDisconnect();
        Log.i(TAG, "checkErrorAndShowDialog: serialDisconnect: " + serialDisconnect);
        // 判断串口
        if (serialDisconnect) {
            showSerialDisConnectDialog();
            return true;
        }
        // 判断是否缺水 ,需要判断state
        int waterLackStatus = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.CISTERN.siid, WaterPropEnum.CISTERN.piid, -1);
        Log.i(TAG, "checkErrorAndShowDialog: waterLackStatus : " + waterLackStatus);
        if (waterLackStatus == 1) {
            showLackWaterDialog();
            return true;
        }
        return false;
    }

    public void showSelfCleanFragment() {
        // 是否正在出水
        String waterOutState = (String) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.WATER_OUT_STATUS.siid
                , WaterPropEnum.WATER_OUT_STATUS.piid, "");
        Log.i(TAG, "showSelfCleanFragment: waterOutState: " + waterOutState);
        if (waterOutState.contains(WaterConstant.WATEROUT_PURING)) {
            WaterActionUtils.stopOutWater();
        }
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        Log.d(TAG, "self clean---showCleanDialog: ");
        if (selfCleanFragment == null) {
            selfCleanFragment = new SelfCleanFragment();
        }
        if (selfCleanFragment.getDialog() != null && selfCleanFragment.getDialog().isShowing()) {
            return;
        }
        selfCleanFragment.show(fragmentActivity.getSupportFragmentManager(), "SelfCleanFragment");
    }

    public void dissMissSelfFragment() {
        Log.i(TAG, "dissMissSelfFragment: ");
        if (selfCleanFragment != null && selfCleanFragment.getDialog() != null && selfCleanFragment.getDialog().isShowing()) {
            selfCleanFragment.dismissAllowingStateLoss();
            // 自清洁 消除后，需要置空 1 、减少内存占用  2 、再次启动的话还是需要创建。否则会绑定错误
            selfCleanFragment = null;
        }
    }

    public void showFilterWashFragment() {
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        Log.d(TAG, "self clean---showCleanDialog: ");
        FilterWashFragment filterWashFragment = new FilterWashFragment();
        filterWashFragment.show(fragmentActivity.getSupportFragmentManager(), "filterWashFragment");
    }

}
