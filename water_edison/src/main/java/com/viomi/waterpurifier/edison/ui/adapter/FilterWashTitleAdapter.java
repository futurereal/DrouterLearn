package com.viomi.waterpurifier.edison.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.waterpurifier.edison.databinding.ItemFilterwashSteptitleBinding;
import com.viomi.waterpurifier.edison.entity.FilterWashTitleEntity;

import java.util.List;

/**
 * Created by Ljh on 2020/11/10.
 */
public class FilterWashTitleAdapter extends BaseRecyclerViewAdapter<FilterWashTitleAdapter.Holder> {
    private static final String TAG = "FilterWashTitleAdapter";
    private final List<FilterWashTitleEntity> filterWashTitleEntityList;
    private ItemFilterwashSteptitleBinding itemFilterwashSteptitleBinding;

    public FilterWashTitleAdapter(List<FilterWashTitleEntity> filterWashTitleEntityList) {
        this.filterWashTitleEntityList = filterWashTitleEntityList;
        Log.i(TAG, "FilterWashTitleAdapter: filterWashTitleEntityList size: " + filterWashTitleEntityList.size());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        itemFilterwashSteptitleBinding = ItemFilterwashSteptitleBinding.inflate(layoutInflater, parent, false);
        return new Holder(itemFilterwashSteptitleBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return filterWashTitleEntityList == null ? 0 : filterWashTitleEntityList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        Holder(ItemFilterwashSteptitleBinding itemFilterwashSteptitleBinding) {
            super(itemFilterwashSteptitleBinding.getRoot());
        }

        public void bindData(int position) {
            Log.i(TAG, "bindData: position: " + position);
            if (position == filterWashTitleEntityList.size() - 1) {
                itemFilterwashSteptitleBinding.washitemStepLine.setVisibility(View.GONE);
            }
            if (position == 0) {
                ConstraintLayout.LayoutParams layoutParametor = (ConstraintLayout.LayoutParams) itemFilterwashSteptitleBinding.washitemStepName.getLayoutParams();
                layoutParametor.leftMargin = 50;
                itemFilterwashSteptitleBinding.washitemStepName.setLayoutParams(layoutParametor);
            }
            FilterWashTitleEntity filterTitleEntity = filterWashTitleEntityList.get(position);
            itemFilterwashSteptitleBinding.washitemStepName.setText(filterTitleEntity.getTitleName());
            int stepStaus = filterTitleEntity.getStepStatus();
            Log.i(TAG, "bindData: stepStaus " + stepStaus);
            if (stepStaus == FilterWashTitleEntity.STATUS_NORMAL) {
                itemFilterwashSteptitleBinding.washitemStepLine.setFocusable(false);
                itemFilterwashSteptitleBinding.washitemStepIcon.setFocusable(false);
            } else if (stepStaus == FilterWashTitleEntity.STATUS_COMING) {
                itemFilterwashSteptitleBinding.washitemStepLine.setFocusable(true);
                itemFilterwashSteptitleBinding.washitemStepIcon.setFocusable(true);
            } else if (stepStaus == FilterWashTitleEntity.STATUS_FINISH) {
                itemFilterwashSteptitleBinding.washitemStepLine.setFocusable(true);
                itemFilterwashSteptitleBinding.washitemStepIcon.setClickable(true);
            }
        }
    }
}
