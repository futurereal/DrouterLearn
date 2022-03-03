package com.viomi.ovenso.ui.activity;

import android.util.Log;
import android.view.View;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.PropertyUtil;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityOvenAgingBinding;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.util.TimeUtil;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.OvenActionEnum;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2021/1/21.
 * Description:老化测试界面
 * so7 老化界面   3分钟高火微波，15分钟原汽蒸100℃，5分钟空气炸180℃。
 * so6 老化界面   20min/100℃    3min/180℃    2min/180℃
 */
@Route(path = ViomiRouterConstant.OVENSO_AGING)
public class OvenAgingActivity extends BaseActivity<ActivityOvenAgingBinding> {
    private static final String TAG = "OvenAgingActivity";
    // 是否是微蒸烤 so7
    private static final boolean IS_OVENSO_MICRO = true;
    private int currentStep = 1;
    OvenWorkStatusEnum mStatusType;
    private int currentStatus = OvenWorkStatusEnum.IDLE.value;
    // so6 蒸烤箱模式名字
    private final String[] soSixModeNames = new String[]{ApplicationUtils.getContext().getString(R.string.aging_ovenso_modeone),
            ApplicationUtils.getContext().getString(R.string.aging_ovenso_modetwo),
            ApplicationUtils.getContext().getString(R.string.aging_ovenso_modethree)};
    // so7 微蒸烤模式名字
    private final String[] soSevenModeNames = new String[]{ApplicationUtils.getContext().getString(R.string.aging_ovenmicro_modeone),
            ApplicationUtils.getContext().getString(R.string.aging_ovenmicro_modetwo),
            ApplicationUtils.getContext().getString(R.string.aging_ovenmicro_modethree)};
    private final int modeIdStream = 26;
    private final int modeIdHotBake = 17;
    private final int modeIdFanBake = 21;

    //
    private final int modeIdMicro = OvenConstants.MODE_ID_MICRWAVE;
    private final int modeBake = 24;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_oven_aging;
    }

    @Override
    protected void initView() {
        updateAllProperty();
    }

    @Override
    protected void initData() {
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            updateAllProperty();
            switch (viomiRxBusEvent.getMsgId()) {//灯开关图标、故障图标
                case OvenBusEventConstants.MSG_COOK_STATUSCHANGE:
                    int statusEnumValue = (int) viomiRxBusEvent.getMsgObject();
                    int faultValue = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, 0);
                    Log.i(TAG, "initData: faultValue: " + faultValue);
                    if (faultValue != 0) {
                        stopAgingAndShowResult(false);
                        return;
                    }
                    Log.i(TAG, "initData: propertyEntity:" + statusEnumValue);
                    currentStatus = statusEnumValue;
                    // 防止连续收到多个结束的返回
                    if (currentStatus != OvenWorkStatusEnum.COMPLETED.value) {
                        Log.i(TAG, "initData: not complete return  or same status ");
                        currentStatus = PropertyUtil.status;
                        return;
                    }
                    currentStatus = PropertyUtil.status;
                    Log.i(TAG, "initData: currentStep: " + currentStep);
                    //继续下一阶段
                    if (currentStep < 3) {
                        currentStep++;
                        startAging();
                        return;
                    }
                    stopAgingAndShowResult(true);
                    break;
                case CommonConstant.MSG_DOWNWRITE_SUCCESS:
                    // 非烹饪的 写入指令成功 不调用这个逻辑
                    OvenSerialManager.getInstance().doStandardAction(OvenActionEnum.ACTION_START);
                    break;
            }
        });
        addDisposable(disposable);
        initListener();
    }

    @Override
    protected void initListener() {
        viewDataBinding.ovenaginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewDataBinding.ovenaginBack.getRootView().getAlpha() < 1.0f) {
                    ViomiToastUtil.showToastCenter(getString(R.string.aging_finish_tip));
                } else {
                    finish();
                }
            }
        });
        viewDataBinding.includeStart.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                currentStep = 1;
                startAging();
            }
        });

        viewDataBinding.includeOver.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAgingAndShowResult(false);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        viewDataBinding.ovenagingLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changeState = !viewDataBinding.ovenagingLight.isSelected();
                OvenSerialManager.getInstance().setLightProperty(changeState);
            }
        });

    }

    private void showStatus() {
        Log.i(TAG, "showStatus: currentStatus: " + currentStatus);
        Log.i(TAG, "showStatus: PropertyUtil.status: " + PropertyUtil.status);
        if (currentStatus == PropertyUtil.status) {
            Log.i(TAG, "showStatus: sameproperty  return");
            return;
        }
        for (OvenWorkStatusEnum type : OvenWorkStatusEnum.values()) {
            if (PropertyUtil.status == type.value) {
                mStatusType = type;
                viewDataBinding.imgRotate1.rotate(mStatusType == OvenWorkStatusEnum.WORKING);
                viewDataBinding.imgRotate2.rotate(mStatusType == OvenWorkStatusEnum.WORKING);
                viewDataBinding.ovenaginBack.setAlpha(mStatusType == OvenWorkStatusEnum.IDLE ? 1.0f : 0.4f);
                break;
            }
        }
        String workStatusTitle = getString(R.string.ovenaging_workstatus_title);
        viewDataBinding.tvWorkStatus.setText(workStatusTitle + mStatusType.name);
    }


    private void startAging() {
        Log.i(TAG, "startAging: step: " + currentStep);
        String modeTitle = getString(R.string.ovenaging_current_mode);
        if (IS_OVENSO_MICRO) {
            viewDataBinding.currentMode.setText(modeTitle + soSevenModeNames[currentStep - 1]);
        } else {
            viewDataBinding.currentMode.setText(modeTitle + soSixModeNames[currentStep - 1]);
        }
        List<PropertyEntity> propList = new ArrayList<>();
        if (currentStep == 1) {//第一阶段 纯蒸
            if (IS_OVENSO_MICRO) {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, 26),
                        new PropertyEntity(OvenPropEnum.TIMEZ.siid, OvenPropEnum.TIMEZ.piid, 20),
                        new PropertyEntity(OvenPropEnum.TEMPZ.siid, OvenPropEnum.TEMPZ.piid, 100));
            } else {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, modeIdStream),
                        new PropertyEntity(OvenPropEnum.TIMEZ.siid, OvenPropEnum.TIMEZ.piid, 20),
                        new PropertyEntity(OvenPropEnum.TEMPZ.siid, OvenPropEnum.TEMPZ.piid, 100));
            }
