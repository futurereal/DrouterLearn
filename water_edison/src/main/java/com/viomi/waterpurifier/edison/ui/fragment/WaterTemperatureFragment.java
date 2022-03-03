package com.viomi.waterpurifier.edison.ui.fragment;


import android.annotation.SuppressLint;
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
import com.viomi.waterpurifier.edison.databinding.FragmentWaterTemperatureBinding;
import com.viomi.waterpurifier.edison.entity.SeekBarSetEntity;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;
import com.viomi.waterpurifier.edison.ui.adapter.SeekBarSetAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author admin
 */
@Route(path = ViomiRouterConstant.WATER_FRAGMENT_TEMP)
public class WaterTemperatureFragment extends BaseDialogFragment<FragmentWaterTemperatureBinding> {
    private final static String TAG = "WaterTempFragment";
    private ArrayList<SeekBarSetEntity> waterTempSeekBarList;
    int[] waterTemperatureDefaultValues = new int[]{WaterConstant.DEFAULT_WARM_WATER_TEMP, WaterConstant.DEFAULT_HOT_WATER_TEMP, WaterConstant.DEFAULT_BOILING_WATER_TEMP};
    WaterPropEnum[] waterPropEnums = new WaterPropEnum[]{WaterPropEnum.TEMP_WARM, WaterPropEnum.TEMP_HOT, WaterPropEnum.TEMP_BOILING};
    private SeekBarSetAdapter seekBarSetAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_water_temperature;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void initView() {
        Log.i(TAG, "initChildView: ");
        waterTempSeekBarList = getSeekBarDataList();
        seekBarSetAdapter = new SeekBarSetAdapter(waterTempSeekBarList);
        viewDataBinding.temperatureRecyclerview.setAdapter(seekBarSetAdapter);
    }

    private ArrayList<SeekBarSetEntity> getSeekBarDataList() {
        int[] tempNamesId = new int[]{R.string.water_warm, R.string.water_hot, R.string.water_boiling};
        String tempUnite = getResources().getString(R.string.unit_temperature);
        int[] tempMinValues = new int[]{40, 51, 90};
        int[] tempMaxValues = new int[]{50, 90, 100};
        int tempStep = 1;
        int tempTotal = tempNamesId.length;
        ArrayList<SeekBarSetEntity> tempSeekBarList = new ArrayList<>(tempTotal);
        for (int i = 0; i < tempTotal; i++) {
            SeekBarSetEntity seekBarSetEntity = new SeekBarSetEntity();
            String tempName = getResources().getString(tempNamesId[i]);
            seekBarSetEntity.setName(tempName + "/" + tempUnite);
            seekBarSetEntity.setMinValue(tempMinValues[i]);
            seekBarSetEntity.setMaxValue(tempMaxValues[i]);
            seekBarSetEntity.setStepVlue(tempStep);
            int currentValue = (int) PropertyPreferenceManager.getInstance().getProperty(waterPropEnums[i].siid, waterPropEnums[i].piid, waterTemperatureDefaultValues[i]);
            seekBarSetEntity.setCurrentValue(currentValue);
            Log.i(TAG, "getSeekBarDataList: " + seekBarSetEntity);
            tempSeekBarList.add(seekBarSetEntity);
        }
        return tempSeekBarList;
    }

    @Override
    public void initListener() {
        seekBarSetAdapter.setSeekBarValueListener((position, currentValue) -> {
            WaterPropEnum currentWaterProp = waterPropEnums[position];
            Log.i(TAG, "onValueChange: position: " + position + " currentValue: " + currentValue);
            WaterSerialManager.getInstance().writeProperty(new PropertyEntity(currentWaterProp.siid, currentWaterProp.piid, currentValue));
        });
        viewDataBinding.temperatureReset.setOnClickListener(view -> {
            List<PropertyEntity> propertyEntityList = new ArrayList<>();
            for (int i = 0; i < waterPropEnums.length; i++) {
                PropertyEntity tempPropertyEntity = new PropertyEntity(waterPropEnums[i].siid, waterPropEnums[i].piid, waterTemperatureDefaultValues[i]);
                propertyEntityList.add(tempPropertyEntity);
                waterTempSeekBarList.get(i).setCurrentValue(waterTemperatureDefaultValues[i]);
            }
            Log.i(TAG, "initListener: writePropertyList: " + propertyEntityList.size());
            WaterSerialManager.getInstance().writePropertyList(propertyEntityList);
        });

        Disposable temperatureDisposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            if (msgId != WaterBusEvent.MSG_PROPERTY_TEMPERATURE) {
                return;
            }
            PropertyEntity propertyEntity = (PropertyEntity) viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "accept: propertyEntity: " + propertyEntity);
            int tempIndex = propertyEntity.getPid() - WaterPropEnum.TEMP_WARM.piid;
            waterTempSeekBarList.get(tempIndex).setCurrentValue((Integer) propertyEntity.getContent());
            seekBarSetAdapter.notifyDataSetChanged();
        });
        addDispose(temperatureDisposable);
    }
}
