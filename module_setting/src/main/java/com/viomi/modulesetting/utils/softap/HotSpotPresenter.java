package com.viomi.modulesetting.utils.softap;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author bailing
 * date 2020/9/9
 * description：
 */
public class HotSpotPresenter implements SoftApContract.HotSpotPresenterI {
    private static final String TAG = "HotSpotPresenter";
    private SoftApContract.SoftApViewI mViewI;
    private final List<HotSpotDevice> mDeviceList;
    private Context mContext;
    private SoftApData mSoftApData;
    private final CompositeDisposable mCompositeDisposable;

    public HotSpotPresenter(Context context, SoftApContract.SoftApViewI viewI) {
        mContext = context;
        mViewI = viewI;
        mDeviceList = new ArrayList<>();
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(Observable.create((ObservableOnSubscribe<SoftApData>) emitter ->
                emitter.onNext(SoftApManagerUtils.getSoftApData(context)))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(softApData -> {
                    Log.i(TAG, "HotSpotPresenter: softApData : " + softApData);
                    mSoftApData = softApData;
                    initOpen();
                }));

    }

    /**
     * 设置热点名称及密码
     */
    private void changeSoftAp(boolean isNow) {
        Log.i(TAG, "changeSoftAp:isNow :  " + isNow);
        Log.i(TAG, "changeSoftAp: mSoftApData : " + mSoftApData);
        String sofeApName = mSoftApData.getWifiSsid();
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        final String changeWifiId = wifiId + "_hot";
        String wifiPwd = "12345678";
        boolean canChange = (!sofeApName.equals(changeWifiId)) && wifiId != null;
        Log.i(TAG, "changeSoftAp: canChange : " + canChange);
        if (!canChange) {
            return;
        }
        //修改成当前WiFi的名称及密码
        if (isNow) {
            SoftApManagerUtils.setSsidPwd(mContext, changeWifiId, wifiPwd);
            mSoftApData.setWifiSsid(changeWifiId);
            mSoftApData.setWifiPwd(wifiPwd);
        } else {
            // 关闭到打开 需要延迟修改，否则会出问题
            mCompositeDisposable.add(Observable.timer(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        SoftApManagerUtils.setSsidPwd(mContext, changeWifiId, wifiPwd);
                        mSoftApData.setWifiSsid(changeWifiId);
                        mSoftApData.setWifiPwd(wifiPwd);
                    }));
        }

    }

    private void initOpen() {
        Log.i(TAG, "initOpen: ");
        if (mViewI != null) {
            boolean enabled = false;
            int hertz = 1;
            if (mSoftApData != null) {
                enabled = mSoftApData.getWifiOnOff() == 1;
                hertz = SoftApManagerUtils.getWifiHertzMode(mContext);
            }
            if (enabled && !NetworkUtils.isConnected()) {
                SoftApManagerUtils.setWifiApEnabled(mContext, false);
                enabled = false;
            }
            mViewI.initSoftAp(enabled);
            mViewI.setCurrentHertz(hertz);
            Log.d(TAG, "initOpen: enabled: " + enabled);
            if (enabled) {
                changeSoftAp(true);
                notifyDeviceList();
            }
        }
    }

    @Override
    public void setOpen(boolean isOpen) {
        Log.d(TAG, "setOpen:  enabled  " + isOpen);
        if (mSoftApData != null) {
            if (mSoftApData.getWifiOnOff() == (isOpen ? 1 : 0)) {
                return;
            }
        }
        SoftApManagerUtils.setWifiApEnabled(mContext, isOpen);
        if (mSoftApData != null) {
            mSoftApData.setWifiOnOff(isOpen ? 1 : 0);
        }
        if (!isOpen) {
            if (mDeviceList != null) {
                mDeviceList.clear();
            }
        } else {
            changeSoftAp(false);
            if (mViewI != null) {
                mViewI.setCurrentHertz(SoftApManagerUtils.getWifiHertzMode(mContext));
            }
        }
    }

    @Override
    public void onDestroy() {
        mViewI = null;
        mContext = null;
        mCompositeDisposable.dispose();
    }

    @Override
    public void notifyDeviceList() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.add(Observable.create((ObservableOnSubscribe<List<HotSpotDevice>>) emitter ->
                    emitter.onNext(SoftApManagerUtils.getDeviceList(mContext)))
                    .subscribeOn(AndroidSchedulers.mainThread()).subscribe(dataList -> {
                        if (mDeviceList != null) {
                            mDeviceList.clear();
                            mDeviceList.addAll(dataList);
                            if (mViewI != null) {
                                mViewI.notifyItemChanged(mDeviceList);
                            }
                        }
                    }));
        }
    }

}
