package com.viomi.modulesetting.ui.fragment.dialog;

import static com.viomi.modulesetting.ModuleSettingConstants.VERSION_PREFIX;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.FirmUpGradeContract;
import com.viomi.modulesetting.databinding.FragmentUpgradeFirmBinding;
import com.viomi.modulesetting.entity.FirmUpdateResult;
import com.viomi.modulesetting.http.download.SettingDownloadManager;
import com.viomi.modulesetting.presenter.FirmUpgradePresenter;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.modulesetting.utils.SettingPreference;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import java.util.List;

/**
 * @Description: 固件升级弹框
 */
public class FirmUpgradeFragment extends BaseDialogFragment<FragmentUpgradeFirmBinding> implements FirmUpGradeContract.View {
    private static final String TAG = "FirmUpgradeFragment";
    private static final String KEY_VERSION_NAME = "keyVersionName";
    private String mUrl;
    private FirmUpGradeContract.Presenter mPresenter;
    private String currentFirmVersion;
    private String currentVersionFullTip;
    private String newVersionTips;
    private int newVersionCode;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_upgrade_firm;
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new NullPointerException();
        }
        currentFirmVersion = bundle.getString(KEY_VERSION_NAME);
        currentVersionFullTip = String.format(getResources().getString(R.string.upgrade_current_tip), currentFirmVersion);
        viewDataBinding.firmupgradeTip.setText(currentVersionFullTip);
        mPresenter = new FirmUpgradePresenter();
        mPresenter.subscribe(this);
        // 检查版本号
        mPresenter.checkFirmVersion();
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.firmupgradeSure.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mUrl)) {
                Log.i(TAG, "onClick: mUrl is empty return ");
                dismissAllowingStateLoss();
                return;
            }
            viewDataBinding.firmupgradeTip.setText(R.string.setting_firm_updating);
            mPresenter.downloadFirmFile(mUrl);
        });
        viewDataBinding.firmupgradeCancel.setOnClickListener(v -> {
            // 结束固件升级
            dismissAllowingStateLoss();
        });
    }

    @Override
    public void updateVersionTip(List<FirmUpdateResult> result) {
        Log.i(TAG, "updateVersionTip: result: " + result.size());
        int versionPreIndex = currentFirmVersion.indexOf(ModuleSettingConstants.VERSION_TITLE);
        int currentVersionCode = Integer.parseInt(currentFirmVersion.substring(versionPreIndex + 1));
        newVersionCode = -1;
        if (result.size() > 0) {
            String newVersionCodeStr = result.get(0).getLatestFwVer();
            newVersionCode = Integer.parseInt(newVersionCodeStr);
            Log.i(TAG, "updateVersionTip: currentVersion: " + currentVersionCode + " newVersion: " + newVersionCode);
        }
        if (newVersionCode > currentVersionCode) {
            // 不是最新版本
            viewDataBinding.firmupgradeCancel.setVisibility(View.VISIBLE);
            viewDataBinding.firmupgradeCancel.setText(R.string.upgrade_late);
            viewDataBinding.firmupgradeSure.setText(R.string.upgrade_sure);
            mUrl = result.get(0).getDownloadUrl();
            newVersionTips = String.format(getResources().getString(R.string.upgrade_new_tip),
                    VERSION_PREFIX + ModuleSettingUtil.getFirmVersionStr(newVersionCode));
        } else {
            newVersionTips = String.format(getResources().getString(R.string.upgrade_new_tip),
                    VERSION_PREFIX + ModuleSettingUtil.getFirmVersionStr(newVersionCode));
            Log.i(TAG, "updateVersionTip: mUrl : " + mUrl);
            // 已经是最新版本
            viewDataBinding.firmupgradeCancel.setVisibility(View.GONE);
            viewDataBinding.firmupgradeTitle.setText(R.string.firmupgrade_title_new);
            viewDataBinding.firmupgradeSure.setText(R.string.module_sure);
        }

        String finalVersionTip = currentVersionFullTip + "\n" + newVersionTips;
        viewDataBinding.firmupgradeTip.setText(finalVersionTip);
    }

    @Override
    public void refreshProgress(int progress) {
        Log.i(TAG, "refreshProgress: progress: " + progress);
        if (progress < 0) {
            return;
        }
        if (viewDataBinding.firmupgradeTip.getVisibility() == View.VISIBLE) {
            viewDataBinding.firmupgradeTip.setVisibility(View.GONE);
            viewDataBinding.firmupgradeCancel.setVisibility(View.GONE);
            viewDataBinding.firmupgradeSure.setVisibility(View.GONE);
        }
        String progressStr = String.format(getString(R.string.progress_unit), progress, getString(R.string.modulesetting_percent_unit));
        viewDataBinding.firmupgradeProgress.setText(progressStr);
    }

    @Override
    public void updateSucess() {
        Log.i(TAG, "updateSucess: ");
        viewDataBinding.firmupgradeTitle.setText(R.string.upgrade_suceess);
        viewDataBinding.firmupgradeTip.setVisibility(View.VISIBLE);
        currentVersionFullTip = (String.format(getResources().getString(R.string.upgrade_current_tip),
                VERSION_PREFIX + ModuleSettingUtil.getFirmVersionStr(newVersionCode)));
        String finalVersionTip = currentVersionFullTip + "\n" + newVersionTips;
        viewDataBinding.firmupgradeTip.setText(finalVersionTip);
        viewDataBinding.firmupgradeCancel.setVisibility(View.VISIBLE);
        viewDataBinding.firmupgradeCancel.setText(getString(R.string.setting_firm_finish));
        viewDataBinding.firmupgradeCancel.setBackgroundResource(R.drawable.shape_green_btn);
        viewDataBinding.firmupgradeCancel.setTextColor(Color.WHITE);
        viewDataBinding.firmupgradeProgress.setVisibility(View.GONE);
        SettingPreference.getInstance().setValue(SettingPreference.MCU_UPGRADING, false);
        //1  获取所有属性   2 更新 关于软件里面的固件版本
        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_FIRMUPDATE_SUCCESS,
                VERSION_PREFIX + ModuleSettingUtil.getFirmVersionStr(newVersionCode));
    }

    /**
     * 升级失败 ，给予 重新升级 或者暂不升级的提示
     */
    @Override
    public void updateFail() {
        Log.i(TAG, "updateFail: ");
        viewDataBinding.firmupgradeTip.setText(R.string.setting_firm_fail);
        viewDataBinding.firmupgradeProgress.setVisibility(View.GONE);
        viewDataBinding.firmupgradeTip.setVisibility(View.VISIBLE);
        viewDataBinding.firmupgradeSure.setVisibility(View.VISIBLE);
        viewDataBinding.firmupgradeCancel.setVisibility(View.VISIBLE);
        viewDataBinding.firmupgradeSure.setVisibility(View.VISIBLE);
        viewDataBinding.firmupgradeCancel.setText(R.string.upgrade_late);
        viewDataBinding.firmupgradeSure.setText(R.string.upgrade_sure);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            SettingDownloadManager.getInstance().cancelDownloadTask();
            mPresenter.unSubscribe();
            mPresenter = null;
        }
    }

    public static Bundle makeBundle(String versionName) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VERSION_NAME, versionName);
        return bundle;
    }
}
