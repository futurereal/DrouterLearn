package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentTimeSelectBinding;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.modulesetting.view.wheelview.WheelView;
import com.viomi.modulesetting.view.wheelview.adapter.ArrayWheelAdapter;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 息屏时间 和锁屏时间的设置
 */
public class TimeSelectFragment extends BaseDialogFragment<FragmentTimeSelectBinding> {
    public static final String KEY_STANDTIME_NAME = "keyStandByName";
    public static final String KEY_STANDTYPE = "keyStandType";

    public static final int STANDTYPE_SCREEN_OFF = 1;
    public static final int STANDTYPE_CHILD_LOCK = 2;

    private static final String TAG = "TimeSelectFragment";
    private List<String> standByNameList;
    private List<Integer> standByTimeList;
    private int currentPosition;
    private final Handler mHandler = new Handler();
    private int selectPosition;
    private int currentStandByType;
    private String currentStandByName;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_time_select;
    }

    @Override
    protected void initView() {
        int screenOffArrayId = ModuleSettingServiceFactory.getInstance().getViotService().getScreenOffArrayId();
        Log.i(TAG, "initScreenSleep: screenOffArrayId: " + screenOffArrayId);
        int[] screenOffArray = getResources().getIntArray(screenOffArrayId);
        standByNameList = new ArrayList<String>();
        standByTimeList = new ArrayList<Integer>();
        Bundle bundle = getArguments();
        currentStandByType = bundle.getInt(KEY_STANDTYPE);
        currentStandByName = bundle.getString(KEY_STANDTIME_NAME);
        for (int screenOffTime : screenOffArray) {
            standByTimeList.add(screenOffTime);
            standByNameList.add(ModuleSettingUtil.getScreenOffTimeName(screenOffTime));
        }
        currentPosition = standByNameList.indexOf(currentStandByName);
        Log.i(TAG, "initChildView: " + currentPosition);
        viewDataBinding.timeselectTitle.setText(currentStandByName);
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

        viewDataBinding.timeselectContainer.setWheelData(standByNameList);
        viewDataBinding.timeselectContainer.setWheelAdapter(new ArrayWheelAdapter<>(getActivity()));
        viewDataBinding.timeselectContainer.setSkin(WheelView.Skin.Holo);
        viewDataBinding.timeselectContainer.setLoop(false);
        viewDataBinding.timeselectContainer.setWheelSize(5);
        viewDataBinding.timeselectContainer.setStyle(wheelStyle);

        viewDataBinding.timeselectContainer.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int position, String name) {
                Log.i(TAG, "position: " + position + "   name: " + name);
                selectPosition = position;
            }
        });
        mHandler.postDelayed(() -> {
            viewDataBinding.timeselectContainer.setSelection(currentPosition);
        }, 200);


    }

    @Override
    protected void initListener() {
        viewDataBinding.timeselectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        viewDataBinding.timeselectSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentStandByName = standByNameList.get(selectPosition);
                int msgType = 0;
                if (currentStandByType == STANDTYPE_SCREEN_OFF) {
                    msgType = ModuleSetingEventConstant.MSG_UPDATE_STANDBYTIME;
                } else if (currentStandByType == STANDTYPE_CHILD_LOCK) {
                    msgType = ModuleSetingEventConstant.MSG_UPDATE_LOCKTIME;
                }
                ViomiRxBus.getInstance().post(msgType, currentStandByName);
                dismissAllowingStateLoss();
            }
        });

        Disposable standByDisposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            switch (msgId) {
                case ModuleSetingEventConstant.MSG_SCREENOFF_TIME:
                    Log.i(TAG, "initListener:" + viomiRxBusEvent);
                    currentStandByName = (String) msgObj;
                    currentPosition = standByNameList.indexOf(currentStandByName);
                    viewDataBinding.timeselectTitle.setText(currentStandByName);
                    viewDataBinding.timeselectContainer.setSelection(currentPosition);
                    break;
            }
        });
        addDispose(standByDisposable);
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
