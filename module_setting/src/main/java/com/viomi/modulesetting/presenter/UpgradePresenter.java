package com.viomi.modulesetting.presenter;

import android.content.Context;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.contract.UpgradeContract;
import com.viomi.modulesetting.utils.SettingPreference;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.upgrade_lib.UpdateConst;
import com.viomi.upgrade_lib.api.VUpgradeApi;
import com.viomi.upgrade_lib.entity.CheckVersionResult;
import com.viomi.upgrade_lib.entity.DeviceInfo;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 版本更新 Presenter
 */
public class UpgradePresenter implements UpgradeContract.Presenter {
    private static final String TAG = "UpgradePresenter";
    private static final int CHANAL_VIOT = 0; // 1 代表小米
    private static final int FIRM_TYPE_LAUNCHER = 3; // 桌面升级
    private final Context mContext;
    private UpgradeContract.View mView;

    public UpgradePresenter() {
        mContext = ApplicationUtils.getContext();
        initUpgradeSdk();
    }

    @Override
    public void checkAppUpgrade() {
        Log.i(TAG, "checkAppUpdate: ");
        VUpgradeApi.Companion.getInstance().checkVersion(mContext, checkVersionResult -> {
            Log.i(TAG, "checkAppUpdate:  " + checkVersionResult);
            SettingPreference.getInstance().setValue(SettingPreference.UPDATE_DESC, checkVersionResult.getDescription());
            if (mView != null) {
                mView.updateVersion(checkVersionResult);
            }
            return null;
        });
    }

    private void initUpgradeSdk() {
        // 为了获取数据里面的值
        Log.i(TAG, "initUpgradeSdk: ");
        String deviceId = ViomiProvideUtil.getDeviceId();
        String macAddrss = ViomiProvideUtil.getMac();
        String modelName = ModelUtil.getModelName();
        String apkName = ModelUtil.getApkName();
        Log.i(TAG, "initUpgradeSdk: modelName: " + modelName + " apkName: " + apkName);
        DeviceInfo deviceInfo = new DeviceInfo(deviceId, CHANAL_VIOT, modelName, macAddrss, apkName, FIRM_TYPE_LAUNCHER);
        String userId = ModuleSettingApplicaiton.getUserInfoDb().getUserId();
        Log.i(TAG, "initUpgradeSdk: userId: " + userId);
        VUpgradeApi.Companion.getInstance().init(
                ApplicationUtils.getContext(), deviceInfo,
                () -> userId,
                () -> {
                    // 黑名单暂时没有
                    return true;
                }, aBoolean -> {
                    return null;
                });
    }

    @Override
    public void downloadApp(CheckVersionResult result) {
        Log.i(TAG, "downloadApp: result: " + result);
        VUpgradeApi.Companion.getInstance().downloadApk(mContext, result.getUrl(), result.getUpgradeType(), result.getRecordId(), downloadResult -> {
            if (mView == null) {
                Log.i(TAG, "downloadApp: mVIew is null return ");
                return null;
            }
            switch (downloadResult.getCode()) {
                case UpdateConst.CODE_DOWNLOADING:
                    mView.refreshProgress(downloadResult.getProgress());
                    break;
                case UpdateConst.CODE_PARSE_FAIL:
                case UpdateConst.CODE_NO_NEED_UPDATE:
                    mView.install(null);
                    break;
                case UpdateConst.CODE_SUCCESS:
                    mView.install("");
                    break;
            }
            return null;
        });
    }

    @Override
    public void downloadAppFull(String url) {
        Log.i(TAG, "downloadAppFull: url : " + url);
        VUpgradeApi.Companion.getInstance().downloadApk2(mContext, url, integer -> {
            Log.i(TAG, "invoke: integer : " + integer);
            if (integer == -1) {
                if (mView != null) {
                    mView.install(null);
                }
            } else if (integer == 100) {
                if (mView != null) {
                    mView.install("");
                }
            } else {
                if (mView != null) {
                    mView.refreshProgress(integer);
                }
            }
            return null;
        });
    }

    @Override
    public void subscribe(UpgradeContract.View view) {
        mView = view;
    }

    @Override
    public void unSubscribe() {
        mView = null;
    }
}
