package com.viomi.modulesetting.ui.fragment.dialog;

import static android.content.Context.WIFI_SERVICE;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentWlanInputpwdBinding;
import com.viomi.modulesetting.utils.wifi.Wifi;
import com.viomi.modulesetting.utils.wifi.WifiSupport;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.utils.CommonStringUtils;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: wifi输入密码对话框
 */
public class WlanPwdInputFragment extends BaseDialogFragment<FragmentWlanInputpwdBinding> {
    private static final String TAG = "WlanPwdInputFragment";
    public static final String KEY_SCANRESULT = "keyScanResult";
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final float ALPHA_CLICKABLE = 1f;
    public static final float ALPHA_UNCLICKABLE = 0.3f;
    private ScanResult scanResult;
    private WifiManager mWifiManger;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wlan_inputpwd;
    }

    @Override
    protected void initListener() {
        viewDataBinding.wifiPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence inputChar, int start, int before, int count) {
                if (!TextUtils.isEmpty(inputChar) && inputChar.length() >= PASSWORD_MIN_LENGTH) {
                    viewDataBinding.joinBtn.setAlpha(ALPHA_CLICKABLE);
                    viewDataBinding.joinBtn.setClickable(true);
                } else {
                    viewDataBinding.joinBtn.setAlpha(ALPHA_UNCLICKABLE);
                    viewDataBinding.joinBtn.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewDataBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        viewDataBinding.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiManger == null) {
                    mWifiManger = (WifiManager) ApplicationUtils.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
                }
                String scanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(scanResult);
                WifiConfiguration configuration = Wifi.getWifiConfiguration(mWifiManger, scanResult, scanResultSecurity);
                Log.i(TAG, "onClick: joint " + configuration);
                if (configuration == null) {
                    String inputPwd = viewDataBinding.wifiPwdEt.getText().toString().trim();
                    configuration = WifiSupport.createWifiConfiguration(scanResult, inputPwd);
                }
                WifiSupport.addNetWork(configuration, getContext());
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_INPUT_WIFI_PW, scanResult);
                dismissAllowingStateLoss();
            }
        });
    }


    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        scanResult = bundle.getParcelable(KEY_SCANRESULT);
        String ssidName = scanResult.SSID;
        ssidName = CommonStringUtils.getFixCharString(ssidName, CommonStringUtils.MAX_SSID_LENGTH);
        String inputTip = getResources().getString(R.string.setting_input);
        String passwordTip = getResources().getString(R.string.setting_password);
        viewDataBinding.wifiNameTitle.setText(inputTip + ssidName + passwordTip);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
    }

}
