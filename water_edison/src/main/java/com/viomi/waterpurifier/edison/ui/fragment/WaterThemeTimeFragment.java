package com.viomi.waterpurifier.edison.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.view.wheelview.WheelView;
import com.viomi.ovensocommon.view.wheelview.adapter.ArrayWheelAdapter;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterPreference;
import com.viomi.waterpurifier.edison.databinding.FragmentWaterThemetimeBinding;
import com.viomi.waterpurifier.edison.util.TimeUtil;

import java.util.List;


public class WaterThemeTimeFragment extends BaseDialogFragment<FragmentWaterThemetimeBinding> {
    private static final String TAG = "WaterThemeTimeFragment";
    private int currentIndex;

    public WaterThemeTimeFragment(int currentTimeIndex) {
        super();
        this.currentIndex = currentTimeIndex;
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_water_themetime;
    }


    @Override
    protected void initView() {
        WheelView.WheelViewStyle wheelStyle = getViewStyle();
        List<String> dateList = TimeUtil.getAllChangeDate();
        viewDataBinding.themedateWheelview.setWheelData(dateList);
        viewDataBinding.themedateWheelview.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.themedateWheelview.setSkin(WheelView.Skin.Holo);
        viewDataBinding.themedateWheelview.setLoop(true);
        viewDataBinding.themedateWheelview.setWheelSize(7);
        viewDataBinding.themedateWheelview.setStyle(wheelStyle);
        Log.i(TAG, "initChildView: currentIndex： " + currentIndex);
        viewDataBinding.themedateWheelview.setSelection(currentIndex);
    }

    @Override
    protected void initListener() {
        viewDataBinding.themedateSure.setOnClickListener(v -> {
            WaterPreference.getInstance().setWaterProperty(WaterPreference.KEY_THEME_CHANGETIME, currentIndex);
            Log.i(TAG, "onClick: sure: " + currentIndex);
            dismissAllowingStateLoss();
            ViomiRxBus.getInstance().post(WaterBusEvent.MSG_THEME_TIME_CHANGE, currentIndex);
        });

        viewDataBinding.themedateCancel.setOnClickListener(v -> dismissAllowingStateLoss());

        viewDataBinding.themedateWheelview.setOnWheelItemSelectedListener((WheelView.OnWheelItemSelectedListener<String>) (position, content) -> {
            Log.i(TAG, "onItemSelected: position: " + position + " content: " + content);
            currentIndex = position;
        });

    }

    private WheelView.WheelViewStyle getViewStyle() {
        WheelView.WheelViewStyle wheelStyle = new WheelView.WheelViewStyle();
        wheelStyle.selectedTextColor = getResources().getColor(R.color.color_green_light);
        wheelStyle.textColor = getResources().getColor(R.color.color_68);
        wheelStyle.textSize = 34;
        wheelStyle.selectedTextSize = 34;
        wheelStyle.itemPadding = 0;
        wheelStyle.itemMargin = 0;
        wheelStyle.itemHeight = 76;
        wheelStyle.holoBorderColor = getResources().getColor(R.color.color_line_devider);
        wheelStyle.holoBorderWidth = 1;
        wheelStyle.backgroundColor = getResources().getColor(R.color.color_dialog_bg);
        return wheelStyle;
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isLandScreen = ScreenUtils.isLandscape();
        WindowManager.LayoutParams layoutParmater = window.getAttributes();
        if (isLandScreen) {
            layoutParmater.width = layoutParmater.height;
        } else {
            layoutParmater.width = 720;
            layoutParmater.height = 700;
            layoutParmater.gravity = Gravity.BOTTOM;
        }
        Log.i(TAG, "onStart: width: " + layoutParmater.width + "  height : " + layoutParmater.height);
        window.setAttributes(layoutParmater);
    }

    public Bundle makeBundle(int currentTimeIndex) {
        return null;
    }
}
