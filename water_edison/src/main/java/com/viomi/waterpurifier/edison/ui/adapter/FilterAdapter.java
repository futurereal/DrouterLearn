package com.viomi.waterpurifier.edison.ui.adapter;

import static com.viomi.waterpurifier.edison.WaterConstant.FILTER_ERROR_MARGIN;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.common.ApplicationUtils;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.ItemFilterBinding;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.ui.fragment.FilterResetFragment;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Holder> {
    private static final String TAG = "SeekBarSetAdapter";
    List<FilterEntity> filterEntityArrayList;

    public FilterAdapter(List<FilterEntity> filterEntityArrayList) {
        this.filterEntityArrayList = filterEntityArrayList;
        Log.i(TAG, "SeekBarSetAdapter: size: " + filterEntityArrayList.size());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFilterBinding itemFilterBinding = ItemFilterBinding.inflate(layoutInflater, parent, false);
        return new Holder(itemFilterBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return filterEntityArrayList == null ? 0 : filterEntityArrayList.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        private final ItemFilterBinding itemFilterBinding;

        public Holder(ItemFilterBinding mineralBinding, FilterAdapter mineralAdapter) {
            super(mineralBinding.getRoot());
            this.itemFilterBinding = mineralBinding;
        }

        public void bindData(int position) {
            Log.i(TAG, "bindData: position: " + position);
            FilterEntity filterEntity = filterEntityArrayList.get(position);
            itemFilterBinding.filteritemName.setText(filterEntity.getFilterName());
            int lifePercent = filterEntity.getLefePercent();
            int filterLifeColorId = R.color.color_99;
            if (lifePercent < FILTER_ERROR_MARGIN) {
                filterLifeColorId = R.color.color_orange;
            }
            itemFilterBinding.filteritemLefe.setTextColor(filterLifeColorId);
            String left = lifePercent + ApplicationUtils.getContext().getString(R.string.percent_unit);
            itemFilterBinding.filteritemLefe.setText(left);
            itemFilterBinding.getRoot().getRootView().setOnClickListener(view -> {
                Log.i(TAG, "bindData: startResetFragment");
                FilterResetFragment filterResetFragment = new FilterResetFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(WaterConstant.KEY_FILTERENTIY, filterEntity);
                filterResetFragment.setArguments(bundle);
                FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
                filterResetFragment.show(fragmentActivity.getSupportFragmentManager(), FilterResetFragment.class.getSimpleName());
            });
        }
    }

}
