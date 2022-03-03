package com.viomi.modulesetting.ui;

import android.os.Bundle;
import android.util.Log;

import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.ActivitySettingContainerBinding;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.utils.FragmentUtils;
import com.viomi.router.annotation.Route;

@Route(path = ViomiRouterConstant.SETTING_CONTAINER)
public class SettingContainerActivity extends BaseCommonSettingActivity<ActivitySettingContainerBinding> {
    private static final String TAG = "SettingContainerActivity";

    @Override
    public int onBindLayout() {
        return R.layout.activity_setting_container;
    }

    @Override
    public void initView() {
        super.initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        Bundle extrasBundle = getIntent().getExtras();
        String targetFragmentRouter = ViomiRouterConstant.SETTING_FRAGMENT_ABOUT;
        if (extrasBundle != null) {
            targetFragmentRouter = extrasBundle.getString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER);
        }
        FragmentUtils.loadFragment(R.id.container_fragment,targetFragmentRouter);
    }

    @Override
    public void registerModuleEvent() {
    }
}
