package com.viomi.waterpurifier.edison.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterErrorBinding;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: WaterPurifier
 * @Description: 滤芯过期对话框
 */
public class FilterErrorFragment extends BaseDialogFragment<FragmentFilterErrorBinding> {
    private static final String TAG = "FilterErrorFragment";
    private FilterEntity filterEntity;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_filter_error;
    }

    @Override
    protected void initView() {
        filterEntity = getArguments().getParcelable(WaterConstant.KEY_FILTERENTIY);
        Log.i(TAG, "initChildView: waterErrorEntity : " + filterEntity);
        viewDataBinding.filtererrorTitle.setText(filterEntity.getFilterName());
        String leftTimeTip = getResources().getString(R.string.error_filter_1eft);
        String finalLeftTime = String.format(leftTimeTip, filterEntity.getLefePercent());
        viewDataBinding.filtererrorLefetime.setText(finalLeftTime);
    }

    @Override
    protected void initListener() {
        viewDataBinding.filtererrorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        viewDataBinding.filtererrorBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBuyFragment buyNowDialog = new FilterBuyFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(WaterConstant.KEY_FILTERENTIY, filterEntity);
                buyNowDialog.setArguments(bundle);
                buyNowDialog.show(getActivity().getSupportFragmentManager(), TAG);
                dismissAllowingStateLoss();
            }
        });
        viewDataBinding.filtererrorReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterSerialManager.getInstance().resetFilter(filterEntity);
                dismissAllowingStateLoss();
            }
        });
    }

}