package com.viomi.modulesetting.ui.fragment.dialog;

import static android.content.Context.WIFI_SERVICE;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.Utils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentWlanErrorpwdBinding;
import com.viomi.modulesetting.utils.wifi.Wifi;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.CommonStringUtils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.ui.fragment.dialog
 * @ClassName: WlanPwdErrorFragment
 * @Description: 密码错误对话框
 * @Author: randysu
 * @CreateDate: 2020/2/28 5:28 PM
 * @UpdateUser:
 * @UpdateDate: 2020/2/28 5:28 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class WlanPwdErrorFragment extends BaseDialogFragment<FragmentWlanErrorpwdBinding> {
    private static final String TAG = "WlanPwdErrorFragment";
    private ScanResult scanResult;
    private WifiManager mWifiManager;
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wlan_errorpwd;
    }

    @Override
    protected void initView() {

        mWifiManager = (WifiManager) Utils.getApp().getApplicationContext().getSystemService(WIFI_SERVICE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            scanResult = bundle.getParcelable("ScanResult");
            String ssidName = scanResult.SSID;
            ssidName = CommonStringUtils.getFixCharString(ssidName, CommonStringUtils.MAX_SSID_LENGTH);
            viewDataBinding.logoutTips.setText("网络“" + ssidName + "”的密码错误");
        }
    }

    @Override
    protected void initListener() {
        viewDataBinding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.sureButtonClick(null);
                }
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_IGNORE_WIFI, scanResult);
                dismissAllowingStateLoss();
                String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(scanResult);
                WifiConfiguration configuration = Wifi.getWifiConfiguration(mWifiManager, scanResult, scanResultSecurity);
                Log.i(TAG, "onClick: "+configuration);
                if (configuration != null) {
                    mWifiManager.removeNetwork(configuration.networkId);
                    mWifiManager.saveConfiguration();
                }
            }
        });
    }




}
