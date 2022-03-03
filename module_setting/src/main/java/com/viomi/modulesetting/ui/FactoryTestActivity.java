package com.viomi.modulesetting.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.ActivityFactorytestBinding;
import com.viomi.modulesetting.receiver.CommonSetReceiver;
import com.viomi.modulesetting.utils.DeviceUtil;
import com.viomi.modulesetting.utils.SettingToolUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 工厂测试界面，包含 屏幕测试，老化测试 等
 */
@Route(path = ViomiRouterConstant.SETTING_FACTORY_TEST)
public class FactoryTestActivity extends BaseCommonSettingActivity<ActivityFactorytestBinding> {
    public static final String ERROR_FIRM = "-1";
    private static final String TAG = "FactoryTestActivity";
    private int serialTextCount;

    @Override
    public int onBindLayout() {
        return R.layout.activity_factorytest;
    }

    @Override
    public void initView() {
        super.initView();
        String openPropShowTip = getString(R.string.factorytest_open_propshowtip);
        String closePropShowTip = getString(R.string.factorytest_close_propshowtip);
        String propShowTip = CommonConstant.SHOW_DEBUG_INFO ? closePropShowTip : openPropShowTip;
        viewDataBinding.factoryShowProp.setText(propShowTip);
    }

    @Override
    public void initListener() {
        //老化
        viewDataBinding.factoryAgetest.setOnClickListener(v -> {
            String agingRoutPath = ModuleSettingServiceFactory.getInstance().getViotService().getAgingRoutPath();
            Log.i(TAG, "initListener: agingRoutPath: " + agingRoutPath);
            ViomiRouter.getInstance().build(agingRoutPath)
                    .navigation();
        });

        viewDataBinding.factoryChangeenv.setOnClickListener(v -> {
            ViomiRouter.getInstance().build("/app/Test1Activity")
                    .navigation();
        });
        // 系统工厂测试
        viewDataBinding.factorySystemset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceUtil.checkApkExist(FactoryTestActivity.this, ModuleSettingConstants.SYSTEM_FACTORY_PACKAGE)) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(ModuleSettingConstants.SYSTEM_FACTORY_PACKAGE, ModuleSettingConstants.SYSTEM_FACTORY_ACTIVITY);
                    intent.setComponent(cn);
                    startActivity(intent);
                } else {
                    ViomiToastUtil.showToastCenter("不支持");
                }
            }
        });
        // 串口 开关测试
        viewDataBinding.factorySerialtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialTextCount++;
                Log.i("OvenCommonReceiver", "onClick:serialTextCount " + serialTextCount);
                Intent intent = new Intent();
                if (serialTextCount % 2 == 0) {
                    intent.setAction(CommonSetReceiver.ACTION_SERIAL_OPEN);
                } else {
                    intent.setAction(CommonSetReceiver.ACTION_SERIAL_CLOSE);
                }
                sendBroadcast(intent);
            }
        });
        // 属性开关的显示
        viewDataBinding.factoryShowProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonConstant.SHOW_DEBUG_INFO = !CommonConstant.SHOW_DEBUG_INFO;
                String openPropShowTip = getString(R.string.factorytest_open_propshowtip);
                String closePropShowTip = getString(R.string.factorytest_close_propshowtip);
                String propShowTip = CommonConstant.SHOW_DEBUG_INFO ? closePropShowTip : openPropShowTip;
                viewDataBinding.factoryShowProp.setText(propShowTip);
            }
        });
    }

    @Override
    public void initData() {
        viewDataBinding.factoryModel.setText("Model：" + ModelUtil.getModelName());
        viewDataBinding.facotryPid.setText("PID码：" + ModelUtil.getViomiPid());
        String firmVersion = (String) PropertyPreferenceManager.getInstance().getProperty(CommonConstant.SERIAL_VERSION_SIID, CommonConstant.SERIAL_VERSION_PIID, ERROR_FIRM);
        viewDataBinding.factoryMuc.setText("MCU版本：V" + firmVersion);
        viewDataBinding.factoryAppversion.setText("App版本：V" + SettingToolUtil.getVersion(this));
    }

    @Override
    public void registerModuleEvent() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume time = " + System.currentTimeMillis());
    }
}
