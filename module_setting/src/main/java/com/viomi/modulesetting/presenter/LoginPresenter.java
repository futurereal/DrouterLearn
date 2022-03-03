package com.viomi.modulesetting.presenter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.contract.LoginContract;
import com.viomi.modulesetting.entity.QRCodeBase;
import com.viomi.modulesetting.manager.ViotDeviceManager;
import com.viomi.modulesetting.repository.LoginRepository;
import com.viomi.ovensocommon.componentservice.miot.BindKeyCallBack;
import com.viomi.ovensocommon.componentservice.miot.IMiotService;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.rxbus.RxSchedulerUtil;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 扫码登录Presenter
 */
public class LoginPresenter extends LoginContract.Presenter<LoginContract.View> {
    private static final String TAG = "LoginPresenter";
    private static final int QRCODE_WIDTH = 150;
    private static final int QRCODE_HEIGHT = 150;
    private CompositeDisposable mCompositeDisposable;
    private Context mContext;
    private String clientId;

    public LoginPresenter() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        mContext = ApplicationUtils.getContext();
        mCompositeDisposable = new CompositeDisposable();
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            if (mView == null) {
                Log.i(TAG, "subScrible : mView is null return");
                return;
            }
            switch (busEvent.getMsgId()) {
                case ModuleSetingEventConstant.MSG_VIOMI_LOGIN:
                case ModuleSetingEventConstant.MSG_MIOT_LOGIN:
                    Log.i(TAG, "login bind device success");
                    mView.dismissSelf();
                    break;
                case ModuleSetingEventConstant.MSG_BIND_VIOMI_DEVICE_FAIL: // 绑定设备失败
                    Log.i(TAG, "login bind device fail");
                    mView.loginViomiFail(mContext.getResources().getString(R.string.login_error_tip));
                    break;
                case ModuleSetingEventConstant.MSG_BIND_DISMISS_QRCODE:
                    mView.dismissSelf();
                    break;
                default:
                    break;
            }
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void loadQrCodeBitmap(boolean isMiotLogin) {
        Log.i(TAG, "loadQrCodeBitmap: isMiotLogin" + isMiotLogin);
        if (isMiotLogin) {
            loadMiQRCode();
        } else {
            loadViomiQRCode();
        }
    }

    public void loadViomiQRCode() {
        Log.i(TAG, "loadViomiQRCode: " + mView);
        mView.showLoadingQRCode();
        Log.d(TAG, clientId);
        String mac = ViomiProvideUtil.getMac();
        mac = mac == null ? "" : mac.replaceAll(":", "");
        clientId = mac + System.currentTimeMillis() / 1000;
        Disposable disposable = LoginRepository.getInstance().createQRCode(clientId)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(qrCodeBase -> {
                    Log.i(TAG, "loadViomiQRCode getQrcodeSuccess:" + (qrCodeBase == null ? "null" : qrCodeBase.toString()));
                    if (mView == null) {
                        Log.i(TAG, "loadViomiQRCode: mView is null return");
                        return;
                    }
                    if (qrCodeBase != null && !TextUtils.isEmpty(qrCodeBase.getLoginQRCode().getResult())) {
                        mView.showViomiQRCode(qrCodeBase.getLoginQRCode().getResult());
                        // 检查登录状态
                        checkViomiLoginStatus();
                    } else {
                        mView.showRetry();
                    }
                }, throwable -> {
                    Log.e(TAG, "login get qrcode fail:" + throwable.getMessage());
                    if (mView != null) {
                        mView.showRetry();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 每隔三秒 获取一次登录状态
     */
    public void checkViomiLoginStatus() {
        Log.i(TAG, "checkViomiLoginStatus: ");
        // 云米二维码登录的时候会动态生成一个一个ClientID 并且保存
        Disposable disposable = Flowable.interval(0, 3, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.io())
                .onTerminateDetach()
                .flatMap((Function<Long, Publisher<QRCodeBase>>) aLong -> {
                    Log.i(TAG, "checkViomiLoginStatus: clientId: " + clientId);
                    if (aLong > 60) {
                        if (mView != null) {
                            mView.loginViomiFail(mContext.getResources().getString(R.string.login_error_tip));
                        }
                        mCompositeDisposable.clear();
                        return null;
                    }
                    return LoginRepository.getInstance().getLoginStatus(clientId);
                })
                .takeUntil(result -> result != null
                        && result.getLoginQRCode().getResult() != null
                        && result.getLoginQRCode().getResult().getToken() != null
                        && result.getLoginQRCode().getResult().getAppendAttr() != null
                        && result.getLoginQRCode().getResult().getLoginResult() != null
                        && result.getLoginQRCode().getResult().getLoginResult().getUserCode() != null)
                .filter(result -> result != null
                        && result.getLoginQRCode().getResult() != null
                        && result.getLoginQRCode().getResult().getToken() != null
                        && result.getLoginQRCode().getResult().getAppendAttr() != null
                        && result.getLoginQRCode().getResult().getLoginResult() != null
                        && result.getLoginQRCode().getResult().getLoginResult().getUserCode() != null)
                .map((Function<QRCodeBase, UserInfoDb>) qrCodeBase -> {
                    qrCodeBase.getLoginQRCode().getResult().parseAppendAttr();
                    // qrCodeBase  转为 UserInfoDb
                    UserInfoDb userInfoDb = LoginRepository.getInstance().convertQrcodeToUserInfoDb(qrCodeBase);
                    userInfoDb.setFromQrcode(true);
                    return userInfoDb;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .map(userInfoDb -> {
                    if (mView != null) {
                        mView.showLoading();
                    }
                    return userInfoDb;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .subscribe(userInfoDb -> {
                    Log.i(TAG, "login success:" + userInfoDb);
                    if (userInfoDb == null || mView == null) {
                        return;
                    }
                    boolean isMiotBind = userInfoDb.isBindMiot();
                    Log.i(TAG, "checkViomiLoginStatus: isMiotBind: " + isMiotBind);
                    // 如果米家已经登录
                    if (isMiotBind) {
                        mView.showRebindMiotFragment(userInfoDb);
                    } else {
                        ViotDeviceManager.getInstance().bindViotServcie(userInfoDb);
                    }
                }/*, throwable -> {
                    Log.e(TAG, "login fail:" + throwable.getMessage());
                    if (mView != null) {
                        mView.loginViomiFail(mContext.getResources().getString(R.string.login_error_tip));
                    }
                }*/);
        mCompositeDisposable.add(disposable);
    }

    public void loadMiQRCode() {
        IMiotService miotService = MiotServiceFactory.getInstance().getMiotService();
        Log.i(TAG, "loadMiQRCode:miotService  " + miotService.getClass().getName());
        miotService.getMiotBindKey(new BindKeyCallBack() {
            @Override
            public void keyReuslt(String bindKey, String bindResult) {
                Log.i(TAG, "keyReuslt: " + bindKey + "  bindResult: " + bindResult);
                Observable.just(bindKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String bindKey) throws Exception {
                        Log.i(TAG, "accept: bindKEy: " + bindKey);
                        // 获取成功
                        if (!TextUtils.isEmpty(bindKey)) {
                            mView.showMiQRCode(LoginRepository.getInstance().createMiQRCodeBimap(bindKey, QRCODE_WIDTH, QRCODE_HEIGHT));
                            return;
                        }
                        // 获取失败
                        mView.showRetry();
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

}
