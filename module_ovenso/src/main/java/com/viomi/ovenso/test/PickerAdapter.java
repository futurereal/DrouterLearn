package com.viomi.ovenso.test;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovenso.microwave.databinding.ItemPickerBinding;
import com.viomi.ovenso.microwave.databinding.ItemPickerSelectBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 */
public class PickerAdapter extends BaseRecyclerViewAdapter<PickerAdapter.ViewHolder> {
    private static final String TAG = "PickerAdapter";
    private static final int TYPE_CENTER = 1;
    private static final int TYPE_NORMAL = 2;
    List<PickerEntity> pickerEntityList;
    private int centerPosition = 3;

    public PickerAdapter(List<PickerEntity> pickerEntityList) {
        Log.i(TAG, "SettingMenuAdapter: " + (pickerEntityList == null ? 0 : pickerEntityList.size()));
        this.pickerEntityList = pickerEntityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding viewDataBinding = null;
        if (viewType == TYPE_CENTER) {
            viewDataBinding = ItemPickerSelectBinding.inflate(layoutinflater, parent, false);
        } else if (viewType == TYPE_NORMAL) {
            viewDataBinding = ItemPickerBinding.inflate(layoutinflater, parent, false);
        }
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        PickerEntity pickerEntity = pickerEntityList.get(position % 20);
        int itemType = getItemViewType(position);
        ViewDataBinding viewDataBinding = holder.getItemViewDataBinding();
        if (itemType == TYPE_NORMAL) {
            ItemPickerBinding itemPickerBinding = (ItemPickerBinding) viewDataBinding;
            itemPickerBinding.itemPicker.setText(pickerEntity.getName());
        }
        if (itemType == TYPE_CENTER) {
            ItemPickerSelectBinding itemPickerSelectBinding = (ItemPickerSelectBinding) viewDataBinding;
            itemPickerSelectBinding.itempickerSelectContent.setText(pickerEntity.getName());
        }

    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCenterPosition(int centerPosition) {
        Log.i(TAG, "setCenterPosition: " + centerPosition);
        this.centerPosition = centerPosition;
    }

    @Override
    public int getItemCount() {
        int itemCount = pickerEntityList == null ? 0 : pickerEntityList.size();
        Log.i(TAG, "getItemCount: itemCount: " + itemCount);
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemViewType(int position) {
        int pickerType = TYPE_NORMAL;
        if (position == centerPosition) {
            pickerType = TYPE_CENTER;
        }
        Log.i(TAG, "getItemViewType: " + pickerType);
        return pickerType;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding itemViewDataBinding;

        public ViewHolder(@NonNull ViewDataBinding itemViewDataBinding) {
            super(itemViewDataBinding.getRoot());
            this.itemViewDataBinding = itemViewDataBinding;
        }

        public ViewDataBinding getItemViewDataBinding() {
            return itemViewDataBinding;
        }
    }

}
