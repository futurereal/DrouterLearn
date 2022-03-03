package com.viomi.modulesetting.ui.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.databinding.SettingMenuHeaderBinding;
import com.viomi.modulesetting.databinding.SettingMenuItemBinding;
import com.viomi.modulesetting.entity.SettingMenuEntity;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 */
public class SettingMenuAdapter extends BaseRecyclerViewAdapter<SettingMenuAdapter.ViewHolder> {
    private static final String TAG = "SettingMenuAdapter";
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_NORMAL = 2;
    List<SettingMenuEntity> menuList;

    public SettingMenuAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutinflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding viewDataBinding = null;
        if (viewType == TYPE_HEADER) {
            viewDataBinding = SettingMenuHeaderBinding.inflate(layoutinflater, parent, false);
        } else if (viewType == TYPE_NORMAL) {
            viewDataBinding = SettingMenuItemBinding.inflate(layoutinflater, parent, false);
        }
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingMenuEntity settingMenuEntity = menuList.get(position);
        int itemType = getItemViewType(position);
        Log.i(TAG, "onBindViewHolder: itemType：" + itemType);
        Log.i(TAG, "onBindViewHolder:settingMenuEntity:  " + settingMenuEntity);
        ViewDataBinding viewDataBinding = holder.getItemViewDataBinding();
        View rootView = viewDataBinding.getRoot();
        if (settingMenuEntity.isSelected()) {
            rootView.setBackgroundResource(R.color.menufragment_select);
        }else{
            rootView.setBackgroundResource(R.color.black);
        }
        // 点击事件的处理
        viewDataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemHolderClick(holder, 500);
            }
        });
        // 头类型  的 iconName 就是 url
        if (itemType == TYPE_HEADER) {
            SettingMenuHeaderBinding settingMenuHeaderBinding = (SettingMenuHeaderBinding) viewDataBinding;
            settingMenuHeaderBinding.menuheaderName.setText(settingMenuEntity.getName());
            Uri headPicUri = Uri.parse(settingMenuEntity.getIconName());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(headPicUri)
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(settingMenuHeaderBinding.menuheaderHead.getController())
                    .setImageRequest(request)
                    .build();
            settingMenuHeaderBinding.menuheaderHead.setController(controller);
            return;
        }
        // 非头类型的 iconName 就是 drawable 里面的 图片名字
        if (itemType == TYPE_NORMAL) {
            SettingMenuItemBinding settingMenuItemBinding = (SettingMenuItemBinding) viewDataBinding;
            settingMenuItemBinding.menuitemTitle.setText(settingMenuEntity.getName());
            int imgResourceId = ApplicationUtils.getContext().getResources().getIdentifier(settingMenuEntity.getIconName(), "drawable",
                    ApplicationUtils.getContext().getPackageName());
            settingMenuItemBinding.menuitemIcon.setImageResource(imgResourceId);
            String status = settingMenuEntity.getStatus();
            if (!TextUtils.isEmpty(status)) {
                settingMenuItemBinding.menuitemStatus.setText(status);
            } else {
                // 如果不设置为空，点击item 的时候可能会错乱，净水器主题设置有显示矿物质状态的问题
                settingMenuItemBinding.menuitemStatus.setText("");
            }
            // 如果是竖屏显示窗口
            if (ScreenUtils.isPortrait()) {
                settingMenuItemBinding.menuitemArrow.setVisibility(View.VISIBLE);
            }
        }
    }


    public void updateDate(List<SettingMenuEntity> menuList) {
        if (menuList == null) {
            return;
        }
        Log.d(TAG, "updateDate: list size : " + menuList.size());
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType: ");
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "getItemId: ");
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = menuList == null ? 0 : menuList.size();
        Log.i(TAG, "getItemCount: itemCount: " + itemCount);
        return itemCount;
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
