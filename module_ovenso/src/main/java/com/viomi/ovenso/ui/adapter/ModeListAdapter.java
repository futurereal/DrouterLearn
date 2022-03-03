package com.viomi.ovenso.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.microwave.databinding.ItemModeBinding;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;
import com.viomi.ovensocommon.db.CookParamEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/11/10.
 */
public class ModeListAdapter extends BaseRecyclerViewAdapter<ModeListAdapter.Holder> {
    private static final String TAG = "ModeListAdapter";
    private List<ModeTypeEntity> modeTypeEntityList;
    private ItemModeBinding modeDataBinding;

    public ModeListAdapter() {
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        modeDataBinding = ItemModeBinding.inflate(layoutInflater, parent, false);
        return new Holder(modeDataBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ModeTypeEntity modeTypeEntity = modeTypeEntityList.get(position);
        Log.i(TAG, "onBindViewHolder: position : " + position + "  modeTypeEntity: " + modeTypeEntity);
        holder.bindData(modeTypeEntity);
    }

    @Override
    public int getItemCount() {
        return modeTypeEntityList == null ? 0 : modeTypeEntityList.size();
    }

    public void setModeList(List<ModeTypeEntity> modeTypeEntityList) {
        this.modeTypeEntityList = modeTypeEntityList;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private final ItemModeBinding modeDataBinding;

        Holder(ItemModeBinding modeDataBinding, ModeListAdapter adapter) {
            super(modeDataBinding.getRoot());
            this.modeDataBinding = modeDataBinding;
            modeDataBinding.getRoot().setOnClickListener(v -> adapter.onItemHolderClick(this, 250));
        }

        private void bindData(ModeTypeEntity modeTypeEntity) {
            modeDataBinding.modeName.setText(modeTypeEntity.getName());
            ArrayList<CookParamEntity> cookParamList = modeTypeEntity.getCookParamEntityList();
            float totleTime = 0;
            for (CookParamEntity cookParamEntity : cookParamList) {
                totleTime = totleTime + cookParamEntity.getDefineTime();
            }

            String time = OvenUtil.getFloatString(totleTime);
            modeDataBinding.modeTotalTime.setText(time);
            int imgResourceId = OvenApplication.getContext().getResources().getIdentifier(modeTypeEntity.getResIdBg(),
                    "drawable", ApplicationUtils.getContext().getPackageName());
            modeDataBinding.modeBg.setImageResource(imgResourceId);
            int imgTipResourceId = OvenApplication.getContext().getResources().getIdentifier(modeTypeEntity.getResIdTip(),
                    "drawable", ApplicationUtils.getContext().getPackageName());
            modeDataBinding.modeTip.setImageResource(imgTipResourceId);
            modeDataBinding.timeUnite.setText("分钟");
           /* if (cookParamEntity != null && cookParamEntity.getModeId() == ModeTypeEnum.T_YR.modeId) {
                modeDataBinding.timeUnite.setText("℃");
                modeDataBinding.modeTotalTime.setText(cookParamEntity.getDefineFirepower() + "");
            }*/
        }

    }
}
