package com.viomi.ovenso.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ResourceUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.bean.ThemeItemEntity;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 */
public class OvenThemeAdapter extends BaseRecyclerViewAdapter<OvenThemeAdapter.ViewHolder> {

    private List<ThemeItemEntity> items = new ArrayList<>();

    public OvenThemeAdapter(List<ThemeItemEntity> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThemeItemEntity entity = items.get(position);
        int id = ResourceUtils.getDrawableIdByName(entity.getThemeDrawablethumbnail());
        holder.thumbnail.setActualImageResource(id);
        holder.name.setText(entity.getName());
        holder.content.setSelected(entity.isSelect());
        holder.thumbnail.setSelected(entity.isSelect());
        holder.select.setVisibility(entity.isSelect() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> onItemHolderClick(holder, 500));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView thumbnail;
        private final TextView name;
        private final ImageView select;
        private final ConstraintLayout content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.theme_thumbnail);
            name = itemView.findViewById(R.id.theme_name);
            select = itemView.findViewById(R.id.theme_select);
            content = itemView.findViewById(R.id.content);
        }
    }

}
