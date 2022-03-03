package com.viomi.ovenso.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.FragmentDiscaleBinding;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

/**
 * 除垢的弹窗
 */
public class DiscaleFragment extends BaseDialogFragment<FragmentDiscaleBinding> {
    private static final String TAG = "DiscaleFragment";
    public static final String KEY_CHANGE_WATER = "keyChangeWater";
    private boolean isLauncher = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_discale;
    }

    @Override
    protected void initView() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        boolean isChangeWater = bundle.getBoolean(KEY_CHANGE_WATER);
        Log.i(TAG, "initChildView: " + isChangeWater);
        if (isChangeWater) {
            viewDataBinding.discaleSure.setVisibility(View.GONE);
            viewDataBinding.discaleCancel.setVisibility(View.GONE);
            viewDataBinding.discaleContent.setText(R.string.oven_discalling_tip_three);
        }
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.discaleCancel.setOnClickListener(view -> {
            dismissAllowingStateLoss();
        });
        viewDataBinding.discaleSure.setOnClickListener(view -> {
            if (isLauncher) {
                ViomiRxBus.getInstance().post(OvenBusEventConstants.MSG_START_COOK);
                dismissAllowingStateLoss();
                return;
            }
            viewDataBinding.discaleContent.setText(R.string.oven_discalling_tip_two);
            viewDataBinding.discaleSure.setText(R.string.oven_discalling_start);
            isLauncher = true;
        });
    }

}
