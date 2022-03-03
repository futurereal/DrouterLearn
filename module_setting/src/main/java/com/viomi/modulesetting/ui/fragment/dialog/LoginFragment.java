package com.viomi.modulesetting.ui.fragment.dialog;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.LoginContract;
import com.viomi.modulesetting.databinding.FragmentLoginBinding;
import com.viomi.modulesetting.presenter.LoginPresenter;
import com.viomi.ovensocommon.BasePresenterDialogFragment;
import com.viomi.ovensocommon.db.UserInfoDb;

import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 云米米家登录界面二维码生成
 */
public class LoginFragment extends BasePresenterDialogFragment<FragmentLoginBinding, LoginPresenter> implements LoginContract.View {
    private static final String TAG = "LoginFragment";
    public static final String TAG_LOGIN_GUIDE_DIALOG_FRAGMENT = "LoginGuideFragment";
    public static final String KEY_ISMIOT_LOGIN = "isMiotLogin";
    public static final boolean DEFAULT_MIOT_LOGIN = false;
    private Disposable mDisposable;
    private boolean isMiotLogin;

    public static Bundle makeBundle(boolean isBindViot) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_ISMIOT_LOGIN, isBindViot);
        return bundle;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initView() {
        initData();
    }


    private void initData() {
        isMiotLogin = getArguments().getBoolean(KEY_ISMIOT_LOGIN, DEFAULT_MIOT_LOGIN);
        Log.i(TAG, "initData:  isMiotLogin" + isMiotLogin);
        // 加载二维码的图片
        basePresenter.loadQrCodeBitmap(isMiotLogin);
        if (isMiotLogin) {
            viewDataBinding.loginGuide.setVisibility(View.GONE);
            viewDataBinding.loginWarming.setText(getResources().getString(R.string.mijia_login_scan_tips));
        }
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onStart() {
        boolean isLandScreen = ScreenUtils.isLandscape();
        if (isLandScreen) {
            landWidth = landHeight;
        } else {
            landHeight = landWidth;
        }
        super.onStart();
        if (ScreenUtils.isPortrait()) {
            WindowManager.LayoutParams layoutParameter = window.getAttributes();
            layoutParameter.gravity = Gravity.BOTTOM;
            layoutParameter.width = ScreenUtils.getScreenWidth() - 20;
            layoutParameter.height = ScreenUtils.getScreenWidth();
            window.setAttributes(layoutParameter);
        }
    }

    @Override
    protected void initListener() {
        // 如何使用
        viewDataBinding.loginGuide.setOnClickListener(v -> {
            Log.i(TAG, "initListener: show  LoginGuideFragment ");
            LoginGuideFragment guideDialog = new LoginGuideFragment();
            guideDialog.show(getActivity().getSupportFragmentManager(), TAG_LOGIN_GUIDE_DIALOG_FRAGMENT);
        });
        viewDataBinding.loginClose.setOnClickListener(v -> dismissAllowingStateLoss());
        viewDataBinding.retryLoadQrcode.setOnClickListener(v -> {
            viewDataBinding.loginQrCode.setVisibility(View.INVISIBLE);
            viewDataBinding.loadQrcodeProgressbar.setVisibility(View.VISIBLE);
            viewDataBinding.retryLoadQrcode.setVisibility(View.GONE);
            basePresenter.loadQrCodeBitmap(isMiotLogin);
        });
    }

    @Override
    public void showLoadingQRCode() {
        viewDataBinding.loadQrcodeProgressbar.setVisibility(View.VISIBLE);
        viewDataBinding.loginQrCode.setVisibility(View.INVISIBLE);
        viewDataBinding.retryLoadQrcode.setVisibility(View.GONE);
    }

    /**
     * 显示米家登录二维码
     * MiotDeviceManager  的 MiBindListener监听绑定状态
     *
     * @param bitmap 图片
     */
    @Override
    public void showMiQRCode(Bitmap bitmap) {
        Log.i(TAG, "showMiQRCode: ");
        viewDataBinding.loadQrcodeProgressbar.setVisibility(View.GONE);
        viewDataBinding.loginQrCode.setVisibility(View.VISIBLE);
        viewDataBinding.retryLoadQrcode.setVisibility(View.GONE);
        if (bitmap != null) {
            viewDataBinding.loginQrCode.setImageBitmap(bitmap);
        }
    }

    /**
     * 显示 云米登录二维码
     * qrcodeUrl 二维码的url
     */
    @Override
    public void showViomiQRCode(String qrcodeUrl) {
        Log.i(TAG, "showViomiQRCode: qrcodeUrl" + qrcodeUrl);
        viewDataBinding.loadQrcodeProgressbar.setVisibility(View.GONE);
        viewDataBinding.loginQrCode.setVisibility(View.VISIBLE);
        viewDataBinding.retryLoadQrcode.setVisibility(View.INVISIBLE);
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(qrcodeUrl))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(viewDataBinding.loginQrCode.getController())
                .setImageRequest(request)
                .build();
        viewDataBinding.loginQrCode.setController(controller);
    }

    @Override
    public void showRetry() {
        viewDataBinding.loadQrcodeProgressbar.setVisibility(View.INVISIBLE);
        viewDataBinding.loginQrCode.setVisibility(View.INVISIBLE);
        viewDataBinding.retryLoadQrcode.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        viewDataBinding.loginGuide.setText(getResources().getString(R.string.logining));
        viewDataBinding.loginGuide.setEnabled(false);
    }

    @Override
    public void showRebindMiotFragment(UserInfoDb userInfoDb) {
        ReBindMiotFragment reBindMiotFragment = new ReBindMiotFragment();
        Bundle bundle = ReBindMiotFragment.makeBundle(userInfoDb);
        reBindMiotFragment.setArguments(bundle);
        reBindMiotFragment.show(getActivity().getSupportFragmentManager(), "ReBindMiotDialogFragment_ViomiLogout");
    }

    @Override
    public void loginViomiFail(String message) {
        viewDataBinding.loginTitle.setText(R.string.login_title);
        Log.i(TAG, "loginFail: " + message);
        showRetry();
    }

    @Override
    public void dismissSelf() {
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }
}
