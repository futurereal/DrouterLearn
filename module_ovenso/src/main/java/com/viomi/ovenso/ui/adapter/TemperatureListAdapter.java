package com.viomi.ovenso.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.TemperatureEntity;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ItemTargetTemperatureBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by Ljh on 2020/11/12
 * Description:
 */
public class TemperatureListAdapter extends BaseRecyclerViewAdapter<TemperatureListAdapter.Holder> {
    private static final String TAG = "RecipeListAdapter";
    private List<TemperatureEntity> temperatureEntityList;

    public TemperatureListAdapter(List<TemperatureEntity> temperatureEntityList) {
        this.temperatureEntityList = temperatureEntityList;
    }

    public void update(List<TemperatureEntity> temperatureEntityList) {
        this.temperatureEntityList = temperatureEntityList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTargetTemperatureBinding itemTargetTemperatureBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_target_temperature, null, false);
        return new Holder(itemTargetTemperatureBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return temperatureEntityList == null ? 0 : temperatureEntityList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private ItemTargetTemperatureBinding itemTargetTemperatureBinding = null;

        Holder(ItemTargetTemperatureBinding itemTargetTemperatureBinding) {
            super(itemTargetTemperatureBinding.getRoot());
            this.itemTargetTemperatureBinding = itemTargetTemperatureBinding;
        }

        private void bind(int position) {
            TemperatureEntity temperatureEntity = temperatureEntityList.get(position);
            int currentTemperature = temperatureEntity.getTemperature();
            Log.i(TAG, "bind: currentTemperature: " + currentTemperature);
            if (currentTemperature == 0) {
                Log.i(TAG, "bind: no need show temprature return  ");
                itemTargetTemperatureBinding.targetTemperatureUnite.setVisibility(View.GONE);
                itemTargetTemperatureBinding.targetTemperatureArrow.setVisibility(View.GONE);
                return;
            }
            String temperatureName = String.valueOf(currentTemperature);
            if (currentTemperature < OvenConstants.MICRO_LEVEL_MARGIN && currentTemperature > 0) {
                List<String> microNameList = OvenUtil.getMicroPowerNames();
                temperatureName = microNameList.get(currentTemperature - 1);
                itemTargetTemperatureBinding.targetTemperatureUnite.setVisibility(View.GONE);
            }
            String temperature = String.valueOf(temperatureName);
            itemTargetTemperatureBinding.targetTemperature.setText(temperature);
            Log.i(TAG, "bind: " + temperatureEntity.isSlected());
            if (temperatureEntity.isSlected()) {
                itemTargetTemperatureBinding.targetTemperature.setAlpha(1f);
                itemTargetTemperatureBinding.targetTemperatureUnite.setAlpha(1f);
                itemTargetTemperatureBinding.targetTemperatureArrow.setAlpha(1f);
            } else {
                itemTargetTemperatureBinding.targetTemperature.setAlpha(0.5f);
                itemTargetTemperatureBinding.targetTemperatureUnite.setAlpha(0.5f);
                itemTargetTemperatureBinding.targetTemperatureArrow.setAlpha(0.5f);
            }
            Log.i(TAG, "bind: temperature: " + temperature);
            // 如果不是数字，单位隐藏
            if (!OvenUtil.isNumeric(temperature)) {
                itemTargetTemperatureBinding.targetTemperatureUnite.setVisibility(View.GONE);
            }
            if (position == temperatureEntityList.size() - 1) {
                itemTargetTemperatureBinding.targetTemperatureArrow.setVisibility(View.GONE);
            }
        }
    }
}
