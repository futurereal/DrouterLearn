package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentRebindMiotBinding;
import com.viomi.modulesetting.manager.ViotDeviceManager;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ReBindMiotFragment
 * @Description: 重新绑定小米账号的提示框
 */
public class ReBindMiotFragment extends BaseDialogFragment<FragmentRebindMiotBinding> {
    private static final String TAG = "ReBindMiotDialogFragmen";
    private static final String KEY_USERINFO = "KEY_QRCODE_BASE";
    private UserInfoDb userInfoDb;

    @Override
    protected void initView() {
        viewDataBinding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_BIND_DISMISS_QRCODE);
            }
        });
        viewDataBinding.sureBtn.setText("是");
        viewDataBinding.sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                ViotDeviceManager.getInstance().bindViotServcie(userInfoDb);
            }
        });
        Bundle bundle = getArguments();
        userInfoDb = (UserInfoDb) bundle.get(KEY_USERINFO);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_rebind_miot;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            Log.i(TAG, "onStart dialog is null return");
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            Log.i(TAG, "onstart window  is null return");
            return;
        }
        Log.i(TAG, "onStart  setWindowParameter");
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = 480;
        wlp.height = 300;
        window.setAttributes(wlp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    public static Bundle makeBundle(UserInfoDb userInfoDb) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_USERINFO, userInfoDb);
        return bundle;
    }

}
