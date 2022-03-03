package com.viomi.waterpurifier.edison.ui.fragment;

import android.content.DialogInterface;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.FragmentFlushBinding;
import com.viomi.waterpurifier.edison.presenter.FilterResetPresenter;
import com.viomi.waterpurifier.edison.presenter.FlushContact;

/**
 * 冲洗弹窗
 */
public class FlushFragment extends BaseDialogFragment<FragmentFlushBinding> implements FlushContact.View {

    FilterResetPresenter presenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_flush;
    }

    @Override
    protected void initView() {
        presenter = new FilterResetPresenter(this);
        presenter.subscribe(this);
        presenter.flush();
    }

    @Override
    protected void initListener() {
        viewDataBinding.tvCancel.setOnClickListener(v -> {
            presenter.stopsFlush();
            dismissAllowingStateLoss();
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        presenter.unSubscribe();
    }


    @Override
    public void process(int process) {
        viewDataBinding.tvPer.setText(process + "%");
        if (process == 100) {
            FlushSucessDialog flushSucessDialog = new FlushSucessDialog();
            flushSucessDialog.setArguments(getArguments());
            dismissAllowingStateLoss();
            flushSucessDialog.show(getActivity().getSupportFragmentManager(), "flushSucessDialog");
        }
    }
}
