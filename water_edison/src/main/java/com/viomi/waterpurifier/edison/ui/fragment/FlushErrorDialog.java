package com.viomi.waterpurifier.edison.ui.fragment;


import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.DialogFlushErrorBinding;
import com.viomi.waterpurifier.edison.entity.WaterErrorEntity;

public class FlushErrorDialog extends BaseDialogFragment<DialogFlushErrorBinding> implements DialogInterface.OnDismissListener {
    private static final String TAG = "FlushErrorDialog";
    public static final String KEY_BUNDLE = "keyBundle";

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_flush_error;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        WaterErrorEntity error = getArguments().getParcelable(KEY_BUNDLE);
        if (error == null) {
            return;
        }
        viewDataBinding.flusherrorTitle.setText(error.getTitle());
        viewDataBinding.flusherrorContent.setText(error.getDiscription());
        // 监听串口通信，串口连上消掉弹出框
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        WindowManager.LayoutParams layoutParameter = window.getAttributes();
        layoutParameter.gravity = Gravity.CENTER;
        window.setAttributes(layoutParameter);
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.flusherrorCancel.setOnClickListener(v -> {
            dismissAllowingStateLoss();
        });
    }
}
