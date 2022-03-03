package com.viomi.modulesetting.ui.fragment;

import static android.content.Context.WIFI_SERVICE;
import static android.view.View.MeasureSpec.AT_MOST;
import static com.viomi.modulesetting.utils.wifi.Wifi.ConfigSec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentWlanSetBinding;
import com.viomi.modulesetting.ui.adapter.WlanSetAdapter;
import com.viomi.modulesetting.ui.fragment.dialog.WlanPwdErrorFragment;
import com.viomi.modulesetting.ui.fragment.dialog.WlanPwdForgetFragment;
import com.viomi.modulesetting.ui.fragment.dialog.WlanPwdInputFragment;
import com.viomi.modulesetting.utils.RxLifecycleUtil;
import com.viomi.modulesetting.utils.softap.SoftApData;
import com.viomi.modulesetting.utils.softap.SoftApManagerUtils;
import com.viomi.modulesetting.utils.wifi.ConfigurationSecurities;
import com.viomi.modulesetting.utils.wifi.Wifi;
import com.viomi.modulesetting.utils.wifi.WifiSupport;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.RxSchedulerUtil;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: wifi管理fragment
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
public class WlanSetFragment extends BindingBaseFragment<FragmentWlanSetBinding> {
    private static final String TAG = "WlanSetFragment";
    public static final int UPDATE_WIFI_LIST_DELAY = 150;
    private boolean mIsShowing = false;
    // 正在连接的 ScanResult
    private ScanResult mScanResult;
    private WifiManager mWifiManager;
    private final List<ScanResult> mNearbyList = new ArrayList<>();
    private final List<ScanResult> mPairList = new ArrayList<>();
    private WlanSetAdapter mNearbyAdapter, mPairAdapter;
    private boolean isNewWifiPasswordInput = false;
    //检查wifi 的监听
    private Disposable checkWifiStatusDisposable;
    // 刷新wif 的监听
    private Disposable scanWifiDisposable;
    //是否已经有需要新连接的wifi，有了就不需要间隔10S连接第一个wifi
    private static final int MSG_UPDATE_SCAN_RESULT = 101;
    private static final int MSG_UPDATE_PAIR = 102;
    private static final int MSG_UPDATE_NEARBY = 103;
    /**
     * 避免WIFI频繁变化，导致频繁的刷新UI
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SCAN_RESULT:
                    updateWifiList();
                    break;
                case MSG_UPDATE_PAIR:
                    mPairAdapter.setData(mPairList);
                    break;
                case MSG_UPDATE_NEARBY:
                    mNearbyAdapter.setData(mNearbyList);
                    break;
            }
        }
    };
    /**
     * WIFI 状态的广播接收
     */
    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action: " + action);
            // 开关是关闭的
            if (!viewDataBinding.wlanSwitch.isOn()) {
                Log.i(TAG, "onReceive: switch is off return");
                return;
            }
            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    Log.i(TAG, "onReceive: stateChange: " + mWifiManager.isWifiEnabled());
                    if (mWifiManager.isWifiEnabled()) {
                        mWifiManager.startScan();
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    stopAnimation();
                    boolean isResultUpdate = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        isResultUpdate = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    }
                    Log.i(TAG, "onReceive: isResultUpdate : " + isResultUpdate);
                    Log.i(TAG, "onReceive: resultSize : " + mWifiManager.getScanResults().size());
                    if (isResultUpdate && mWifiManager.getScanResults().size() > 0) {
                        Log.i(TAG, "onReceive: SCAN Result   post wifi result");
                        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_SCAN_WIFI_RESULT);
                    }
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    if (mWifiManager.getScanResults().size() > 0) {
                        Log.i(TAG, "onReceive: Net  post wifi result");
                        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_SCAN_WIFI_RESULT);
                    }
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                    Log.i(TAG, "onReceive: SUPPLICANT_STATE   post wifi result");
                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(supplicantState);
                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    Log.e(TAG, "linkWifiResult:" + linkWifiResult);
                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING && state == NetworkInfo.DetailedState.DISCONNECTED) {
                        dealPwdError();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_wlan_set;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        // 配对的网络
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
                super.setMeasuredDimension(childrenBounds, wSpec,
                        View.MeasureSpec.makeMeasureSpec(333, AT_MOST));
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    Log.i(TAG, "onLayoutChildren: pair layoutChild Exception ");
                }

            }
        };
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        viewDataBinding.wlanPair.setLayoutManager(linearLayoutManager);
        mPairAdapter = new WlanSetAdapter(mPairList, true);
        viewDataBinding.wlanPair.setAdapter(mPairAdapter);
        // 附近的网络
        LinearLayoutManager nearbyLayoutManger = new LinearLayoutManager(getActivity()) {
            @Override
            public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
                super.setMeasuredDimension(childrenBounds, wSpec,
                        View.MeasureSpec.makeMeasureSpec(1000, AT_MOST));
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    Log.i(TAG, "onLayoutChildren:nearby layoutChild Exception ");
                }

            }
        };
        nearbyLayoutManger.setOrientation(RecyclerView.VERTICAL);
        viewDataBinding.wlanNearby.setLayoutManager(nearbyLayoutManger);
        mNearbyAdapter = new WlanSetAdapter(mNearbyList, false);
        viewDataBinding.wlanNearby.setAdapter(mNearbyAdapter);
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        viewDataBinding.wlanSwitch.setOnSwitchStateChangeListener(this::dealWifiOpenOrClose);
        mPairAdapter.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "initAdapterListener: itemClick  position: " + position);
            changeWifi(position);
        });
        mPairAdapter.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.i(TAG, "initAdapterListener: longTime  position: " + position);
            showIgnoreDialog(position);
            return true;
        });

        mNearbyAdapter.setOnItemClickListener((parent, view, position, id) -> connectNewWifi(position));
        // 必须要设置个LongClickListener 否则会闪退
        mNearbyAdapter.setOnItemLongClickListener((parent, view, position, id) -> true);
        initWifiStatusChangeObservable();
    }

    /**
     * WIFI 状态的观察
     */
    private void initWifiStatusChangeObservable() {
        Log.i(TAG, "initWifiStatusChangeObservable: ");
        addDisposable(ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            int msgId = busEvent.getMsgId();
            Log.i(TAG, "initWifiStatusChangeObservable: msgId: " + msgId);
            if (msgId == ModuleSetingEventConstant.MSG_SCAN_WIFI_RESULT) {
                handleScanResult();
            } else if (msgId == ModuleSetingEventConstant.MSG_IGNORE_WIFI) {
                ScanResult scanResult = (ScanResult) busEvent.getMsgObject();
                Log.i(TAG, "initWifiStatusChangeObservable: ignore:" + scanResult);
                //忽视wifi  的更新界面
                if (scanResult != null) {
                    mPairList.remove(scanResult);
                    mPairAdapter.setData(mPairList);
                }
            } else if (msgId == ModuleSetingEventConstant.MSG_INPUT_WIFI_PW) {
                mScanResult = (ScanResult) busEvent.getMsgObject();
                //输入密码连接wifi
            } else if (msgId == CommonConstant.MSG_WIFI_STATUS_CHANGE) {
                boolean wifiStatusIsEnable = mWifiManager.isWifiEnabled();
                viewDataBinding.wlanSwitch.setOn(wifiStatusIsEnable, false);
                Log.i(TAG, "initWifiStatusChangeObservable: " + wifiStatusIsEnable);
                showOrHideWifiList(wifiStatusIsEnable);
            }
        }));
    }

    /**
     * 连接新的WIFI
     *
     * @param position
     */
    private void connectNewWifi(int position) {
        Log.i(TAG, "connectNewWifi: position: " + position);
        if (position >= mNearbyList.size()) {
            // 快速操作 会闪退
            return;
        }
        mScanResult = mNearbyList.get(position);
        String security = ConfigSec.getScanResultSecurity(mScanResult);
        Log.i(TAG, "connectNewWifi: security : " + security);
        if (ConfigSec.isOpenNetwork(security)) {
            WifiConfiguration configuration = WifiSupport.createWifiConfiguration(mScanResult, null);
            WifiSupport.addNetWork(configuration, getContext());
        } else {
            Log.i(TAG, "connectNewWifi: openPwdFragment");
            Bundle bundle = new Bundle();
            bundle.putParcelable(WlanPwdInputFragment.KEY_SCANRESULT, mScanResult);
            WlanPwdInputFragment inputPwdDialog = new WlanPwdInputFragment();
            inputPwdDialog.setArguments(bundle);
            inputPwdDialog.show(getActivity().getSupportFragmentManager(), "WlanInputPwdDialogFragment");
        }
    }

    /**
     * 显示忽略密码的对话框
     *
     * @param position
     */
    private void showIgnoreDialog(int position) {
        ScanResult scanResult = mPairList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("ScanResult", scanResult);
        WlanPwdForgetFragment forgetWlanPwdDialog = new WlanPwdForgetFragment();
        forgetWlanPwdDialog.setArguments(bundle);
        forgetWlanPwdDialog.show(getActivity().getSupportFragmentManager(), "ForgetWlanPwdDialogFragment");
    }

    /**
     * 切换WIFI
     *
     * @param position
     */
    private void changeWifi(int position) {
        Log.i(TAG, "changeWifi: position: " + position);
        mScanResult = mPairList.get(position);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean hasWifiInfo = wifiInfo != null
                && TextUtils.equals(wifiInfo.getSSID(), "\"" + mPairList.get(position).SSID + "\"")
                && TextUtils.equals(wifiInfo.getBSSID(), mPairList.get(position).BSSID);
        if (hasWifiInfo) {
            Log.i(TAG, "changeWifi: hasWifiInfo true   return");
            return;
        }
        ViomiToastUtil.showToastCenter("切换WiFi中");
        WifiConfiguration exitWifiConfig = WifiSupport.isExsits(mScanResult.SSID, getContext());
        Log.i(TAG, "changeWifi: exitWifiConfig : " + exitWifiConfig);
        if (exitWifiConfig != null) {
            boolean addResult = WifiSupport.addNetWork(exitWifiConfig, getContext());
            Log.i(TAG, "changeWifi:  addSSID : " + mScanResult.SSID + " addResult: " + addResult);
        }
    }

    /**
     * 用户操作 打开或者关闭WIFI
     *
     * @param isWifiOpen
     */
    private void dealWifiOpenOrClose(boolean isWifiOpen) {
        Log.i(TAG, "dealWifiOpenOrClose: isWifiOpen: " + isWifiOpen);
        Observable.just(mWifiManager.setWifiEnabled(isWifiOpen))
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .as(RxLifecycleUtil.bindLifecycle(getViewLifecycleOwner()))
                .subscribe(isOpenResult -> {
                    Log.d(TAG, "dealWifiOpenOrClose :" + isOpenResult);
                    if (isOpenResult) {
                        showOrHideWifiList(viewDataBinding.wlanSwitch.isOn());
                        if (!viewDataBinding.wlanSwitch.isOn()) {
                            stopAnimation();
                            SoftApManagerUtils.setWifiApEnabled(getActivity(), false);
                            ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_SOFT_AP_CHANGED, false);
                        } else {
                            startAnimation();
                        }
                    } else {
                        viewDataBinding.wlanSwitch.setOn(!viewDataBinding.wlanSwitch.isOn(), true);
                        SoftApData softApData = SoftApManagerUtils.getSoftApData(getActivity());
                        if (softApData.getWifiOnOff() > 0) {
                            ViomiToastUtil.showToastCenter("请先关闭热点");
                        }
                    }
                }, throwable -> Log.e(TAG, " dealWifiOpenOrClose  throwable:" + throwable.getMessage()));
    }

    @Override
    public void initData() {
        mWifiManager = (WifiManager) ApplicationUtils.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        Log.i(TAG, "initData: " + mWifiManager);
        if (mWifiManager == null) {
            Log.e(TAG, "设备不支持WLAN");
            return;
        }
        // 开关
        boolean isWifiEnable = mWifiManager.isWifiEnabled();
        Log.i(TAG, "initData: wifiEnable : " + isWifiEnable);
        viewDataBinding.wlanSwitch.setOn(isWifiEnable, false);
        showOrHideWifiList(isWifiEnable);
     /*   if (isWifiEnable) {
            Log.i(TAG, "initData: startScan");
            mWifiManager.startScan();
        }*/
        registerWifiReceiver();
        checkWifiStatusInterval();
        scanWifiInterval();
    }

    private void registerWifiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(wifiScanReceiver, intentFilter);
    }

    private void updateWifiList() {
        if (mWifiManager == null) {
            Log.i(TAG, "updateWifiList:  mWifiManager is null return");
            return;
        }
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        Log.i(TAG, "updateWifiList: scanResults  " + scanResults.size());
        WifiInfo connectWifiInfo = mWifiManager.getConnectionInfo();
        Log.d(TAG, "updateWifiList  ConnectionInfo:" + connectWifiInfo.getSSID() + ",IpAddress:" + connectWifiInfo.getIpAddress());
        // 已经配对的网络
        ConfigurationSecurities configureSpec = ConfigurationSecurities.newInstance();
        Map<String, ScanResult> scanResultHashMap = new HashMap<>();
        for (ScanResult scanResult : scanResults) {
            if (TextUtils.isEmpty(scanResult.SSID)) {
                Log.i(TAG, "updateWifiList:  ssid empty  continue ");
                continue;
            }
            // 已经包含的ssid
            if (scanResultHashMap.containsKey(scanResult.SSID)) {
//                Log.i(TAG, "updateWifiList: put ssid: " + scanResult.SSID);
                String security = configureSpec.getScanResultSecurity(scanResult);
//                Log.i(TAG, "updateWifiList: security : " + security);
                WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, scanResult, security);
                //已经包含了，并且 当前wifi
                boolean isConnectWifi = connectWifiInfo != null
                        && TextUtils.equals(connectWifiInfo.getSSID(), "\"" + scanResult.SSID + "\"")
                        && TextUtils.equals(connectWifiInfo.getBSSID(), scanResult.BSSID);
//                Log.i(TAG, "updateWifiList: isConnectWifi : " + isConnectWifi);
                if (config == null || !isConnectWifi) {
                    Log.i(TAG, "updateWifiList:  ssid empty or is not connect   continue ");
                    continue;
                }
            }
//            Log.i(TAG, "updateWifiList: put ssid: " + scanResult.SSID);
            scanResultHashMap.put(scanResult.SSID, scanResult);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "updateWifiList: have no  location permission  return ");
            return;
        }
        List<WifiConfiguration> configurations = mWifiManager.getConfiguredNetworks();
        Log.i(TAG, "updateWifiList:configurations :  " + configurations.size());
        mPairList.clear();
        for (WifiConfiguration wifiConfiguration : configurations) {
            String ssid = wifiConfiguration.SSID.replace("\"", "");
            if (!scanResultHashMap.containsKey(ssid)) {
                Log.i(TAG, "updateWifiList: haven't put Map : " + ssid);
                continue;
            }
            String bssid = wifiConfiguration.BSSID;
//            Log.i(TAG, "updateWifiList: wifiConfiguration.BSSID : " + bssid);
            ScanResult scanResult = scanResultHashMap.get(ssid);
//            Log.i(TAG, "updateWifiList: scanResult.BSSID : " + scanResult.BSSID);
            if (bssid == null || "any".equals(bssid) || scanResult.BSSID.equals(bssid)) {
                final String configSecurity = configureSpec.getWifiConfigurationSecurity(wifiConfiguration);
//                Log.i(TAG, "updateWifiList:configSecurity : BSSID " + configSecurity);
                String scanResultSecurity = configureSpec.getScanResultSecurity(scanResult);
//                Log.i(TAG, "updateWifiList: scanResultSecurity :BSSID " + scanResultSecurity);
                if (scanResultSecurity.equals(configSecurity)) {
                    mPairList.add(scanResult);
                    scanResultHashMap.remove(ssid);
                }
            }
        }
        Collections.sort(mPairList, (lhs, rhs) -> rhs.level - lhs.level);// 信号由强到弱
        String connectWifiName = "", connectWifiAddress = "";
        if (connectWifiInfo != null) {
            connectWifiName = connectWifiInfo.getSSID();
            connectWifiAddress = connectWifiInfo.getBSSID();
        }
        Log.i(TAG, "updateWifiList:mPairList size:  " + mPairList.size());
        for (int i = 0; i < mPairList.size(); i++) { // 已连接放在首位
            if (connectWifiName.equals("\"" + mPairList.get(i).SSID + "\"") && mPairList.get(i).BSSID.equals(connectWifiAddress)) {
                Collections.swap(mPairList, i, 0);
                break;
            }
        }
        Log.i(TAG, "updateWifiList:mPairList size: total  " + mPairList.size());
        mainHandler.sendEmptyMessage(MSG_UPDATE_PAIR);
        // 附近的网络
        Collection<ScanResult> scanResultCollection = scanResultHashMap.values();
        mNearbyList.clear();
        mNearbyList.addAll(scanResultCollection);
        // 信号由强到弱
        Collections.sort(mNearbyList, (lhs, rhs) -> rhs.level - lhs.level);
        // 已连接放在首位
        for (int i = 0; i < mNearbyList.size(); i++) {
            if (connectWifiName.equals("\"" + mNearbyList.get(i).SSID + "\"") && mNearbyList.get(i).BSSID.equals(connectWifiAddress)) {
                Collections.swap(mNearbyList, i, 0);
                break;
            }
        }
        mainHandler.sendEmptyMessageDelayed(MSG_UPDATE_NEARBY, 100);
    }

    /**
     * 显示或者隐藏wifi 信息
     *
     * @param isWifiEnable
     */
    void showOrHideWifiList(boolean isWifiEnable) {
        Log.i(TAG, "refreshView: " + isWifiEnable);
        int visibleInt = isWifiEnable ? View.VISIBLE : View.INVISIBLE;
        viewDataBinding.wlanPairTitle.setVisibility(visibleInt);
        viewDataBinding.wlanPair.setVisibility(visibleInt);
        viewDataBinding.wlanNearbyTip.setVisibility(visibleInt);
        viewDataBinding.wlanNearbyRefresh.setVisibility(visibleInt);
        viewDataBinding.wlanNearby.setVisibility(visibleInt);
        if (!isWifiEnable) {
            mNearbyList.clear();
            mNearbyAdapter.notifyDataSetChanged();
            mPairList.clear();
            mPairAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 每隔8s 扫描一次WIFI状态 刷新WIFI列表
     */
    @SuppressLint("AutoDispose")
    private void scanWifiInterval() {
        Log.i(TAG, "refreshWifiList: ");
        if (scanWifiDisposable != null && !scanWifiDisposable.isDisposed()) {
            scanWifiDisposable.dispose();
        }
        scanWifiDisposable = Observable.interval(8, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    Log.i(TAG, "accept: mWifiManager: " + mWifiManager);
                    if (mWifiManager == null) {
                        return;
                    }
                    boolean isWifiOpen = viewDataBinding.wlanSwitch.isOn();
                    Log.i(TAG, "accept: isWifiOpen: " + isWifiOpen);
                    if (isWifiOpen) {
                        mWifiManager.startScan();
                    }
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * 每隔10s 检查一次WIFI连接状态
     * 如果 没有WIFI连接，尝试连接第一个WIFI
     */
    @SuppressLint("AutoDispose")
    private void checkWifiStatusInterval() {
        if (mWifiManager == null) {
            LogUtils.e(TAG, "该设备不能使用wifi");
            return;
        }
        Log.i(TAG, "checkWifiConnectStatus: check wifi  every 10 second");
        if (checkWifiStatusDisposable != null && !checkWifiStatusDisposable.isDisposed()) {
            checkWifiStatusDisposable.dispose();
        }
        checkWifiStatusDisposable = Observable.interval(10, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    if (mWifiManager == null) {
                        Log.i(TAG, "accept:  mWifiManger is null return ");
                        return;
                    }
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    boolean hasWifiInfo = wifiInfo != null && wifiInfo.getIpAddress() != 0;
                    Log.i(TAG, "checkWifiConnectStatus:hasWifiInfo " + hasWifiInfo);
                    if (hasWifiInfo) {
                        Log.i(TAG, "accept: hasWifi ");
                        return;
                    }
                    if (mPairList != null && mPairList.size() > 0) {
                        mScanResult = mPairList.get(0);
                        Log.e(TAG, "checkWifiConnectStatus:addNetWork" + mScanResult.SSID);
                        WifiConfiguration tempConfig = WifiSupport.isExsits(mScanResult.SSID, getContext());
                        WifiSupport.addNetWork(tempConfig, getContext());
                    }
                }, throwable -> Log.e(TAG, throwable.getMessage()));
    }

    private void startAnimation() {
        viewDataBinding.wlanNearbyRefresh.setVisibility(View.GONE);
        viewDataBinding.wlanNearbyTip.setVisibility(View.VISIBLE);
        viewDataBinding.wlanNearbyRefreshing.setVisibility(View.VISIBLE);
        RotateAnimation rotateAnimation = new RotateAnimation(-360f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setFillAfter(true);
        viewDataBinding.wlanNearbyRefreshing.setAnimation(rotateAnimation);
    }

    private void stopAnimation() {
        if (viewDataBinding.wlanNearbyRefreshing.getAnimation() != null) {
            viewDataBinding.wlanNearbyRefreshing.clearAnimation();
        }
        if (viewDataBinding.wlanSwitch.isOn()) {
            viewDataBinding.wlanNearbyRefresh.setVisibility(View.VISIBLE);
        }
        viewDataBinding.wlanNearbyRefreshing.setVisibility(View.GONE);
    }

    private void handleScanResult() {
        Log.i(TAG, "handleScanResult: send ");
        mainHandler.removeMessages(MSG_UPDATE_SCAN_RESULT);
        mainHandler.sendEmptyMessageDelayed(MSG_UPDATE_SCAN_RESULT, UPDATE_WIFI_LIST_DELAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(wifiScanReceiver);
        }
        if (mWifiManager != null) {
            mWifiManager = null;
        }
        isNewWifiPasswordInput = false;
        checkWifiStatusDisposable.dispose();
        scanWifiDisposable.dispose();
    }

    private void dealPwdError() {
        Log.i(TAG, "dealPwdError: " + mScanResult);
        if (mIsShowing || mScanResult == null) {
            return;
        }
        showPwdErrorFragment();
        mIsShowing = false;
        if (mScanResult == null) {
            return;
        }
        mPairList.remove(mScanResult);
        mPairAdapter.setData(mPairList);
        String scanResultSecurity = ConfigSec.getScanResultSecurity(mScanResult);
        WifiConfiguration configuration = Wifi.getWifiConfiguration(mWifiManager, mScanResult, scanResultSecurity);
        Log.i(TAG, "dealPwdError: configuration" + configuration);
        if (configuration != null) {
            mWifiManager.removeNetwork(configuration.networkId);
            mWifiManager.saveConfiguration();
            mScanResult = null;
            if (mWifiManager != null) {
                mWifiManager.startScan();
            }
        }

    }

    private void showPwdErrorFragment() {
        Log.i(TAG, "showPwdErrorFragment: ");
        Bundle bundle = new Bundle();
        bundle.putParcelable("ScanResult", mScanResult);
        WlanPwdErrorFragment errorPwdDialog = new WlanPwdErrorFragment();
        errorPwdDialog.setOnButtonsClickListener(new BaseDialogFragment.OnButtonsClickListener<Object>() {
            @Override
            public void sureButtonClick(Object o) {
                mIsShowing = false;
            }

            @Override
            public void cancelButtonClick() {
                mIsShowing = false;
            }
        });
        errorPwdDialog.setArguments(bundle);
        errorPwdDialog.show(getActivity().getSupportFragmentManager(), "WlanErrorPwdDialogFragment");
    }

}
