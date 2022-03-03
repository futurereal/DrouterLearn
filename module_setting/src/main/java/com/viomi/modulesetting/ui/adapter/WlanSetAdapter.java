package com.viomi.modulesetting.ui.adapter;

import static android.content.Context.WIFI_SERVICE;
import static com.viomi.modulesetting.utils.wifi.Wifi.ConfigSec;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.WlanSetItemBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: wifi设置页列表适配器
 */
public class WlanSetAdapter extends BaseRecyclerViewAdapter<WlanSetAdapter.WlanSetViewHolder> {
    private static final String TAG = "WlanSetAdapter";
    private static final int CLICK_DELAY = 500;
    private static final int CLICK_LONG_DELAY = 2000;
    private final int[] mStatusIcon = new int[]{R.drawable.wifi_signal_low, R.drawable.wifi_signal_middle, R.drawable.wifi_signal_high};
    private final WifiManager mWifiManager;
    private List<ScanResult> scanResults;
    private final boolean isPari;

    public WlanSetAdapter(List<ScanResult> scanResults, boolean isPair) {
        Log.i(TAG, "WlanSetAdapter: ");
        this.mWifiManager = (WifiManager) ApplicationUtils.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        this.isPari = isPair;
        this.scanResults = scanResults;
    }

    public void setData(List<ScanResult> scanResults) {
        Log.i(TAG, "setData: scanResults : " + scanResults.size());
        this.scanResults = scanResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WlanSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WlanSetItemBinding wlanSetItemBinding = WlanSetItemBinding.inflate(layoutInflater, parent, false);
        return new WlanSetViewHolder(wlanSetItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WlanSetViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: " + position);
        if (scanResults == null || scanResults.size() == 0 || position >= scanResults.size()) {
            Log.i(TAG, "onBindViewHolder: scanResults is wrong: " + position);
            return;
        }
        ScanResult scanResult = scanResults.get(position);
        holder.bindData(scanResult);
    }

    @Override
    public int getItemCount() {
        int size = scanResults == null ? 0 : scanResults.size();
        Log.i(TAG, "getItemCount: " + size);
        return size;
    }

    class WlanSetViewHolder extends RecyclerView.ViewHolder {
        private final WlanSetItemBinding wlanSetItemBinding;

        public WlanSetViewHolder(WlanSetItemBinding wlanSetItemBinding) {
            super(wlanSetItemBinding.getRoot());
            this.wlanSetItemBinding = wlanSetItemBinding;
        }

        public void bindData(ScanResult scanResult) {
            String wlanRate = "";
            if (scanResult.frequency > 5000) {
                wlanRate = "(5GHz)";
            } else {
                wlanRate = "(2.4GHz)";
            }
            wlanSetItemBinding.wlansetName.setText(scanResult.SSID + wlanRate);
            Log.i(TAG, "bindData: mWifiManger:" + mWifiManager);
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            boolean hasWifiInfo = wifiInfo != null
                    && TextUtils.equals(wifiInfo.getSSID(), "\"" + scanResult.SSID + "\"")
                    && TextUtils.equals(wifiInfo.getBSSID(), scanResult.BSSID);
            if (hasWifiInfo) {
                if (wifiInfo.getIpAddress() != 0) {
                    wlanSetItemBinding.wlansetStatus.setText(R.string.status_connected);
                } else {
                    wlanSetItemBinding.wlansetStatus.setText(R.string.status_connecting);
                }
                wlanSetItemBinding.wlansetStatus.setVisibility(View.VISIBLE);
                wlanSetItemBinding.wlansetStatus.setTextColor(Color.WHITE);
            } else {
                wlanSetItemBinding.wlansetStatus.setVisibility(View.GONE);
                wlanSetItemBinding.wlansetStatus.setTextColor(0xFFFFFFFF);
            }
            if (isPari) {
                if (hasWifiInfo && wifiInfo.getIpAddress() != 0) {
                    wlanSetItemBinding.wlansetConnectStatus.setImageResource(R.drawable.wifi_selected);
                } else {
                    wlanSetItemBinding.wlansetConnectStatus.setImageResource(R.drawable.wifi_unselected);
                }
            } else if (ConfigSec.isOpenNetwork(ConfigSec.getScanResultSecurity(scanResult))) {
                wlanSetItemBinding.wlansetConnectStatus.setVisibility(View.INVISIBLE);
            } else {
                wlanSetItemBinding.wlansetConnectStatus.setVisibility(View.VISIBLE);
                wlanSetItemBinding.wlansetConnectStatus.setImageResource(R.drawable.wifi_lock);
            }
            wlanSetItemBinding.wlansetSignalTip.setImageResource(mStatusIcon[WifiManager.calculateSignalLevel(scanResult.level, mStatusIcon.length)]);
            wlanSetItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemHolderClick(WlanSetViewHolder.this, CLICK_DELAY);
                }
            });

            wlanSetItemBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemHolderLongClick(WlanSetViewHolder.this);
                    return true;
                }
            });
            Log.i(TAG, "bindData: end: ");
        }
    }

}
