package com.viomi.waterpurifier.edison.ui.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.config.WaterQualityEnum;
import com.viomi.waterpurifier.edison.databinding.FragmentChangeConfirmBinding;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author by xinqi on 2021-07-07.
 * @describe
 */
public class ChangeConfirmFragment extends BaseDialogFragment<FragmentChangeConfirmBinding> {

    private static final String TAG = "ChangeConfirmFragment";
    public static final String KEY_CURRENT_QUALITY = "keyCurrentQuality";
    private int currentWaterQulityVaule;

    public ChangeConfirmFragment() {
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_change_confirm;
    }


    @Override
    protected void initView() {
        currentWaterQulityVaule = getArguments().getInt(KEY_CURRENT_QUALITY);
        Log.i(TAG, "initView: " + currentWaterQulityVaule);

    }

    @Override
    protected void initListener() {
        viewDataBinding.changeconfirmSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWaterMineral();
            }
        });
        viewDataBinding.changeconfirmClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        //当前水质 和目标水质
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            int msgId = viomiRxBusEvent.getMsgId();
            Object msgObj = viomiRxBusEvent.getMsgObject();
            Log.i(TAG, "initListener: " + viomiRxBusEvent);
            if (msgId == CommonConstant.MSG_PROPERTY_CHANGE) {
                PropertyEntity propertyEntity = (PropertyEntity) msgObj;
                showWaterChangeDialog(propertyEntity);
                return;
            }
        });
        addDispose(disposable);
    }

    @Override
    public void onStart() {
        portWidth = 640;
        portHeight = 384;
        super.onStart();
    }

    /**
     * 切换水质
     */
    private void changeWaterMineral() {
        Log.i(TAG, "changeWaterMineral: ");
        // 开启水质切换动画
        List<PropertyEntity> propList = new ArrayList<>();
        Log.i(TAG, "changeWaterMineral: " + (currentWaterQulityVaule + 1));
        WaterQualityEnum targetWaterQulityEnum = WaterQualityEnum.values()[(currentWaterQulityVaule + 1) % 2];
        if (targetWaterQulityEnum == WaterQualityEnum.MINERAL_WATER) {
            int level = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.MINERAL_TYPE.siid,
                    WaterPropEnum.MINERAL_TYPE.piid, WaterConstant.DEFAULT_MINERAL_TYPE);
            PropertyEntity propMineral = new PropertyEntity(WaterPropEnum.MINERAL_TYPE.siid, WaterPropEnum.MINERAL_TYPE.piid, level);
            propList.add(propMineral);
        }
        PropertyEntity propQuality = new PropertyEntity(WaterPropEnum.WATER_QUALITY.siid, WaterPropEnum.WATER_QUALITY.piid, targetWaterQulityEnum.ordinal());
        propList.add(propQuality);
        Log.d(TAG, "changeWaterMineral: propList size: " + propList.size());
        WaterSerialManager.getInstance().writePropertyList(propList);
    }

    private void showWaterChangeDialog(PropertyEntity propertyEntity) {
        Log.i(TAG, "showWaterChangeDialog: " + propertyEntity);
        if (propertyEntity.getSid() != WaterPropEnum.WATER_QUALITY.siid || propertyEntity.getPid() != WaterPropEnum.WATER_QUALITY.piid) {
            Log.i(TAG, "showWaterChangeDialog: not show return");
            return;
        }
        Log.i(TAG, "showWaterChangeDialog:  show");
        WaterChangeFragment waterQualityDialog = new WaterChangeFragment();
        Bundle bundle = new Bundle();
        WaterQualityEnum currentWaterQulity = WaterQualityEnum.values()[currentWaterQulityVaule];
        WaterQualityEnum targetWaterQuality = WaterQualityEnum.values()[(int) propertyEntity.getContent()];
        bundle.putInt(WaterChangeFragment.KEY_TARGET_TYPE, targetWaterQuality.nameStrId);
        bundle.putInt(WaterChangeFragment.KEY_CURRENT_TYPE, currentWaterQulity.nameStrId);
        waterQualityDialog.setArguments(bundle);
        FragmentActivity fragmentActivity = (FragmentActivity) ActivityUtils.getTopActivity();
        waterQualityDialog.show(fragmentActivity.getSupportFragmentManager(), "waterQualityChangeDialogFragment");
        dismissAllowingStateLoss();
    }

}
