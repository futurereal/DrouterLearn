package com.viomi.modulesetting.ui.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.viomi.modulesetting.databinding.ItemAboutSofewareBinding;
import com.viomi.modulesetting.entity.AboutSofewareEntity;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 */
public class AboutSoftwareAdapter extends BaseRecyclerViewAdapter<AboutSoftwareAdapter.ViewHolder> {
    public static final int CONTENT_MAX_EMS = 10;
    private static final String TAG = "SettingMenuAdapter";
    private static final String EMPTY_PATH = "empty";
    List<AboutSofewareEntity> aboutWareList;

    public AboutSoftwareAdapter(List<AboutSofewareEntity> aboutWareList) {
        Log.i(TAG, "SettingMenuAdapter: " + (aboutWareList == null ? 0 : aboutWareList.size()));
        this.aboutWareList = aboutWareList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        ItemAboutSofewareBinding viewDataBinding = ItemAboutSofewareBinding.inflate(layoutinflater, parent, false);
        if (ScreenUtils.isPortrait()) {
            viewDataBinding.aboutwareContent.setMaxEms(CONTENT_MAX_EMS);
        }
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AboutSofewareEntity aboutSofewareEntity = aboutWareList.get(position);
        holder.bindView(this, aboutSofewareEntity);
    }

    public void updateData(List<AboutSofewareEntity> aboutWareList) {
        Log.i(TAG, "updateData: " + aboutWareList.size());
        this.aboutWareList = aboutWareList;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = aboutWareList == null ? 0 : aboutWareList.size();
        Log.i(TAG, "getItemCount: itemCount: " + itemCount);
        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAboutSofewareBinding itemViewDataBinding;

        public ViewHolder(@NonNull ItemAboutSofewareBinding itemViewDataBinding) {
            super(itemViewDataBinding.getRoot());
            this.itemViewDataBinding = itemViewDataBinding;
        }

        public void bindView(AboutSoftwareAdapter aboutSoftwareAdapter, AboutSofewareEntity aboutSofewareEntity) {
            itemViewDataBinding.aboutwareTitle.setText(aboutSofewareEntity.getTitle());
            String routPath = aboutSofewareEntity.getRoutPath();
            Log.i(TAG, "bindView: routPath: " + routPath + " " + EMPTY_PATH);
            if (TextUtils.equals(routPath, EMPTY_PATH)) {
                itemViewDataBinding.aboutwareJumptip.setVisibility(View.GONE);
            } else {
                itemViewDataBinding.aboutwareJumptip.setVisibility(View.VISIBLE);
                itemViewDataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutSoftwareAdapter.onItemHolderClick(ViewHolder.this, 500);
                    }
                });
            }
            String conent = aboutSofewareEntity.getContent();
            if (!TextUtils.equals(conent, EMPTY_PATH)) {
                itemViewDataBinding.aboutwareContent.setText(conent);
            } else {
                itemViewDataBinding.aboutwareContent.setText("");
            }
            if (aboutSofewareEntity.isError()) {
                itemViewDataBinding.aboutwareContent.setTextColor(Color.RED);
            }
        }
    }

}
