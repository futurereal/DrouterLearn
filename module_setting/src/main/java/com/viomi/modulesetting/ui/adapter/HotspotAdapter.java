package com.viomi.modulesetting.ui.adapter;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.modulesetting.R;
import com.viomi.modulesetting.utils.softap.HotSpotDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 附近设备的adapter
 * @data:2021/8/6
 */

public class HotspotAdapter extends RecyclerView.Adapter<HotSpotHolder> {
    private static final String TAG = "HotspotAdapter";
    private final int ITEM_TYPE_NORMAL = 1;
    private final int ITEM_TYPE_HEADER = 0;
    View header;
    List<HotSpotDevice> data = new ArrayList();


    @NonNull
    @Override
    public HotSpotHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new HotSpotHolder(header);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_hotspot_item, null);
            return new HotSpotHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HotSpotHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: position: " + position);
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_HEADER) {
            Log.i(TAG, "onBindViewHolder: isHead return ");
            return;
        }
        // 防止数组越界或者空指针
        if (position > data.size() || data.get(position - 1) == null) {
            Log.i(TAG, "onBindViewHolder: postion bean is null or index out return " + position);
            return;
        }
        HotSpotDevice device = data.get(position - 1);
        Log.i(TAG, "onBindViewHolder: device : " + device.getDevName());
        String deviceName = device.getDevName();
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = device.getDevMac();
        }
        holder.tvName.setText(deviceName);
        holder.vDivider.setVisibility(position == data.size() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        int count = data.size();
        if (header != null) {
            count++;
        }
        return count;
    }

    public void setHeader(View headerView) {
        this.header = headerView;
        notifyItemInserted(0);
    }

    public void setData(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

}

class HotSpotHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    View vDivider;

    public HotSpotHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.hotspot_name);
        vDivider = itemView.findViewById(R.id.hotspot_divider);
    }
}