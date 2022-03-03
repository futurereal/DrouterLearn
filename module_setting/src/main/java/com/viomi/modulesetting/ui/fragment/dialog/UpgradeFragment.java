package com.viomi.modulesetting.ui.fragment.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.UpgradeContract;
import com.viomi.modulesetting.databinding.FragmentUpgradeBinding;
import com.viomi.modulesetting.presenter.UpgradePresenter;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.upgrade_lib.api.VUpgradeApi;
import com.viomi.upgrade_lib.entity.CheckVersionResult;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 桌面升级
 */
public class UpgradeFragment extends BaseDialogFragment<FragmentUpgradeBinding> implements UpgradeContract.View {
    private static final String TAG = "UpgradeFragment";
    private UpgradeContract.Presenter mPresenter;
    private CheckVersionResult mCheckVersionResult;
    private String currentVersionTips;
    private String versionName;
    private boolean isSureTip;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_upgrade;
    }

    @Override
    protected void initView() {
        versionName = ModuleSettingConstants.VERSION_PREFIX + AppUtils.getAppVersionName();
        Log.i(TAG, "initView: " + versionName);
        currentVersionTips = String.format(getResources().getString(R.string.upgrade_current_tip), versionName);
        viewDataBinding.upgradeTip.setText(currentVersionTips);
        Bundle bundle = getArguments();
        if (bundle == null) {
            mPresenter.checkAppUpgrade();
            return;
        }
        boolean forceUpgrade = bundle.getBoolean("forceUpgrade", false);
        String url = bundle.getString("url", "");
        mPresenter = new UpgradePresenter();
        mPresenter.subscribe(this);
        Log.i(TAG, "onViewCreated: forceUpgrade : " + forceUpgrade);
        if (forceUpgrade) {
            //立刻下载更新
            mPresenter.downloadAppFull(url);
            viewDataBinding.upgradeSure.setVisibility(View.GONE);
            viewDataBinding.upgradeLate.setVisibility(View.VISIBLE);
            viewDataBinding.upgradeTitle.setText(R.string.upgrade_updating);
            viewDataBinding.upgradeTip.setVisibility(View.GONE);
            viewDataBinding.upgradeProgress.setVisibility(View.VISIBLE);
        } else {
            mPresenter.checkAppUpgrade();
        }
    }

    @Override
    protected void initListener() {
        viewDataBinding.upgradeSure.setOnClickListener(v -> {
            if (isSureTip) {
                Log.i(TAG, "onClick: mCheckVersionResult  is null");
                dismissAllowingStateLoss();
                return;
            }
            //立刻下载更新
            mPresenter.downloadApp(mCheckVersionResult);
            viewDataBinding.upgradeSure.setText(R.string.upgrade_cancel);
            viewDataBinding.upgradeLate.setVisibility(View.GONE);
            viewDataBinding.upgradeTip.setVisibility(View.GONE);
            isSureTip = true;
        });

        viewDataBinding.upgradeLate.setOnClickListener(v -> {
            // 是否正在升级
            dismissAllowingStateLoss();
        });
    }

    /**
     * 刷新界面
     */
    @Override
    public void updateVersion(CheckVersionResult result) {
        Log.i(TAG, "refreshUi: result: " + result);
        String newVersion = result.getNewVersion();
        Log.i(TAG, "refreshUi: newVersion: " + newVersion + "  versionName:" + versionName);
        String newVersionTips;
        if (TextUtils.equals(newVersion, versionName) || TextUtils.isEmpty(newVersion)) {
            // 已经是最新版本
            newVersionTips = String.format(getResources().getString(R.string.upgrade_new_tip), versionName);
            viewDataBinding.upgradeTitle.setText(R.string.firmupgrade_title_new);
        } else {
            // 不是最新版本
            mCheckVersionResult = result;
            viewDataBinding.upgradeLate.setVisibility(View.VISIBLE);
            viewDataBinding.upgradeSure.setText(getString(R.string.upgrade_sure));
            newVersionTips = String.format(getResources().getString(R.string.upgrade_new_tip), ModuleSettingConstants.VERSION_PREFIX + result.getNewVersion());
        }
        viewDataBinding.upgradeTip.setText(String.format(getString(R.string.module_setting_dubleline), currentVersionTips, newVersionTips));
    }

    @Override
    public void refreshProgress(int progress) {
        Log.i(TAG, String.valueOf(progress));
        if (progress < 0) return;
        String progressStr = String.format(getString(R.string.progress_unit), progress, getString(R.string.modulesetting_percent_unit));
        if (viewDataBinding.upgradeProgress.getVisibility() != View.VISIBLE) {
            viewDataBinding.upgradeProgress.setVisibility(View.VISIBLE);
        }
        viewDataBinding.upgradeProgress.setText(progressStr);
    }

    @Override
    public void install(String path) {
        Log.i(TAG, "path: " + path);
        viewDataBinding.upgradeTip.setVisibility(View.VISIBLE);
        viewDataBinding.upgradeProgress.setVisibility(View.GONE);
        if (path == null) {
            viewDataBinding.upgradeTip.setText(R.string.upgrade_download_fail);
            viewDataBinding.upgradeSure.setText(getString(R.string.upgrade_sure));
            viewDataBinding.upgradeLate.setVisibility(View.VISIBLE);
            isSureTip = false;
        } else {
            viewDataBinding.upgradeTip.setText(R.string.upgrade_download_success);
            viewDataBinding.upgradeSure.setVisibility(View.GONE);
            viewDataBinding.upgradeLate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        VUpgradeApi.Companion.getInstance().cancelAllWork(ApplicationUtils.getContext());
        mPresenter.unSubscribe();
        mPresenter = null;
    }

}
