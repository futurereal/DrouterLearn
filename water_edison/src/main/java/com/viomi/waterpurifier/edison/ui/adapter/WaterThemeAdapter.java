package com.viomi.waterpurifier.edison.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.waterpurifier.edison.databinding.ItemWaterthemeBinding;
import com.viomi.waterpurifier.edison.entity.WaterThemeEntity;

import java.util.List;

/**
 *
 * @author admin
 * @date:2021/10/21
 */
public class WaterThemeAdapter extends BaseRecyclerViewAdapter<WaterThemeAdapter.ViewHolder> {
    private static final String TAG = "WaterThemeAdapter";
    List<WaterThemeEntity> waterThemeEntityList;

    public WaterThemeAdapter(List<WaterThemeEntity> waterThemeEntityList) {
        this.waterThemeEntityList = waterThemeEntityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemWaterthemeBinding itemWaterthemeBinding = ItemWaterthemeBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemWaterthemeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterThemeEntity waterThemeEntity = waterThemeEntityList.get(position);
        holder.bindView(waterThemeEntity);
        ViewDataBinding viewDataBinding = holder.getItemViewDataBinding();
        View rootView = viewDataBinding.getRoot();
        // 点击事件的处理
        rootView.setOnClickListener(v -> onItemHolderClick(holder, 500));
    }

    @Override
    public int getItemCount() {
        int itemCount = waterThemeEntityList == null ? 0 : waterThemeEntityList.size();
        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWaterthemeBinding itemWaterthemeBinding;


        public ViewHolder(@NonNull ItemWaterthemeBinding itemWaterthemeBinding) {
            super(itemWaterthemeBinding.getRoot());
            this.itemWaterthemeBinding = itemWaterthemeBinding;
        }

        public void bindView(WaterThemeEntity waterThemeEntity) {
            Log.i(TAG, "bindView: " + waterThemeEntity.isSelected());
            itemWaterthemeBinding.waterthemeItemTitle.setText(waterThemeEntity.getNameId());
            itemWaterthemeBinding.waterthemeItemType.setImageResource(waterThemeEntity.getResourceId());
            itemWaterthemeBinding.waterthemeItemSelect.setSelected(waterThemeEntity.isSelected());
        }

        public ViewDataBinding getItemViewDataBinding() {
            return itemWaterthemeBinding;
        }

    }
}
