package com.viomi.modulesetting.ui.fragment;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.AccountContract;
import com.viomi.modulesetting.databinding.FragmentAccountinfoBinding;
import com.viomi.modulesetting.presenter.AccountInfoPresenter;
import com.viomi.modulesetting.ui.fragment.dialog.LoginFragment;
import com.viomi.modulesetting.ui.fragment.dialog.LogoutFragment;
import com.viomi.modulesetting.utils.ModuleSettingUtil;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 用户信息界面Fragment
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_ACCOUNT)
public class AccountInfoFragment extends BindingBaseFragment<FragmentAccountinfoBinding> implements AccountContract.View {
    private static final String TAG = "AccountInfoFragment";
    private AccountInfoPresenter mPresenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_accountinfo;
    }

    @Override
    protected void initView() {
        boolean showBindMiot = ModuleSettingServiceFactory.getInstance().getViotService().isShowMiotBind();
        Log.i(TAG, "initView: showBindMiot: " + showBindMiot);
        if (!showBindMiot) {
            viewDataBinding.miotDealTitle.setVisibility(View.GONE);
            viewDataBinding.accountinfoMiotDeal.setVisibility(View.GONE);
            viewDataBinding.accountinfoMiotTitle.setVisibility(View.GONE);
            viewDataBinding.accountinfoMiotStatus.setVisibility(View.GONE);
            viewDataBinding.groupInfo.removeView(viewDataBinding.accountinfoMiotTitle);
            viewDataBinding.groupInfo.removeView(viewDataBinding.accountinfoMiotStatus);
            viewDataBinding.groupMiotDeal.removeView(viewDataBinding.miotDealTitle);
            viewDataBinding.groupMiotDeal.removeView(viewDataBinding.accountinfoMiotDeal);
        }
        refreshUserInfo(ModuleSettingApplicaiton.getUserInfoDb());
    }

    @Override
    protected void initListener() {
        viewDataBinding.accountinfoViotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String viotStatusTip = viewDataBinding.accountinfoViotLogin.getText().toString();
                Log.i(TAG, "onClick: viotStatusTip: " + viotStatusTip);
                if (isPubcliKeyError()) {
                    return;
                }
                if (ModuleSettingUtil.checkNetAndShowTip()) {
                    return;
                }
                if (TextUtils.equals(viotStatusTip, getString(R.string.login_now))) {
                    showLoginFragment(false);
                } else {
                    showLogoutFragment(false);
                }
            }
        });
        viewDataBinding.accountinfoMiotDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPubcliKeyError()) {
                    return;
                }
                if (ModuleSettingUtil.checkNetAndShowTip()) {
                    return;
                }
                String miotStatusTip = viewDataBinding.accountinfoMiotDeal.getText().toString();
                Log.i(TAG, "onClick: status: " + miotStatusTip);
                if (TextUtils.equals(miotStatusTip, getString(R.string.mi_iot_no_bind))) {
                    showLoginFragment(true);
                } else {
                    showLogoutFragment(true);
                }
            }
        });
    }

    /**
     * 校验  publcKey是否 错误
     *
     * @return key 是否错误
     */
    private boolean isPubcliKeyError() {
        boolean isPublickeyRight = ViomiProvideUtil.isPublicKeyRight();
        Log.i(TAG, "isPubcliKeyError: isPublickeyRight: " + isPublickeyRight);
        if (!isPublickeyRight) {
            ViomiToastUtil.showToastCenter(getString(R.string.setting_publickey_tip));
            return true;
        }
        return false;
    }

    /**
     * 显示退出登录界面
     *
     * @param isMiotLogout
     */
    private void showLogoutFragment(boolean isMiotLogout) {
        LogoutFragment logoutFragment = new LogoutFragment(mPresenter);
        logoutFragment.setArguments(LogoutFragment.makeBundle(isMiotLogout));
        logoutFragment.show(getActivity().getSupportFragmentManager(), "LogoutDialogFragment_MiLogout");
    }

    /**
     * 显示登录界面
     *
     * @param isMiotLogin
     */
    private void showLoginFragment(boolean isMiotLogin) {
        LoginFragment viomiLoginDialog = new LoginFragment();
        viomiLoginDialog.setArguments(LoginFragment.makeBundle(isMiotLogin));
        viomiLoginDialog.show(getActivity().getSupportFragmentManager(), "ViomiLoginDialogFragment");
    }

    @Override
    public void initData() {
        mPresenter = new AccountInfoPresenter(requireContext());
        mPresenter.subscribe(this);
    }

    @Override
    public void refreshUserInfo(UserInfoDb userInfoDb) {
        Log.i(TAG, "refreshUserInfo: " + userInfoDb);
        if (userInfoDb == null) {
            return;
        }
        boolean isMiotBind = userInfoDb.isBindMiot();
        Log.i(TAG, "refreshUserInfo: " + isMiotBind);
        // 米家账号的处理
        if (isMiotBind) {
            viewDataBinding.accountinfoMiotDeal.setText(R.string.mi_iot_has_bind);
            viewDataBinding.accountinfoMiotStatus.setText(R.string.mi_iot_has_bind);
        } else {
            viewDataBinding.accountinfoMiotDeal.setText(R.string.mi_iot_no_bind);
            viewDataBinding.accountinfoMiotStatus.setText(R.string.mi_iot_no_bind);
        }
        String userid = userInfoDb.getUserId();
        Log.i(TAG, "refreshUserInfo: userId: " + userid);
        if (TextUtils.isEmpty(userid) || TextUtils.equals(userid, ModuleSettingConstants.VIOT_UNBIND)) {
            //云米帐号未登录
            Log.i(TAG, "refreshUserInfo: vmi  isNotLogin");
            viewDataBinding.groupInfo.setVisibility(View.GONE);
            viewDataBinding.groupMiotDeal.setVisibility(View.VISIBLE);
            viewDataBinding.accountinfoViotLogin.setText(getResources().getString(R.string.login_now));
            viewDataBinding.accountinfoViotLogin.setSelected(false);
            viewDataBinding.accountinfoViotLogin.setBackgroundResource(R.drawable.shape_green_btn);
            viewDataBinding.accountinfoViotLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white));
            return;
        }
        //云米帐号已登录;
        viewDataBinding.groupMiotDeal.setVisibility(View.GONE);
        viewDataBinding.groupInfo.setVisibility(View.VISIBLE);
        viewDataBinding.accountinfoName.setText(userInfoDb.getNickname());
        viewDataBinding.accountinfoId.setText(userInfoDb.getAccount());
        viewDataBinding.accountinfoViotLogin.setText(getResources().getString(R.string.login_quit));
        viewDataBinding.accountinfoViotLogin.setSelected(true);
        viewDataBinding.accountinfoViotLogin.setBackgroundResource(R.drawable.setting_button);
        viewDataBinding.accountinfoViotLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.accountinfo_loginout));
        // 头像
        String headUrlStr = userInfoDb.getHeadImgUrl();
        Log.i(TAG, "refreshUserInfo: vmi  isLogin  headUrlStr " + headUrlStr);
        Uri headImageUri;
        if (TextUtils.isEmpty(headUrlStr) || TextUtils.equals(headUrlStr, "null")) {
            headImageUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.menuheader_default);
        } else {
            headImageUri = Uri.parse(headUrlStr);
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(headImageUri)
                .setResizeOptions(new ResizeOptions(60, 60))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(viewDataBinding.accountinfoHead.getController())
                .setImageRequest(request)
                .build();
        viewDataBinding.accountinfoHead.setController(controller);
        boolean miBindStatus = userInfoDb.isBindMiot();
        Log.i(TAG, "refreshUserInfo: miBindStatus: " + miBindStatus);
        refreshMiBindStatus(miBindStatus);
    }

    @Override
    public void refreshMiBindStatus(boolean isBind) {
        Log.i(TAG, "refreshMiBindStatus: isBind: " + isBind);
        UserInfoDb userInfoDb = ModuleSettingApplicaiton.getUserInfoDb();
        userInfoDb.setBindMiot(isBind);
        if (isBind == false) {
            //todo zhangdz 这个要根据情况来定
//            userInfoDb.setMiUserId(ModuleSettingConstants.STATAS_LOGOUT);
        }
        ModuleSettingApplicaiton.setUserInfoDb(userInfoDb);
        //重新设置缓存
        ViomiRoomDatabase.getDatabase().userInfoDao().insert(userInfoDb);
        viewDataBinding.accountinfoMiotDeal.setText(isBind ? getResources().getString(R.string.mi_iot_has_bind) :
                getResources().getString(R.string.mi_iot_no_bind));
        viewDataBinding.accountinfoMiotStatus.setText(isBind ? getResources().getString(R.string.mi_iot_has_bind) :
                getResources().getString(R.string.mi_iot_no_bind));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.unSubscribe();
            mPresenter = null;
        }
    }
}
