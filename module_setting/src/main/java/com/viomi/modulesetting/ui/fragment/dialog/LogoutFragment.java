package com.viomi.modulesetting.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentLogoutBinding;
import com.viomi.modulesetting.manager.ViotDeviceManager;
import com.viomi.modulesetting.presenter.AccountInfoPresenter;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 云米、小米退出登录对话框
 */
public class LogoutFragment extends BaseDialogFragment<FragmentLogoutBinding> {
    private static final String TAG = "LogoutFragment";
    public static final String IS_LOGOUT_MI = "isLogoutMi";
    public static final String TIPS = "tips";
    private static final String KEY_ISMIOT_LOGINOUT = "keyIsMiotLogout";
    private static final boolean DEFAULT_MIOT_LOGOUT = false;
    private final AccountInfoPresenter mPresenter;
    private boolean isMiotLogout;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_logout;
    }


    public LogoutFragment(AccountInfoPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }


    @Override
    protected void initView() {
        isMiotLogout = getArguments().getBoolean(KEY_ISMIOT_LOGINOUT, DEFAULT_MIOT_LOGOUT);
        String miotUnbindTip = getString(R.string.mi_iot_unbind_message);
        String ViotUnbindTip = getString(R.string.logout_message);
        String unBindTip = isMiotLogout ? miotUnbindTip : ViotUnbindTip;
        viewDataBinding.logoutTips.setText(unBindTip);
    }

    @Override
    protected void initListener() {
        viewDataBinding.logoutCancel.setOnClickListener(v -> dismissAllowingStateLoss());

        viewDataBinding.logoutSure.setOnClickListener(v -> {
            if (isMiotLogout) {
                UserInfoDb userInfoDb = ModuleSettingApplicaiton.getUserInfoDb();
                userInfoDb.setMiUserId(ModuleSettingConstants.VIOT_UNBIND);
                ViomiRoomDatabase.getDatabase().userInfoDao().insert(userInfoDb);
                MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
            } else {
                ViotDeviceManager.getInstance().resetDevice();
            }
            dismissAllowingStateLoss();
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    private void setDialogSize() {
        Log.i(TAG, "setDailogSize: ");
        Dialog dialog = getDialog();
        if (dialog == null) {
            Log.i(TAG, "onStart: dialog isf null return ");
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            Log.i(TAG, "onStart: window is null return  ");
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        // 横屏 和竖屏的界面
        if (ScreenUtils.isLandscape()) {
            params.height = (int) (ScreenUtils.getScreenHeight() * 0.9);
            params.width = (int) (ScreenUtils.getAppScreenWidth() * 0.56);
        } else {
            params.width = 640;
            params.height = 384;
        }
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
    }

    public static Bundle makeBundle(boolean isMiotLogout) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_ISMIOT_LOGINOUT, isMiotLogout);
        return bundle;
    }


}
