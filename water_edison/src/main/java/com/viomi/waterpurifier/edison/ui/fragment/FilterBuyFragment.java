package com.viomi.waterpurifier.edison.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterBugBinding;
import com.viomi.waterpurifier.edison.entity.FilterEntity;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: WaterPurifier
 * @Description: 滤芯购买界面
 */
public class FilterBuyFragment extends BaseDialogFragment<FragmentFilterBugBinding> {
    private static final String TAG = "FilterBuyFragment";
    private FilterEntity filterEntity;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_filter_bug;
    }

    @Override
    protected void initView() {
        filterEntity = getArguments().getParcelable(WaterConstant.KEY_FILTERENTIY);
    }

    @Override
    protected void initListener() {
        viewDataBinding.filterbugClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String filterTipFomat = getString(R.string.filter_reset_tip);
        String filterName = filterEntity.getFilterName();
        String filterTipContent = String.format(filterTipFomat, filterName);
        viewDataBinding.filterbugBuytip.setText(filterTipContent);
        viewDataBinding.filterbugQrcode.setImageResource(filterEntity.getQrcodeImgResourceId());
    }


}
