package com.viomi.modulesetting.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentLoginguideTwoBinding;
import com.viomi.ovensocommon.BaseDialogFragment;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Package: com.viomi.settingpagelib.ui.fragment
 * @ClassName: LoginGuideOneFragment
 * @Description: 登录引导云米商城下载二维码页面
 * @Author: randysu
 * @CreateDate: 2020/4/7 3:55 PM
 * @UpdateUser:
 * @UpdateDate: 2020/4/7 3:55 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class LoginGuildeTwoFragment extends BaseDialogFragment<FragmentLoginguideTwoBinding> {
    private static final String TAG = "LoginGuildeTwoFragment";
    private static final String KEY_RESOURCE_ID = "keyResourceId";

    public static LoginGuildeTwoFragment instance(int imageRes) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RESOURCE_ID, imageRes);
        LoginGuildeTwoFragment guildeTwoFragment = new LoginGuildeTwoFragment();
        guildeTwoFragment.setArguments(bundle);
        return guildeTwoFragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_loginguide_two;
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        int resourceId = bundle.getInt(KEY_RESOURCE_ID, 0);
        Log.i(TAG, "initChildView: resourceId: " + resourceId);
        viewDataBinding.loginguildeTwoTip.setImageResource(resourceId);
    }

    @Override
    public void onStart() {
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
    protected void initListener() {

    }


}
