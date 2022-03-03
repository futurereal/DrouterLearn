package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintSet;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentTimePickBinding;
import com.viomi.modulesetting.utils.SettingToolUtil;
import com.viomi.modulesetting.view.wheelview.WheelView;
import com.viomi.modulesetting.view.wheelview.adapter.ArrayWheelAdapter;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.utils.TimeFormateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.ui.fragment.dialog
 * @ClassName: DatePickupDialogFragment
 * @Description: 时间日期  时间设置
 * @Author: randysu
 * @CreateDate: 2020/3/17 3:51 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/17 3:51 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class TimePickFragment extends BaseDialogFragment<FragmentTimePickBinding> {
    private static final String TAG = "TimePickFragment";
    private Calendar calendar;
    private final Handler mHandler = new Handler();
    private boolean isTwentyFourHour;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_time_pick;
    }


    @Override
    protected void initView() {
        if (getActivity() == null) return;
        calendar = Calendar.getInstance();
        isTwentyFourHour = TimeFormateUtil.isTwentyFourFormate();
        Log.i(TAG, "initChildView: isTwentyFourHour " + isTwentyFourHour);
        int currentHour = isTwentyFourHour ? calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        // 是否是 24小时制度
        if (isTwentyFourHour) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(viewDataBinding.timeSettingLayout);
            constraintSet.setHorizontalWeight(R.id.time_setting_zone, 0);
            constraintSet.applyTo(viewDataBinding.timeSettingLayout);
        }

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

        List<String> zoneList = new ArrayList<>();
        zoneList.add(getResources().getString(R.string.morning));
        zoneList.add(getResources().getString(R.string.afternoon));
        viewDataBinding.timeSettingZone.setWheelData(zoneList);

        viewDataBinding.timeSettingZone.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeSettingZone.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingZone.setLoop(false);
        viewDataBinding.timeSettingZone.setWheelSize(5);
        viewDataBinding.timeSettingZone.setStyle(wheelStyle);

        List<String> hourList = new ArrayList<>();
        if (isTwentyFourHour) {
            for (int i = 0; i < 24; i++) hourList.add(i < 10 ? "0" + i : String.valueOf(i));
        } else {
            for (int i = 1; i <= 12; i++) hourList.add(String.valueOf(i));
        }
        viewDataBinding.timeSettingHour.setWheelData(hourList);
        viewDataBinding.timeSettingHour.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeSettingHour.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingHour.setLoop(false);
        viewDataBinding.timeSettingHour.setWheelSize(5);
        viewDataBinding.timeSettingHour.setStyle(wheelStyle);
        viewDataBinding.timeSettingHour.setExtraText(getResources().getString(R.string.hour), 0xFF28BECA, SettingToolUtil.dpToPx(getContext(), 21), SettingToolUtil.dpToPx(getContext(), 65));

        List<String> minuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) minuteList.add(i < 10 ? "0" + i : String.valueOf(i));
        viewDataBinding.timeSettingMinute.setWheelData(minuteList);
        viewDataBinding.timeSettingMinute.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));

        viewDataBinding.timeSettingMinute.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeSettingMinute.setLoop(false);
        viewDataBinding.timeSettingMinute.setWheelSize(5);
        viewDataBinding.timeSettingMinute.setStyle(wheelStyle);
        viewDataBinding.timeSettingMinute.setExtraText(getResources().getString(R.string.minute), 0xFF28BECA, SettingToolUtil.dpToPx(getContext(), 21), SettingToolUtil.dpToPx(getContext(), 65));

        mHandler.postDelayed(() -> {
            viewDataBinding.timeSettingZone.setSelection(calendar.get(Calendar.AM_PM) == Calendar.AM ? 0 : 1);
            viewDataBinding.timeSettingHour.setSelection(isTwentyFourHour ? currentHour :
                    (currentHour - 1 < 0 ? hourList.size() - 1 : currentHour - 1));
            viewDataBinding.timeSettingMinute.setSelection(currentMinutes);
        }, 200);

    }

    @Override
    protected void initListener() {
        viewDataBinding.dateCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        viewDataBinding.dateSure.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (listener != null) {
                listener.sureButtonClick(calendar);
            }
        });

        viewDataBinding.timeSettingZone.setOnWheelItemSelectedListener((position, s) ->
                calendar.set(Calendar.AM_PM, position == 0 ? Calendar.AM : Calendar.PM));

        viewDataBinding.timeSettingHour.setOnWheelItemSelectedListener((position, hour) -> {
            if (!TextUtils.isEmpty((CharSequence) hour)) {
                int hourType = isTwentyFourHour ? Calendar.HOUR_OF_DAY : Calendar.HOUR;
                int currentHour = Integer.parseInt(hour.toString());
                currentHour = !isTwentyFourHour && currentHour == 12 ? 0 : currentHour;
                calendar.set(hourType, currentHour);
            }
        });
        viewDataBinding.timeSettingMinute.setOnWheelItemSelectedListener((position, minute) -> {
            if (!TextUtils.isEmpty((CharSequence) minute)) {
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute.toString()));
            }
        });
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

}
