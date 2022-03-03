package com.viomi.modulesetting.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentAboutSofewareBinding;
import com.viomi.modulesetting.entity.AboutSofewareEntity;
import com.viomi.modulesetting.ui.adapter.AboutSoftwareAdapter;
import com.viomi.modulesetting.ui.fragment.dialog.FirmUpgradeFragment;
import com.viomi.modulesetting.ui.fragment.dialog.RebootFragment;
import com.viomi.modulesetting.ui.fragment.dialog.ResetSystemFragment;
import com.viomi.modulesetting.ui.fragment.dialog.UpgradeFragment;
import com.viomi.modulesetting.utils.DeviceUtil;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 关于软件fragment
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_ABOUT)
public class AboutSoftwareFragment extends BindingBaseFragment<FragmentAboutSofewareBinding> {
    private static final String TAG = "AboutSoftwareFragment";
    public static final String VERSION_SOFT = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_sofeversion);
    public static final String VERSION_FIRM = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_firmversion);
    public static final String VERSION_SYSTEM = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_systemversion);
    public static final String NET_NAME = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_netname);
    public static final String RESET_SYSTEM = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_resetsystem);
    public static final String REBOOT = ApplicationUtils.getContext().getResources().getString(R.string.reset_system_title);
    public static final String UPLOAD_LOG = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_uploadlog);
    public static final String SYSTEM_SETTING = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_systemsetting);
    public static final String FACTORY_TEST = ApplicationUtils.getContext().getResources().getString(R.string.aboutsofe_menu_factorytest);

    private static final int MIN_CLICKTIME = 6;
    public static final String ERROR_FIRM = "-1";
    private int systemVersionClicked = 0;
    private ArrayList<AboutSofewareEntity> aboutWareEntities;
    private AboutSoftwareAdapter aboutSoftwareAdapter;
    private AboutSofewareEntity firmUpdateEntity;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_about_sofeware;
    }

    @Override
    protected void initView() {
        aboutSoftwareAdapter = new AboutSoftwareAdapter(aboutWareEntities);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewDataBinding.aboutwareList.setLayoutManager(linearLayoutManager);
        viewDataBinding.aboutwareList.setAdapter(aboutSoftwareAdapter);
        initListener();
    }

    @Override
    public void initListener() {
        Log.i(TAG, "initListener: ");
        aboutSoftwareAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dealItemClick(position);
            }
        });
        registerModuleEvent();
    }

    /**
     * 处理点击事件
     *
     * @param position
     */
    private void dealItemClick(int position) {
        String title = aboutWareEntities.get(position).getTitle();
        Log.i(TAG, "dealItemClick: title: " + title);
        if (TextUtils.equals(title, VERSION_SOFT)) {
            if (ModuleSettingUtil.checkNetAndShowTip()) {
                return;
            }
            UpgradeFragment updateDialog = new UpgradeFragment();
            updateDialog.setArguments(new Bundle());
            updateDialog.show(getActivity().getSupportFragmentManager(), "UpdateDialogFragment");
        } else if (TextUtils.equals(title, VERSION_FIRM)) {
            if (ModuleSettingUtil.checkNetAndShowTip()) {
                return;
            }
            FirmUpgradeFragment updateDialog = new FirmUpgradeFragment();
            String versionName = aboutWareEntities.get(position).getContent();
            updateDialog.setArguments(FirmUpgradeFragment.makeBundle(versionName));
            updateDialog.show(getActivity().getSupportFragmentManager(), "FirmUpdateDialogFragment");
        } else if (TextUtils.equals(title, VERSION_SYSTEM)) {
            // 判断是否预置了OTA升级的apk
            if (DeviceUtil.checkApkExist(getActivity(), ModuleSettingConstants.ROM_UPDATE_PACKAGE)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName(ModuleSettingConstants.ROM_UPDATE_PACKAGE, ModuleSettingConstants.ROM_UPDATE_ACTIVITY);
                intent.setComponent(cn);
                startActivity(intent);
            } else {
                ViomiToastUtil.showToastCenter("未内置升级应用");
            }
        } else if (TextUtils.equals(title, NET_NAME)) {
            systemVersionClicked++;
            if (systemVersionClicked >= MIN_CLICKTIME) {
                systemVersionClicked = 0;
            }

        } else if (TextUtils.equals(title, RESET_SYSTEM)) {
            ResetSystemFragment restoreFactoryDialog = new ResetSystemFragment();
            restoreFactoryDialog.show(getActivity().getSupportFragmentManager(), "ResetSystemFragment");
        } else if (TextUtils.equals(title, REBOOT)) {
            RebootFragment rebootFragment = new RebootFragment();
            rebootFragment.show(getActivity().getSupportFragmentManager(), "RebootFragment");

        } else if (TextUtils.equals(title, UPLOAD_LOG)) {
            // 启动之前需要再次初始化，否则会闪退
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                    .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_LOG)
                    .navigation();
        } else if (TextUtils.equals(title, SYSTEM_SETTING)) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } else if (TextUtils.equals(title, FACTORY_TEST)) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_FACTORY_TEST)
                    .navigation();
        }
    }

    @Override
    public void initData() {
        Log.i(TAG, "initData: ");
        String[] aboutWareTitles = getResources().getStringArray(R.array.aboutSofewareTitles);
        String[] aboutWareContentDefaults = getResources().getStringArray(R.array.aboutSoftwareContentDefault);
        String[] aboutWareRouts = getResources().getStringArray(R.array.aboutSofewareRout);
        int length = aboutWareTitles.length;
        aboutWareEntities = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            AboutSofewareEntity aboutSofewareEntity = new AboutSofewareEntity();
            String aboutWareTitle = aboutWareTitles[i];
            aboutSofewareEntity.setTitle(aboutWareTitle);
            if (TextUtils.equals(aboutWareTitle, VERSION_SOFT)) {
                aboutSofewareEntity.setContent(getVersionName());
            } else if (TextUtils.equals(aboutWareTitle, VERSION_FIRM)) {
                String firmVersion = (String) PropertyPreferenceManager.getInstance().getProperty(CommonConstant.SERIAL_VERSION_SIID, CommonConstant.SERIAL_VERSION_PIID, ERROR_FIRM);
                firmVersion = firmVersion.replace("\"", "");
                Log.i(TAG, "initData: firmVersion: " + firmVersion);
                if (TextUtils.equals(firmVersion, ERROR_FIRM)) {
                    aboutSofewareEntity.setError(true);
                }
                aboutSofewareEntity.setContent(ModuleSettingConstants.VERSION_TITLE + firmVersion);
                firmUpdateEntity = aboutSofewareEntity;
            } else if (TextUtils.equals(aboutWareTitle, VERSION_SYSTEM)) {
                aboutSofewareEntity.setContent(android.os.Build.DISPLAY);
            } else if (TextUtils.equals(aboutWareTitle, NET_NAME)) {
                aboutSofewareEntity.setContent(getMacAndDid());
                if (!ViomiProvideUtil.isSerialNoRight()) {
                    aboutSofewareEntity.setError(true);
                }
            } else {
                aboutSofewareEntity.setContent(aboutWareContentDefaults[i]);
            }
            aboutSofewareEntity.setRoutPath(aboutWareRouts[i]);
            aboutWareEntities.add(aboutSofewareEntity);
        }
        aboutSoftwareAdapter.updateData(aboutWareEntities);
    }

    private String getVersionName() {
        String versionName = AppUtils.getAppVersionName();
        Log.i(TAG, "getVersionName: " + versionName);
        if (ModuleSettingConstants.IS_DEBUG_ENV) {
            versionName = "V" + versionName + "-debug";
        } else {
            versionName = "V" + versionName;
        }
        return versionName;
    }

    private String getMacAndDid() {
        String mac = ViomiProvideUtil.getMac();
        String did = ViomiProvideUtil.getDeviceId();
        return String.format(getActivity().getResources().getString(R.string.management_did_mac_message), mac, did);
    }

    public void registerModuleEvent() {
        Disposable disposable = ViomiRxBus.getInstance().subscribe(busEvent -> {
            if (busEvent.getMsgId() == ModuleSetingEventConstant.MSG_FIRMUPDATE_SUCCESS) {
                String newVersionStr = (String) busEvent.getMsgObject();
                Log.i(TAG, "accept: newVersion: " + newVersionStr);
                firmUpdateEntity.setContent(newVersionStr);
                aboutSoftwareAdapter.notifyDataSetChanged();
            }
        });
        addDisposable(disposable);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
