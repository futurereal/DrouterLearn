package com.viomi.waterpurifier.edison.presenter;


import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.fragment.FlushFragment;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class FilterResetPresenter implements FlushContact.Presenter {
    private static final String TAG = "FilterResetPresenter";
    private static final int FILTER_FLUSH_TIME = 1000;
    private final FragmentActivity fragmentActivity;
    FlushContact.View view;
    Disposable disposable;

    public FilterResetPresenter(DialogFragment dialogFragment) {
        this.fragmentActivity = dialogFragment.getActivity();
    }

    public FilterResetPresenter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public void subscribe(FlushContact.View view) {
        this.view = view;
    }

    @Override
    public void unSubscribe() {
        this.view = null;
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }


    @Override
    public void flush() {

    }

    @Override
    public void stopsFlush() {

    }

    /**
     * 重置滤芯
     * 需要传参数  userTime  和 userFlow ,重置为 0 。 不管是否已经更换滤芯，是否是新滤芯，都会重置滤芯寿命为 100%
     */
    @Override
    public void startResetFilter(FilterEntity filterEntity) {
        Log.i(TAG, "startResetFilter: ");
        // 检查错误并且弹出框
        WaterSerialManager.getInstance().resetFilter(filterEntity);
    }

    /**
     * @param activity
     * @param type     0冲洗  1重置并冲洗
     * @param filters  0,ro滤芯 1,5in1滤芯
     */
    @Override
    public void checkAndFlush(FragmentActivity activity, int type, ArrayList<Integer> filters) {
        // 检查错误并且弹出框
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        Log.i(TAG, "waterOutListener : " + isError);
        if (isError) {
            return;
        }
        String stateId = (String) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.WATER_OUT_STATUS.siid, WaterPropEnum.WATER_OUT_STATUS.piid, "");
        if (stateId.contains(WaterConstant.WATEROUT_PURING)) {
            ViomiToastUtil.showToastCenter(activity.getString(R.string.setting_flush_exception));
            return;
        }
        // 设备故障
        int equipment = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.EQUIPMENT_FAULT.siid, WaterPropEnum.EQUIPMENT_FAULT.piid, 0);
        // 设备没有故障，停止制水
        if (equipment != 0) {

        }
        // 显示冲洗框
        showFlushDialog(activity, type, filters);
        // 判断是否重置滤芯
        if (type != 1) {
            return;
        }
        ArrayList<PropertyEntity> propertyEntities = new ArrayList<>();
        PropertyEntity timeProperty;
        PropertyEntity flowProperty;
        // 清洗滤芯的指令
        if (filters.contains(0)) {
            timeProperty = new PropertyEntity(WaterPropEnum.FILTER_CARBON_USED_TIME.siid, WaterPropEnum.FILTER_CARBON_USED_TIME.piid, 0);
            flowProperty = new PropertyEntity(WaterPropEnum.FILTER_CARBON__USED_FLOW.siid, WaterPropEnum.FILTER_CARBON__USED_FLOW.piid, 0);
            propertyEntities.add(timeProperty);
            propertyEntities.add(flowProperty);

            propertyEntities.clear();
        }
        // 重置滤芯的指令
        if (filters.contains(1)) {
            timeProperty = new PropertyEntity(WaterPropEnum.FILTER_4IN1_USED_TIME.siid, WaterPropEnum.FILTER_4IN1_USED_TIME.piid, 0);
            flowProperty = new PropertyEntity(WaterPropEnum.FILTER_4IN1__USED_FLOW.siid, WaterPropEnum.FILTER_4IN1__USED_FLOW.piid, 0);
            propertyEntities.add(timeProperty);
            propertyEntities.add(flowProperty);

        }
    }

    private void showFlushDialog(FragmentActivity activity, int type, ArrayList<Integer> filters) {
        FlushFragment flushFragment = new FlushFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putIntegerArrayList("filters", filters);
        flushFragment.setArguments(bundle);
        flushFragment.show(activity.getSupportFragmentManager(), "flushFragment");
    }
}