//            7 老化界面   3分钟高火微波，15分钟原汽蒸100℃，5分钟空气炸180℃。
        } else if (currentStep == 2) {//热风烤
            if (IS_OVENSO_MICRO) {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, 34),
                        new PropertyEntity(OvenPropEnum.MICRO_TIME.siid, OvenPropEnum.MICRO_TIME.piid, 60),
                        new PropertyEntity(OvenPropEnum.MICRO_LEVEL.siid, OvenPropEnum.MICRO_LEVEL.piid, 3));

            } else {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, modeIdHotBake),
                        new PropertyEntity(OvenPropEnum.TIMEK.siid, OvenPropEnum.TIMEK.piid, 3),
                        new PropertyEntity(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 180));
            }
        } else if (currentStep == 3) {//风扇烤
            if (IS_OVENSO_MICRO) {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, 24),
                        new PropertyEntity(OvenPropEnum.TIMEK.siid, OvenPropEnum.TIMEK.piid, 4),
                        new PropertyEntity(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 180));

            } else {
                propList = Arrays.asList(
                        new PropertyEntity(OvenPropEnum.DISHID.siid, OvenPropEnum.DISHID.piid, 0),
                        new PropertyEntity(OvenPropEnum.MODE.siid, OvenPropEnum.MODE.piid, modeIdFanBake),
                        new PropertyEntity(OvenPropEnum.TIMEK.siid, OvenPropEnum.TIMEK.piid, 3),
                        new PropertyEntity(OvenPropEnum.TEMPK.siid, OvenPropEnum.TEMPK.piid, 180));
            }
        }
        OvenSerialManager.getInstance().writePropertyList(propList);
        int code = 0;
        if (code == 0) {
            viewDataBinding.includeStart.getRoot().setVisibility(View.GONE);
            viewDataBinding.includeOver.getRoot().setVisibility(View.VISIBLE);
            viewDataBinding.tvStep.setText(String.valueOf(currentStep));
        } else {
            ViomiToastUtil.showToastCenter("启动失败");
        }
    }


    private void stopAgingAndShowResult(boolean agingResult) {
        if (viewDataBinding.tvResult.getRootView().getVisibility() != View.VISIBLE) {
            viewDataBinding.tvResult.setVisibility(View.VISIBLE);
        }
        int agingResultTip = agingResult ? R.string.ovenaging_result_success : R.string.ovenaging_result_failt;
        viewDataBinding.tvResult.setText(agingResultTip);
        Log.i(TAG, "stopAging: ");
        OvenSerialManager.getInstance().doStandardAction(OvenActionEnum.ACTION_OVER);
    }


    private void updateAllProperty() {
        // 更新灯的开关
        viewDataBinding.ovenagingLight.setSelected(PropertyUtil.light);
        // 更新当前温度
        String temperatureTitle = getString(R.string.ovenaging_currenttemp_title);
        String temperatureUnit = getString(R.string.temperature_single);
        viewDataBinding.tvNowTemp.setText(temperatureTitle + PropertyUtil.currentTemperature + temperatureUnit);
        // 更新状态
        showStatus();
        // 更新剩余时间时间
        String leftTimeTitle = getString(R.string.ovenaging_left_time);
        viewDataBinding.leftTime.setText(leftTimeTitle + TimeUtil.getTimeMMSS(PropertyUtil.lefttime));
        showPropView();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showPropView() {
        viewDataBinding.tvPropView.setVisibility(View.VISIBLE);
        String propertys = PropertyUtil.getTestProperty();
        Log.d(TAG, "the props:" + propertys);
        viewDataBinding.tvPropView.setText(propertys);

    }
}

