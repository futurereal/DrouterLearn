package com.viomi.waterpurifier.edison.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.WaterConstant;
import com.viomi.waterpurifier.edison.databinding.WatermainTestviewBinding;

/**
 * @author
 * @date 2022/01/11
 * @describe 烹饪界面弹框测试
 */
public class WaterMainTestView extends FrameLayout {
    private static final String TAG = "WaterMainTestView";
    private WatermainTestviewBinding waterMainTestDatabinding;
    private boolean cmdBooleanValue;
    private int index = 0;

    public WaterMainTestView(@NonNull Context context) {
        super(context);
    }

    public WaterMainTestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "WaterMainTestView: ");
        LayoutInflater.from(context).inflate(R.layout.watermain_testview, this);
        waterMainTestDatabinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.watermain_testview, this, true
        );
        if (WaterConstant.IS_TEST_UI) {
            waterMainTestDatabinding.watermainTestGroup.setVisibility(View.VISIBLE);
        }
        initListener(waterMainTestDatabinding);
    }

    private void initListener(WatermainTestviewBinding waterMainTestDatabinding) {
        Log.i(TAG, "initListener: ");
        waterMainTestDatabinding.watermainTestSelfclean.setOnClickListener(view -> {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setSid(WaterPropEnum.SELF_CLEAN_MODE.siid);
            propertyEntity.setPid(WaterPropEnum.SELF_CLEAN_MODE.piid);
            int cmdCode = WaterConstant.SELFCLEAN_BEGAIN + index;
            cmdCode = cmdCode % 3;
            propertyEntity.setContent(cmdCode);
            Log.i(TAG, "initListener: cmdCode: " + cmdCode);
            WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyEntity);
            if (cmdCode == 2) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        propertyEntity.setContent(0);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyEntity);
                    }
                }, 10000);
            }
            index++;
        });
        waterMainTestDatabinding.watermainTestFlushfilter.setOnClickListener(view -> {
            PropertyEntity propertyRefresh = new PropertyEntity();
            propertyRefresh.setSid(WaterPropEnum.RINSE.siid);
            propertyRefresh.setPid(WaterPropEnum.RINSE.piid);
            propertyRefresh.setContent(true);
            WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyRefresh);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        PropertyEntity propertyProgress = new PropertyEntity();
                        propertyProgress.setSid(WaterPropEnum.FILTER_RESET_PROGRESS.siid);
                        propertyProgress.setPid(WaterPropEnum.FILTER_RESET_PROGRESS.piid);
                        propertyProgress.setContent(33);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyProgress);
                        Thread.sleep(5000);
                        propertyProgress.setContent(66);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyProgress);
                        Thread.sleep(5000);
                        propertyProgress.setContent(80);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyProgress);
                        Thread.sleep(5000);
                        propertyProgress.setContent(99);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyProgress);
                        Thread.sleep(5000);
                        propertyProgress.setContent(100);
                        WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyProgress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        });
    }
}
