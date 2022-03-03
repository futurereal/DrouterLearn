package com.viomi.ovensocommon;

import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 全局 RecyclerView 适配器
 * Created by William on 2018/1/22.
 */
public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected final String TAG = this.getClass().getSimpleName();
    protected AdapterView.OnItemClickListener onItemClickListener;
    protected AdapterView.OnItemLongClickListener onItemLongClickListener;
    // 两次点击间隔不能少于 1000 毫秒
    private long lastClickTime;
    protected int posFlag = -1;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    protected void onItemHolderClick(RecyclerView.ViewHolder itemHolder, int minTime) {
        if (onItemClickListener != null) {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= minTime) {
                posFlag = itemHolder.getAdapterPosition();
                onItemClickListener.onItemClick(null, itemHolder.itemView,
                        itemHolder.getAdapterPosition(), itemHolder.getItemId());
                notifyDataSetChanged();
            }
            lastClickTime = curClickTime;
        } else {
            throw new IllegalStateException("Please call setOnItemClickListener method set the click event listeners");
        }
    }

    protected boolean onItemHolderLongClick(RecyclerView.ViewHolder itemHolder) {
        if (onItemLongClickListener != null) {
            boolean result =  onItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
            notifyDataSetChanged();
            return result;
        } else {
            throw new IllegalStateException("Please call setOnItemLongClickListener method set the click event listeners");
        }
    }
}
