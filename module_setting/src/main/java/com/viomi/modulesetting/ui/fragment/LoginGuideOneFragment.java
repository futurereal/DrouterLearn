package com.viomi.modulesetting.ui.fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentLoginguideOneBinding;
import com.viomi.ovensocommon.BaseDialogFragment;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Description: 登录引导云米商城  下载二维码页面
 */
public class LoginGuideOneFragment extends BaseDialogFragment<FragmentLoginguideOneBinding> {
    private static final String TAG = "LoginGuideOneFragment";

    @Override
    protected int getLayoutRes() {
        Log.i(TAG, "getLayoutRes: ");
        return R.layout.fragment_loginguide_one;
    }

    @Override
    public void onStart() {
        // 宽和高 和  图片的宽高 一致
        Log.i(TAG, "onStart: landWidth: " + landWidth + "  landHeigth: " + landHeight);
        super.onStart();
        if (ScreenUtils.isPortrait()) {
            WindowManager.LayoutParams layoutParameter = window.getAttributes();
            layoutParameter.gravity = Gravity.BOTTOM;
            layoutParameter.width = ScreenUtils.getScreenWidth() - 20;
            layoutParameter.height = ScreenUtils.getScreenWidth();
            window.setAttributes(layoutParameter);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

}
