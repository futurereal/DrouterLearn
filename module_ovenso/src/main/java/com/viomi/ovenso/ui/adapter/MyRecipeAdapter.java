package com.viomi.ovenso.ui.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ItemMyRecipeBinding;
import com.viomi.ovenso.util.Base64Util;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.ovensocommon.db.CookParamEntity;
import com.viomi.ovensocommon.utils.CommonStringUtils;

import java.util.List;

/**
 * Created by Ljh on 2020/11/12
 * Description:
 */
public class MyRecipeAdapter extends BaseRecyclerViewAdapter<MyRecipeAdapter.Holder> {
    private static final String TAG = "MyRecipeAdapter";
    public static final String ADD = "+";
    private List<ModeTypeEntity> recipeModeList;

    public MyRecipeAdapter() {
    }

    public void setRecipeModeList(List<ModeTypeEntity> recipeModeList) {
        this.recipeModeList = recipeModeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // parent 不能为null  否则 宽高会无效
        ItemMyRecipeBinding itemMyRecipeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_my_recipe, parent, false);
        return new Holder(itemMyRecipeBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return recipeModeList == null ? 0 : recipeModeList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemMyRecipeBinding itemMyRecipeBinding;

        Holder(ItemMyRecipeBinding itemMyRecipeBinding, MyRecipeAdapter adapter) {
            super(itemMyRecipeBinding.getRoot());
            this.itemMyRecipeBinding = itemMyRecipeBinding;
            itemMyRecipeBinding.getRoot().setOnClickListener(v -> adapter.onItemHolderClick(this, 250));
        }

        private void bind(int position) {
            // 防止数组越界和一些空的情况下
            if (position > recipeModeList.size() - 1 || recipeModeList.get(position) == null) {
                Log.i(TAG, "bind: bean is null  or  indexout  return " + position);
                return;
            }
            ModeTypeEntity modeTypeEntity = recipeModeList.get(position);
            Log.i(TAG, "bind: update: " + modeTypeEntity);
            String modeName = Base64Util.decode(modeTypeEntity.getName());
            Log.i(TAG, "bind: modeName: " + modeName);
            modeName = CommonStringUtils.getFixCharString(modeName, 12);
            itemMyRecipeBinding.modeName.setText(modeName);
            List<CookParamEntity> cookParamEntities = modeTypeEntity.getCookParamEntityList();
            Log.i(TAG, "bind: cookParamEntity size: " + cookParamEntities.size());
            float totalTime = 0;
            String cookPower = "";
            for (CookParamEntity cookParamEntity : cookParamEntities) {
                float defineTime = cookParamEntity.getDefineTime();
                if (TextUtils.equals(cookParamEntity.getModeType(), OvenConstants.MODE_TYPE_MICROWAVE)) {
                    defineTime = defineTime / 60;
                }
                totalTime = totalTime + defineTime;
                int definePower = cookParamEntity.getDefineFirepower();
                String definePowerName = OvenUtil.getPowerName(definePower);
                cookPower = cookPower + definePowerName + ADD;
            }
            String totalTimeStr = OvenUtil.getFloatString(totalTime);
            String powerAndTime = cookPower.substring(0, cookPower.length() - 1) + OvenConstants.SPLITER + totalTimeStr + OvenConstants.COOKTIME_UNIT_NAME;
            itemMyRecipeBinding.modePowerTime.setText(powerAndTime);
            int imgTipResourceId = OvenApplication.getContext().getResources().getIdentifier(modeTypeEntity.getResIdTip(),
                    "drawable", ApplicationUtils.getContext().getPackageName());
            itemMyRecipeBinding.modeTip.setImageResource(imgTipResourceId);

            int recipeBg = 0;
            Log.i(TAG, "bind: positon: " + position);
            if (position % 4 == 0) {
                recipeBg = R.drawable.gradient_recipe0_bg;
            } else if (position % 4 == 1) {
                recipeBg = R.drawable.gradient_recipe1_bg;
            } else if (position % 4 == 2) {
                recipeBg = R.drawable.gradient_recipe2_bg;
            } else if (position % 4 == 3) {
                recipeBg = R.drawable.gradient_recipe3_bg;
            }
            Log.i(TAG, "bind: recipeBg ： " + recipeBg);
            itemMyRecipeBinding.modeBg.setBackgroundResource(recipeBg);
        }

    }
}
