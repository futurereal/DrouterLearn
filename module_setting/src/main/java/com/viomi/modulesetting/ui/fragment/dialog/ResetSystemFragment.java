package com.viomi.modulesetting.ui.fragment.dialog;

import android.content.Intent;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentResetSystemBinding;
import com.viomi.ovensocommon.BaseDialogFragment;

/**
 * @description 恢复出厂设置的弹框
 */
public class ResetSystemFragment extends BaseDialogFragment<FragmentResetSystemBinding> {
    private static final String TAG = "ResetSystemFragment";

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_reset_system;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.resetsystemCancel.setOnClickListener(v -> dismissAllowingStateLoss());
        viewDataBinding.resetsystemSure.setOnClickListener(v -> {
            Log.i(TAG, "initView: ");
            Intent intent = new Intent();
            intent.setAction(ModuleSettingConstants.BROADCAST_RECOVERY);
            ApplicationUtils.getContext().sendBroadcast(intent);
            dismissAllowingStateLoss();
        });
    }


}
