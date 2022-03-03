package com.viomi.waterpurifier.edison.ui;

import android.util.Log;
import android.view.View;

import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.ovensocommon.toast.ViomiToastUtil;
import com.viomi.router.annotation.Route;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.ActivityWaterAgingBinding;
import com.viomi.waterpurifier.edison.serial.WaterSerialManager;

/**
 * @author admin
 * @description:
 * @data:2021/10/13
 */
@Route(path = ViomiRouterConstant.WATER_AGING)
public class WaterAgingActivity extends BaseActivity<ActivityWaterAgingBinding> {
    private static final String TAG = "WaterAgingActivity";
    boolean isFactoryStart = false;
    boolean isDisinfectStart = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_water_aging;
    }

    @Override
    protected void initView() {
        viewDataBinding.wateragingFactory.setOnClickListener(v -> dealFactory());

        viewDataBinding.wateragingDisinfect.setOnClickListener(v -> dealDisinfect());

        viewDataBinding.titleBar.setOnbackClickListener(v -> {
            Log.i(TAG, "initView: setBack");
            if (isDisinfectStart || isFactoryStart) {
                ViomiToastUtil.showToastCenter(getString(R.string.wateraging_close_tip));
                return;
            }
            finish();
        });
    }

    private void dealFactory() {
        Log.i(TAG, "dealFactory: isFactoryStart: " + isFactoryStart);
        boolean factoryValue = !isFactoryStart;
        WaterSerialManager.getInstance().writeProperty(new PropertyEntity(WaterPropEnum.FACTORY.siid, WaterPropEnum.FACTORY.piid, factoryValue));
        isFactoryStart = !isFactoryStart;
        if (isFactoryStart) {
            viewDataBinding.wateragingFactory.setText(R.string.wateraging_factory_stop);
        } else {
            viewDataBinding.wateragingFactory.setText(R.string.wateraging_factory_start);
        }
    }

    private void dealDisinfect() {
        Log.i(TAG, "dealDisinfect: isFactoryStart: " + isFactoryStart);
        if (!isFactoryStart) {
            ViomiToastUtil.showToastCenter(getString(R.string.wateraging_factory_tip));
            return;
        }
        // disinfectValue 是相反的设置
        int disinfectValue = isDisinfectStart ? 0 : 1;
        WaterSerialManager.getInstance().writeProperty(new PropertyEntity(WaterPropEnum.FACTORY_DISINFECT.siid, WaterPropEnum.FACTORY_DISINFECT.piid, disinfectValue));
        isDisinfectStart = !isDisinfectStart;
        if (isDisinfectStart) {
            viewDataBinding.wateragingDisinfect.setText(R.string.wateraging_disinfect_stop);
            viewDataBinding.wateragingDisinfectTip.setVisibility(View.VISIBLE);
        } else {
            viewDataBinding.wateragingDisinfect.setText(R.string.wateraging_disinfect_start);
            viewDataBinding.wateragingDisinfectTip.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    protected void initData() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
