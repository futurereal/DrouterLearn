package com.viomi.waterpurifier.edison.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viomi.ovensocommon.view.CommonSettingSeekBar;
import com.viomi.waterpurifier.edison.databinding.ItemSeekbarSetBinding;
import com.viomi.waterpurifier.edison.entity.SeekBarSetEntity;

import java.util.ArrayList;

public class SeekBarSetAdapter extends RecyclerView.Adapter<SeekBarSetAdapter.Holder> {
    private static final String TAG = "SeekBarSetAdapter";
    ArrayList<SeekBarSetEntity> seekBarSetEntityList;
    private SeekBarValueListener seekBarValueListener;

    public SeekBarSetAdapter(ArrayList<SeekBarSetEntity> seekBarSetEntityList) {
        this.seekBarSetEntityList = seekBarSetEntityList;
        Log.i(TAG, "SeekBarSetAdapter: size: " + seekBarSetEntityList.size());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSeekbarSetBinding seekBarSetBinding = ItemSeekbarSetBinding.inflate(layoutInflater, parent, false);
        return new Holder(seekBarSetBinding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SeekBarSetEntity seekBarSetEntity = seekBarSetEntityList.get(position);
        holder.bindData(seekBarSetEntity);
    }

    @Override
    public int getItemCount() {
        return seekBarSetEntityList == null ? 0 : seekBarSetEntityList.size();
    }

    public void setSeekBarValueListener(SeekBarValueListener seekBarValueListener) {
        this.seekBarValueListener = seekBarValueListener;
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ItemSeekbarSetBinding seekBarSetBinding;
        private final SeekBarSetAdapter seekBarSetAdapter;

        public Holder(ItemSeekbarSetBinding seekBarSetBinding, SeekBarSetAdapter seekBarSetAdapter) {
            super(seekBarSetBinding.getRoot());
            this.seekBarSetBinding = seekBarSetBinding;
            this.seekBarSetAdapter = seekBarSetAdapter;
        }

        public void bindData(SeekBarSetEntity seekBarSetEntity) {
            Log.i(TAG, "bindData: seekbarSetEntity: " + seekBarSetEntity);
            seekBarSetBinding.seekbarsetBar.setStep(seekBarSetEntity.getStepVlue());
            seekBarSetBinding.seekbarsetBar.setShowValue(true);
            seekBarSetBinding.seekbarsetName.setText(seekBarSetEntity.getName());
            seekBarSetBinding.seekbarsetBar.setMinValue(seekBarSetEntity.getMinValue());
            seekBarSetBinding.seekbarsetBar.setMaxValue(seekBarSetEntity.getMaxValue());
            seekBarSetBinding.seekbarsetBar.setProgressValue(seekBarSetEntity.getCurrentValue());
            seekBarSetBinding.seekbarsetBar.setOnValueChangeListener(new CommonSettingSeekBar.OnValueChangeListener() {
                @Override
                public void onSelectedPercentage(int selectedPercentage) {
                    Log.i(TAG, "onSelectedPercentage: ");
                    int position = seekBarSetEntityList.indexOf(seekBarSetEntity);
                    Log.i(TAG, "onSelectedPercentage: position: " + position + " selectPerventValue : " + selectedPercentage);
                    seekBarSetAdapter.seekBarValueListener.onValueChange(position, selectedPercentage);
                }

                @Override
                public void onTouch() {
                    Log.i(TAG, "onTouch: ");
                }

                @Override
                public boolean beforeTouch() {
                    return false;
                }

                @Override
                public void onDragging(int selectedPercentage) {
                    Log.i(TAG, "onDragging: ");
                }
            });
        }
    }

    public interface SeekBarValueListener {
        void onValueChange(int position, int currentValue);
    }
}
