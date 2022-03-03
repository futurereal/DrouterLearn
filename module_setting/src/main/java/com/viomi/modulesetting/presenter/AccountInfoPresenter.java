package com.viomi.modulesetting.presenter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.AccountContract;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 用户信息界面
 */
public class AccountInfoPresenter extends AccountContract.Presenter<AccountContract.View> {
    private static final String TAG = "AccountInfoPresenter";
    private CompositeDisposable mCompositeDisposable;
    private final Context mContext;

    public AccountInfoPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
    public void subscribe(AccountContract.View view) {
        this.mView = view;
        mCompositeDisposable = new CompositeDisposable();
        // 订阅事件会 一直接收电控板的属性变化，什么可以避免
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            switch (busEvent.getMsgId()) {
                case ModuleSetingEventConstant.MSG_VIOMI_LOGOUT:
                    Log.i(TAG, "subscribe:viot logout");
                    ViomiToastUtil.showToastSuccess(mContext, mContext.getString(R.string.setting_logout_success));
                    loadUserInfoAndUpdateView((UserInfoDb) busEvent.getMsgObject());
                    break;
                case ModuleSetingEventConstant.MSG_VIOMI_LOGIN:
                    ViomiToastUtil.showToastSuccess(mContext, mContext.getString(R.string.setting_login_success));
                    Log.i(TAG, "subscribe: viot loginSuccess");
                    loadUserInfoAndUpdateView((UserInfoDb) busEvent.getMsgObject());
                    break;
                case ModuleSetingEventConstant.MSG_MIOT_LOGIN:
                    Log.i(TAG, "subscribe: miot logsuccess");
                    // 保存更新状态
                    if (mView != null) {
                        mView.refreshMiBindStatus(true);
                    }
                    break;
                case ModuleSetingEventConstant.MSG_MIOT_LOGOUT:
                    Log.i(TAG, "subscribe: miot logout");
                    if (mView != null) {
                        mView.refreshMiBindStatus(false);
                    }
                    break;
            }
        });
        mCompositeDisposable.add(disposable);
    }

    public void unSubscribe() {
        this.mView = null;
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

    @Nullable
    private AccountContract.View mView;

    /**
     * 从本地文件获取用户信息 并且更新界面
     */
    @Override
    public void loadUserInfoAndUpdateView(UserInfoDb userInfoDb) {
        Log.i(TAG, "loadUserInfoAndUpdateView: " + userInfoDb);
        if (mView != null && userInfoDb != null) {
            mView.refreshUserInfo(userInfoDb);
        }

    }


}
