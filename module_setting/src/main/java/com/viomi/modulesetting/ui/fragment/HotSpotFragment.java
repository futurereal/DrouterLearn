package com.viomi.modulesetting.ui.fragment;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.FragmentHotspotBinding;
import com.viomi.modulesetting.databinding.HotspotRecyclerviewHeaderBinding;
import com.viomi.modulesetting.ui.adapter.HotspotAdapter;
import com.viomi.modulesetting.utils.softap.HotSpotDevice;
import com.viomi.modulesetting.utils.softap.HotSpotPresenter;
import com.viomi.modulesetting.utils.softap.SoftApContract;
import com.viomi.ovensocommon.BindingBaseFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;

import java.util.List;

/**
 * @author hailang
 * @date 2020/9/3 000316:17
 * @desc 设备热点
 */
@Route(path = ViomiRouterConstant.SETTING_FRAGMENT_HOTSPOT)
public class HotSpotFragment extends BindingBaseFragment<FragmentHotspotBinding> implements SoftApContract.SoftApViewI {
    private static final String TAG = "HotSpotFragment";
    HotspotAdapter hotspotAdapter;
    ContentObserver observer;
    private HotspotRecyclerviewHeaderBinding hotspotHeaderBinding;
    private SoftApContract.HotSpotPresenterI hotSpotPresenter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_hotspot;
    }

    @Override
    protected void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        hotspotHeaderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.hotspot_recyclerview_header, null, false);
        viewDataBinding.hotspotRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        hotspotAdapter = new HotspotAdapter();
        hotspotAdapter.setHeader(hotspotHeaderBinding.getRoot());
        viewDataBinding.hotspotRecycleview.setAdapter(hotspotAdapter);
        initListener();
    }

    @Override
    public void initListener() {
        hotspotHeaderBinding.hotspotSwitch.setOnSwitchChangeListener(() -> {
            //未联网提示
            boolean networkEnable = NetworkUtils.isConnected();
            if (!networkEnable) {
                String msgTip = getString(R.string.setting_hotspot_unavailable);
                ViomiToastUtil.showToastCenter(msgTip);
                return true;
            }
            return false;
        });
        hotspotHeaderBinding.hotspotSwitch.setOnSwitchStateChangeListener(isOn -> {
            if (hotSpotPresenter != null) {
                hotSpotPresenter.setOpen(isOn);
            }
            if (isOn) {
                //开启，监听
                registerContentObserver();
            } else {
                //关闭
                unregisterContentObserver();
            }
            hotspotHeaderBinding.hotspotTip.setVisibility(isOn ? View.GONE : View.VISIBLE);
            hotspotHeaderBinding.refreshGroup.setVisibility(isOn ? View.VISIBLE : View.GONE);
            ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_SOFT_AP_CHANGED, isOn);
        });
        hotspotHeaderBinding.hotspotDeviceRefresh.setOnClickListener(v -> hotSpotPresenter.notifyDeviceList());

    }

    @Override
    public void initData() {
        hotSpotPresenter = new HotSpotPresenter(getContext(), this);
        registerContentObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (hotSpotPresenter != null) {
            hotSpotPresenter.onDestroy();
            hotSpotPresenter = null;
        }
        unregisterContentObserver();
    }

    @Override
    public void initSoftAp(boolean isOpen) {
        Log.d(TAG, "initSoftAp: isOpen: " + isOpen);
        hotspotHeaderBinding.hotspotSwitch.setOn(isOpen);
        hotspotHeaderBinding.hotspotTip.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        hotspotHeaderBinding.refreshGroup.setVisibility(isOpen ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCurrentHertz(int hertz) {

    }

    @Override
    public void notifyItemChanged(List<HotSpotDevice> list) {
        Log.i(TAG, "notifyItemChanged: list : " + list);
        if (list != null) {
            Log.d(TAG, "notifyItemChanged: list: " + list);
            hotspotAdapter.setData(list);
        }
    }

    private void unregisterContentObserver() {
        if (observer != null) {
            getActivity().getContentResolver().unregisterContentObserver(observer);
            observer = null;
        }
    }

    private void registerContentObserver() {
        Log.i(TAG, "registerContentObserver: ");
        if (observer == null) {
            observer = new Observer(new Handler(Looper.getMainLooper()));
            try {
                getActivity().getContentResolver().registerContentObserver(
                        Uri.parse("content://com.viomi.device.provider/clientinfo"), true, observer);
            } catch (Throwable e) {
                Log.d(TAG, "registerContentObserver: throwable : " + e.getMessage());
            }
        }
    }

    private final class Observer extends ContentObserver {

        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.i(TAG, "onChange: ");
            if (hotSpotPresenter != null) {
                hotSpotPresenter.notifyDeviceList();
            }
        }
    }
}