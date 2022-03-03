package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentDatePickBinding;
import com.viomi.modulesetting.utils.SettingToolUtil;
import com.viomi.modulesetting.view.wheelview.WheelView;
import com.viomi.modulesetting.view.wheelview.adapter.ArrayWheelAdapter;
import com.viomi.ovensocommon.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 时间日期  日期设置
 */
public class DatePickFragment extends BaseDialogFragment<FragmentDatePickBinding> {
    private static final String TAG = "DatePickFragment";
    private Calendar settingCalendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private final Handler mHandler = new Handler();

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_date_pick;
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.dateCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        viewDataBinding.dateSure.setOnClickListener(v -> {
            Log.i(TAG, "initListener:dateSure ");
            dismissAllowingStateLoss();
            if (listener != null) {
                listener.sureButtonClick(settingCalendar);
            }
        });

        viewDataBinding.timeSettingZone.setOnWheelItemSelectedListener((position, year) -> {
            Log.i(TAG, "initListener:timeSettingZone ");
            if (year != null) {
                mYear = (int) year;
            }
            viewDataBinding.timeSettingMinute.setWheelData(calculateDay(mYear, mMonth));
            settingCalendar.set(Calendar.YEAR, mYear);
        });

        viewDataBinding.timeSettingHour.setOnWheelItemSelectedListener((position, month) -> {
            Log.i(TAG, "initListener: timeSettingHour");
            if (month != null) {
                mMonth = (int) month;
            }
            viewDataBinding.timeSettingMinute.setWheelData(calculateDay(mYear, mMonth));
            settingCalendar.set(Calendar.MONTH, mMonth - 1);
        });

        viewDataBinding.timeSettingMinute.setOnWheelItemSelectedListener((position, day) -> {
            Log.i(TAG, "initListener: timeSettingMinute");
            if (day != null) {
                mDay = (int) day;
            }
            settingCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: ");
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        if (getActivity() == null) return;
        settingCalendar = Calendar.getInstance();
        mYear = settingCalendar.get(Calendar.YEAR);
        mMonth = settingCalendar.get(Calendar.MONTH) + 1;
        mDay = settingCalendar.get(Calendar.DAY_OF_MONTH);

        List<Integer> yearList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();
        List<Integer> dayList;
        for (int i = 2007; i < 2038; i++) yearList.add(i);
        for (int i = 1; i < 13; i++) monthList.add(i);
        dayList = calculateDay(mYear, mMonth);

        WheelView.WheelViewStyle wheelStyle = new WheelView.WheelViewStyle();
        wheelStyle.selectedTextColor = getResources().getColor(R.color.color_green_light);
        wheelStyle.textColor = getResources().getColor(R.color.color_68);
        wheelStyle.textSize = 36;
        wheelStyle.selectedTextSize = 42;
        wheelStyle.itemPadding = 0;
        wheelStyle.itemMargin = 0;
        wheelStyle.itemHeight = 65;
        wheelStyle.holoBorderColor = getResources().getColor(R.color.color_line_devider);
        wheelStyle.holoBorderWidth = 1;
        wheelStyle.backgroundColor = getResources().getColor(R.color.color_dialog_bg);

        viewDataBinding.timeSettingZone.setWheelData(yearList);
        viewDataBinding.timeSettingZone.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeSettingZone.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingZone.setLoop(false);
        viewDataBinding.timeSettingZone.setWheelSize(5);
        viewDataBinding.timeSettingZone.setStyle(wheelStyle);
        viewDataBinding.timeSettingZone.setExtraText(getResources().getString(R.string.year), 0xFF28BECA, SettingToolUtil.dpToPx(getContext(), 21), SettingToolUtil.dpToPx(getContext(), 65));

        viewDataBinding.timeSettingHour.setWheelData(monthList);
        viewDataBinding.timeSettingHour.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeSettingHour.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingHour.setLoop(false);
        viewDataBinding.timeSettingHour.setWheelSize(5);
        viewDataBinding.timeSettingHour.setStyle(wheelStyle);
        viewDataBinding.timeSettingHour.setExtraText(getResources().getString(R.string.month), 0xFF28BECA, SettingToolUtil.dpToPx(getContext(), 21), SettingToolUtil.dpToPx(getContext(), 30));

        viewDataBinding.timeSettingMinute.setWheelData(dayList);
        viewDataBinding.timeSettingMinute.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeSettingMinute.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingMinute.setLoop(false);
        viewDataBinding.timeSettingMinute.setWheelSize(5);
        viewDataBinding.timeSettingMinute.setStyle(wheelStyle);
        viewDataBinding.timeSettingMinute.setExtraText(getResources().getString(R.string.day), 0xFF28BECA, SettingToolUtil.dpToPx(getContext(), 21), SettingToolUtil.dpToPx(getContext(), 45));

        mHandler.postDelayed(() -> {
            viewDataBinding.timeSettingZone.setSelection(yearList.indexOf(mYear));
            viewDataBinding.timeSettingHour.setSelection(monthList.indexOf(mMonth));
            viewDataBinding.timeSettingMinute.setSelection(dayList.indexOf(mDay));
        }, 200);

    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                if (ScreenUtils.isLandscape()) {
                    wlp.width = 852;
                } else {
                    wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                }
                wlp.height = 430;
                window.setAttributes(wlp);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }

    private List<Integer> calculateDay(int selectYear, int selectMonth) {
        List<Integer> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectYear);
        calendar.set(Calendar.MONTH, selectMonth - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDate = calendar.get(Calendar.DATE);
        for (int i = 1; i <= maxDate; i++) list.add(i);
        return list;
    }

}
