package com.viomi.ovenso.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovenso.bean.ModeDetailTitleEntity;
import com.viomi.ovenso.microwave.databinding.ItemModedetailTitleBinding;
import com.viomi.ovensocommon.BaseRecyclerViewAdapter;

import java.util.List;

import retrofit2.http.PATCH;

/**
 * Created by Ljh on 2020/11/10.
 */
public class ModeDetailTitleAdapter extends BaseRecyclerViewAdapter<ModeDetailTitleAdapter.Holder> {
    private static final String TAG = "ModeListAdapter";
    private static final int MAX_ITEM_SIZE = 3;
    private List<ModeDetailTitleEntity> modeDetailTitleEntities;
    private ItemModedetailTitleBinding itemModedetailTitleBinding;

    public ModeDetailTitleAdapter() {
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        itemModedetailTitleBinding = ItemModedetailTitleBinding.inflate(layoutInflater, parent, false);
        return new Holder(itemModedetailTitleBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ModeDetailTitleEntity modeDetailTitleEntity = modeDetailTitleEntities.get(position);
        holder.bindData(modeDetailTitleEntity);
    }

    @Override
    public int getItemCount() {
        return modeDetailTitleEntities == null ? 0 : modeDetailTitleEntities.size();
    }

    public void setTitleList(List<ModeDetailTitleEntity> modeDetailTitleEntities) {
        this.modeDetailTitleEntities = modeDetailTitleEntities;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemModedetailTitleBinding itemModedetailTitleBinding;

        Holder(ItemModedetailTitleBinding itemModedetailTitleBinding, ModeDetailTitleAdapter adapter) {
            super(itemModedetailTitleBinding.getRoot());
            this.itemModedetailTitleBinding = itemModedetailTitleBinding;
            itemModedetailTitleBinding.getRoot().setOnClickListener(v -> adapter.onItemHolderClick(this, 250));
        }

        public void bindData(ModeDetailTitleEntity modeDetailTitleEntity) {
            itemModedetailTitleBinding.modedetailStepname.setText(modeDetailTitleEntity.getTitleName());
            itemModedetailTitleBinding.modedetailSteptip.setText(String.valueOf(modeDetailTitleEntity.getIndex()));
            itemModedetailTitleBinding.modedetailStepSelecttip.setSelected(modeDetailTitleEntity.isSelect());
            if (modeDetailTitleEntity.getIndex() == modeDetailTitleEntities.size()) {
                itemModedetailTitleBinding.modedetailLineStep.setVisibility(View.GONE);
            }
            if (getItemCount() > MAX_ITEM_SIZE) {
                ViewGroup.LayoutParams params = itemModedetailTitleBinding.modedetailLineStep.getLayoutParams();
                params.width = params.width / (getItemCount() - 1);
                itemModedetailTitleBinding.modedetailLineStep.setLayoutParams(params);
            }
        }
    }
}
