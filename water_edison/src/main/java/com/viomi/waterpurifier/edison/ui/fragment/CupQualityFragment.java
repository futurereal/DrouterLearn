package com.viomi.waterpurifier.edison.ui.fragment;

import android.util.Log;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.FragmentCupQualityBinding;
import com.viomi.waterpurifier.edison.entity.SeekBarSetEntity;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.adapter.SeekBarSetAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

@Route(path = ViomiRouterConstant.WATER_FRAGMENT_CUP)
public class CupQualityFragment extends BaseDialogFragment<FragmentCupQualityBinding> {
    private final static String TAG = "CupQualityFragment";
    private ArrayList<SeekBarSetEntity> cupQualityEntityList;
    int[] waterCupQualityDefaultValues = new int[]{WaterConstant.DEFAULT_SMALL_CUP_FLOW, WaterConstant.DEFAULT_MID_CUP_FLOW, WaterConstant.DEFAULT_BIG_CUP_FLOW};
    WaterPropEnum[] waterPropEnums = new WaterPropEnum[]{WaterPropEnum.SMALL_CUP_FLOW, WaterPropEnum.MIDDLE_CUP_FLOW, WaterPropEnum.BIG_CUP_FLOW};
    private SeekBarSetAdapter seekBarSetAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_cup_quality;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initChildView: ");
        cupQualityEntityList = getSeekBarDataList();
        seekBarSetAdapter = new SeekBarSetAdapter(cupQualityEntityList);
        viewDataBinding.cupqualityRecyclerview.setAdapter(seekBarSetAdapter);
        seekBarSetAdapter.notifyDataSetChanged();
    }

    private ArrayList<SeekBarSetEntity> getSeekBarDataList() {
        int[] cupNamesId = new int[]{R.string.cup_small, R.string.cup_middle, R.string.cup_big};
        String cupUnite = getResources().getString(R.string.unit_flow);
        int[] cupMinValues = new int[]{100, 360, 660};
        int[] cupMaxValues = new int[]{350, 650, 1000};
        int cupStep = 10;
        int cupTotal = cupNamesId.length;
        ArrayList<SeekBarSetEntity> cupSeekBarList = new ArrayList<>(cupTotal);
        for (int i = 0; i < cupTotal; i++) {
            SeekBarSetEntity seekBarSetEntity = new SeekBarSetEntity();
            String cupName = getResources().getString(cupNamesId[i]);
            seekBarSetEntity.setName(cupName + "/" + cupUnite);
            seekBarSetEntity.setMinValue(cupMinValues[i]);
            seekBarSetEntity.setMaxValue(cupMaxValues[i]);
            seekBarSetEntity.setStepVlue(cupStep);
            int currentValue = (int) PropertyPreferenceManager.getInstance().getProperty(waterPropEnums[i].siid, waterPropEnums[i].piid, waterCupQualityDefaultValues[i]);
            seekBarSetEntity.setCurrentValue(currentValue);
            Log.i(TAG, "getSeekBarDataList: " + seekBarSetEntity);
            cupSeekBarList.add(seekBarSetEntity);
        }
        return cupSeekBarList;
    }

    @Override
    public void initListener() {
        seekBarSetAdapter.setSeekBarValueListener((position, currentValue) -> {
            WaterPropEnum currentWaterProp = waterPropEnums[position];
            Log.i(TAG, "onValueChange: position: " + position + " currentValue: " + currentValue);
            PropertyEntity propertyEntity = new PropertyEntity(currentWaterProp.siid, currentWaterProp.piid, currentValue);
            WaterSerialManager.getInstance().writeProperty(propertyEntity);
        });
        viewDataBinding.cupqualityReset.setOnClickListener(view -> {
            List<PropertyEntity> propertyEntityList = new ArrayList<>();
            for (int i = 0; i < waterPropEnums.length; i++) {
                PropertyEntity cupPropertyEntity = new PropertyEntity(waterPropEnums[i].siid, waterPropEnums[i].piid, waterCupQualityDefaultValues[i]);
                propertyEntityList.add(cupPropertyEntity);
                cupQualityEntityList.get(i).setCurrentValue(waterCupQualityDefaultValues[i]);
            }
            Log.i(TAG, "initListener: propertyEntityList: " + propertyEntityList.size());
            WaterSerialManager.getInstance().writePropertyList(propertyEntityList);
        });
        Disposable cupDisposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Log.i(TAG, "accept: msgId: " + msgId);
            if (msgId != WaterBusEvent.MSG_PROPERTY_CUPFLOW) {
                return;
            }
            PropertyEntity propertyEntity = (PropertyEntity) viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "accept: propertyEntity: " + propertyEntity);
            int cupIndex = propertyEntity.getPid() - WaterPropEnum.SMALL_CUP_FLOW.piid;
            cupQualityEntityList.get(cupIndex).setCurrentValue((Integer) propertyEntity.getContent());
            seekBarSetAdapter.notifyDataSetChanged();
        });
        addDispose(cupDisposable);
    }
}
