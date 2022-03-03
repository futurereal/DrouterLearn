package com.viomi.waterpurifier.edison.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovensocommon.view.CommonSettingSeekBar;
import com.viomi.waterpurifier.edison.databinding.ItemMineralBinding;
import com.viomi.waterpurifier.edison.databinding.ItemSeekbarSetBinding;
import com.viomi.waterpurifier.edison.entity.MineralEntity;
import com.viomi.waterpurifier.edison.entity.SeekBarSetEntity;

import java.util.ArrayList;

public class MineralAdapter extends RecyclerView.Adapter<MineralAdapter.Holder> {
    private static final String TAG = "SeekBarSetAdapter";
    ArrayList<MineralEntity> mineralEntityArrayList;
    private MineralSelectListener mineralSelectListener;

    public MineralAdapter(ArrayList<MineralEntity> mineralEntityArrayList) {
        this.mineralEntityArrayList = mineralEntityArrayList;
        Log.i(TAG, "SeekBarSetAdapter: size: " + mineralEntityArrayList.size());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMineralBinding itemMineralBinding = ItemMineralBinding.inflate(layoutInflater, parent, false);
        return new Holder(itemMineralBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mineralEntityArrayList == null ? 0 : mineralEntityArrayList.size();
    }

    public void setMineralSelectListener(MineralSelectListener mineralSelectListener) {
        this.mineralSelectListener = mineralSelectListener;
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemMineralBinding mineralBinding;
        private final MineralAdapter mineralAdapter;

        public Holder(ItemMineralBinding mineralBinding, MineralAdapter mineralAdapter) {
            super(mineralBinding.getRoot());
            this.mineralBinding = mineralBinding;
            this.mineralAdapter = mineralAdapter;
        }

        public void bindData(int position) {
            Log.i(TAG, "bindData: position: " + position);
            MineralEntity mineralEntity = mineralEntityArrayList.get(position);
            mineralBinding.mineralitemName.setText(mineralEntity.getName());
            mineralBinding.mineralitemDescription.setText(mineralEntity.getContent());
            Log.i(TAG, "bindData: "+mineralEntity.isSelect());
            mineralBinding.mineralitemSelectstatus.setSelected(mineralEntity.isSelect());
            mineralBinding.mineralitemSelectstatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isFoucuse = mineralBinding.mineralitemSelectstatus.isFocused();
                    Log.i(TAG, "onClick: isFoucus: " + isFoucuse);
                    if (isFoucuse) {
                        return;
                    }
                    mineralSelectListener.mineralSelect(position);
                }
            });
        }
    }

    public interface MineralSelectListener {
        void mineralSelect(int position);
    }
}
