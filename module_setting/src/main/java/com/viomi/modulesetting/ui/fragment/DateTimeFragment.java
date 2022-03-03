package com.viomi.modulesetting.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentDataTimeBinding;
import com.viomi.modulesetting.ui.fragment.dialog.DatePickFragment;
import com.viomi.modulesetting.ui.fragment.dialog.TimePickFragment;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.ovensocommon.utils.TimeFormateUtil;
import com.viomi.router.annotation.Route;

import java.util.Calendar;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @author : randysu
 * @Description: 日期与时间
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_DATETIME)
public class DateTimeFragment extends BindingBaseFragment<FragmentDataTimeBinding> {
    private static final String TAG = "DateTimeFragment";

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_data_time;
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
    }

    @Override
    protected void initView() {
        //  要在 initListener 之前 设置， 从而避免首次启动 switch 就有回调，避免多有的逻辑
        boolean isTwentyFourFormat = TimeFormateUtil.isTwentyFourFormate();
        Log.i(TAG, "initView: isTwentyFourFormat: " + isTwentyFourFormat);
        viewDataBinding.datatimeTwentySwitch.setOn(isTwentyFourFormat, false);
        boolean timeAuto = TimeFormateUtil.isTimeAuto();
        Log.i(TAG, "initView: timeAuto: " + timeAuto);
        viewDataBinding.curGroup.setVisibility(timeAuto ? View.GONE : View.VISIBLE);
        viewDataBinding.datatimeAutoSwitch.setOn(timeAuto, false);
    }

    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.datatimeTwentySwitch.setOnSwitchStateChangeListener(isOn -> {
            boolean setResult;
            if (isOn) {
                setResult = TimeFormateUtil.setFormateTwentyFour();
            } else {
                setResult = TimeFormateUtil.setFormateTwelve();
            }
            Log.i(TAG, "initListener: setResult: " + setResult);
            if (!setResult) {
                ViomiToastUtil.showToastCenter(getString(R.string.setting_fail));
            }
        });

        viewDataBinding.datatimeAutoSwitch.setOnSwitchStateChangeListener(isOn -> {
            viewDataBinding.curGroup.setVisibility(isOn ? View.GONE : View.VISIBLE);
            boolean setAutoResult = TimeFormateUtil.setTimeAuto(isOn);
            Log.i(TAG, "initListener: setAutoResult: " + setAutoResult);
        });

        viewDataBinding.datetimeCurrentdate.setOnClickListener(v -> {
            DatePickFragment datePickupDialog = new DatePickFragment();
            datePickupDialog.setOnButtonsClickListener(new BaseDialogFragment.OnButtonsClickListener<Calendar>() {
                @Override
                public void sureButtonClick(Calendar calendar) {
                    setSystemTime(calendar.getTimeInMillis());
                }

                @Override
                public void cancelButtonClick() {
                }
            });
            datePickupDialog.show(getActivity().getSupportFragmentManager(), "DatePickFragment");
        });

        viewDataBinding.datetimeCurrenttime.setOnClickListener(v -> {
            TimePickFragment timePickupDialog = new TimePickFragment();
            timePickupDialog.setOnButtonsClickListener(new BaseDialogFragment.OnButtonsClickListener<Calendar>() {
                @Override
                public void sureButtonClick(Calendar calendar) {
                    setSystemTime(calendar.getTimeInMillis());
                }

                @Override
                public void cancelButtonClick() {
                }
            });
            timePickupDialog.show(getActivity().getSupportFragmentManager(), "TimePickFragment");
        });
    }

    /**
     * 设置系统时间
     *
     * @param time
     */
    private void setSystemTime(long time) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.USER_SET_TIME");
        intent.putExtra("time", time);
        getActivity().sendBroadcast(intent);
    }

}
