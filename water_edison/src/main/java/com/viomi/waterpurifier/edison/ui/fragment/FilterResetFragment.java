package com.viomi.waterpurifier.edison.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentFilterResetBinding;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.presenter.FilterResetPresenter;
import com.viomi.waterpurifier.edison.presenter.FlushContact;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: WaterPurifier
 * @ClassName: FilterBuyFragment
 * @Description: 滤芯重置 和 购买界面
 */
public class FilterResetFragment extends BaseDialogFragment<FragmentFilterResetBinding> implements FlushContact.View {
    private static final String TAG = "FilterResetFragment";
    FlushContact.Presenter presenter;
    FilterEntity filterEntity;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_filter_reset;
    }

    @Override
    protected void initView() {
        filterEntity = getArguments().getParcelable(WaterConstant.KEY_FILTERENTIY);
        String filterName = filterEntity.getFilterName();
        String filterTipFomat = getString(R.string.filter_reset_tip);
        String filterTipContent = String.format(filterTipFomat, filterName);
        viewDataBinding.filterresetTitle.setText(filterName);
        viewDataBinding.filterresetBugtip.setText(filterTipContent);
        viewDataBinding.filterresetQrcode.setImageResource(filterEntity.getQrcodeImgResourceId());
        presenter = new FilterResetPresenter(this);
        presenter.subscribe(this);
    }

    @Override
    protected void initListener() {
        viewDataBinding.filterresetReset.setOnClickListener(view -> {
            presenter.startResetFilter(filterEntity);
        });
        viewDataBinding.filterresetClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            if (msgId == CommonConstant.MSG_ACTION_SUCCESS) {
                dismissAllowingStateLoss();
                return;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = 700;
                window.setAttributes(wlp);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        presenter.unSubscribe();
    }

    @Override
    public void process(int process) {

    }
}
