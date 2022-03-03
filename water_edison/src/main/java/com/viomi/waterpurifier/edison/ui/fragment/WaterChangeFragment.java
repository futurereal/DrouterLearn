package com.viomi.waterpurifier.edison.ui.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterBusEvent;
import com.viomi.waterpurifier.edison.config.WaterQualityEnum;
import com.viomi.waterpurifier.edison.databinding.FragmentQualitySwitchBinding;

import io.reactivex.disposables.Disposable;

/**
 * @author xinqi
 * @Desciption 水质切换的对话框
 */
public class WaterChangeFragment extends BaseDialogFragment<FragmentQualitySwitchBinding> {

    private final static String TAG = "QualitySwitchFragment";
    public final static String KEY_TARGET_TYPE = "keyTargetType";
    public final static String KEY_CURRENT_TYPE = "keyCurrentType";
    public final static String SECOND_UNIT = "''";
    public final static String ANIMI_FILE_ARROW = "arrowheads.json";
    public final static String ANIMI_FILE_MINERAL = "changing_mineral_water.json";
    public final static String ANIMI_FILE_PURIFIED = "changing_purified_water.json";

    private final static long PROGRESS_MAX_SIZE = 30 * 1000;
    private final static long ONE_SECOND = 1000;
    private CountDownTimer countDownTimer;

    @Override
    protected void initView() {
        String progress = (int) (PROGRESS_MAX_SIZE / ONE_SECOND) + SECOND_UNIT;
        viewDataBinding.qualityswitchCountdown.setText(progress);
        Bundle bundle = getArguments();
        int currentTypeNameId = bundle.getInt(KEY_CURRENT_TYPE);
        int targetTypeNameId = bundle.getInt(KEY_TARGET_TYPE);
        String currentTypeName = getResources().getString(currentTypeNameId);
        String targetTypeName = getResources().getString(targetTypeNameId);
        Log.i(TAG, "initChildView: currentTypeName: " + currentTypeName + " targetTypeName: " + targetTypeName);
        viewDataBinding.qualityswitchCurrentQuality.setText(currentTypeName);
        viewDataBinding.qualityswitchTargetQuality.setText(targetTypeName);

        //开启动画
        if (targetTypeNameId == WaterQualityEnum.MINERAL_WATER.nameStrId) {
            Log.i(TAG, "initChildView: to mineral");
            viewDataBinding.qualityswitchWatertype.setAnimation(ANIMI_FILE_MINERAL);
        } else {
            viewDataBinding.qualityswitchWatertype.setAnimation(ANIMI_FILE_PURIFIED);
        }
        viewDataBinding.qualityswitchWatertype.playAnimation();
        viewDataBinding.qualityswitchArrowhead.setAnimation(ANIMI_FILE_ARROW);
        viewDataBinding.qualityswitchArrowhead.playAnimation();
        // 启动倒计时
        startCountDown();
        ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(true);
    }

    private void startCountDown() {
        Log.i(TAG, "startCountDown: ");
        countDownTimer = new CountDownTimer(PROGRESS_MAX_SIZE, ONE_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                long count = millisUntilFinished / 1000;
                String countText = count + SECOND_UNIT;
                viewDataBinding.qualityswitchCountdown.setText(countText);
            }

            @Override
            public void onFinish() {
                dismissAllowingStateLoss();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void initListener() {
        viewDataBinding.qualityswitchSkip.setOnClickListener(v -> {
            countDownTimer.onFinish();
            dismissAllowingStateLoss();
        });

        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(busEvent -> {
            Log.i(TAG, "initListener: " + busEvent.getMsgId());
            switch (busEvent.getMsgId()) {
                case WaterBusEvent.MSG_PROPERTY_MINERAL_TYPE:
                    // 跟目标水质量取消
                    dismissAllowingStateLoss();
                    break;
            }
        });
        addDispose(disposable);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i(TAG, "onDismiss: dismiss");
        ModuleSettingServiceFactory.getInstance().getViotService().setKeepScreenOn(false);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_quality_switch;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
        viewDataBinding.qualityswitchArrowhead.cancelAnimation();
        viewDataBinding.qualityswitchWatertype.cancelAnimation();
    }
}
