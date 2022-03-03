package com.viomi.modulesetting.ui.fragment.dialog;

import static android.content.Context.WIFI_SERVICE;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.Utils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentWlanForgetpwdBinding;
import com.viomi.modulesetting.utils.wifi.Wifi;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.CommonStringUtils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.ui.fragment.dialog
 * @ClassName: ForgetWlanPwdDialogFragment
 * @Description: 忽略已保存密码网络对话框
 * @Author: randysu
 * @CreateDate: 2020/2/28 3:48 PM
 * @UpdateUser:
 * @UpdateDate: 2020/2/28 3:48 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class WlanPwdForgetFragment extends BaseDialogFragment<FragmentWlanForgetpwdBinding> {
    private ScanResult scanResult;
    private WifiManager mWifiManager;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wlan_forgetpwd;
    }

    @Override
    protected void initView() {
        mWifiManager = (WifiManager) Utils.getApp().getApplicationContext().getSystemService(WIFI_SERVICE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            scanResult = bundle.getParcelable("ScanResult");
            String noSaveTip = getString(R.string.setting_no_save);
            String passordTip = getString(R.string.setting_password);
            String ssidName = scanResult.SSID;
            ssidName = CommonStringUtils.getFixCharString(ssidName, CommonStringUtils.MAX_SSID_LENGTH);
            viewDataBinding.logoutTips.setText(noSaveTip + ssidName + passordTip);
        }
    }


    @Override
    protected void initListener() {
        viewDataBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        viewDataBinding.ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(scanResult);
                WifiConfiguration configuration = Wifi.getWifiConfiguration(mWifiManager, scanResult, scanResultSecurity);
                if (configuration != null) {
                    mWifiManager.removeNetwork(configuration.networkId);
                    mWifiManager.saveConfiguration();
                }
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_IGNORE_WIFI, scanResult);
                dismissAllowingStateLoss();
            }
        });
    }




}
