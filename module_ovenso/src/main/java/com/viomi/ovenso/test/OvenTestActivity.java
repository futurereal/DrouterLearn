package com.viomi.ovenso.test;

import android.util.Log;

import com.viomi.ovenso.OvenSoService;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.ActivityOvenTestBinding;
import com.viomi.ovensocommon.BaseActivity;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.datastore.DataStoreUtils;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.router.core.ViomiRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 测试类
 * @date:2022/1/18
 */
public class OvenTestActivity extends BaseActivity<ActivityOvenTestBinding> {
    private static final String TAG = "OvenTestActivity";
    int deviceFaultBit = 7;
    private final int[] microNameIds = new int[]{R.string.microwave_powername_one, R.string.microwave_powername_two,
            R.string.microwave_powername_three, R.string.microwave_powername_four, R.string.microwave_powername_five};

    private static final String DATASTORE_KEY_STRING = "dataStoreKeyString";
    private static final String DATASTORE_KEY_BOOLEAN = "dataStoreKeyBoolean";
    private static final String DATASTORE_KEY_INT = "dataStoreKeyInt";
    private static final String DATASTORE_KEY_INT1 = "dataStoreKeyInt1";
    private static final String DATASTORE_KEY_INT2 = "dataStoreKeyInt2";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_oven_test;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        OvensoServiceFactory.getInstance().setOvenService(new OvenSoService());
        viewDataBinding.ovensoTestDevicefalut.setOnClickListener(view -> {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setSid(OvenPropEnum.DEVICE_FAULT.siid);
            propertyEntity.setPid(OvenPropEnum.DEVICE_FAULT.piid);
            propertyEntity.setContent(1 << deviceFaultBit);
            OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
            deviceFaultBit++;
        });
        viewDataBinding.ovensoTestMain.setOnClickListener(view -> {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MAIN).navigation();
        });

        viewDataBinding.ovensoDatastoreSaveAndRead.setOnClickListener(view -> {
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_BOOLEAN, true);
            Boolean booleanValue = DataStoreUtils.INSTANCE.getSyncData(DATASTORE_KEY_BOOLEAN, false);
             Log.i(TAG, "initView: booleanValue " + booleanValue);
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_STRING, "true");
            String stringValue = DataStoreUtils.INSTANCE.getSyncData(DATASTORE_KEY_STRING, "");
            Log.i(TAG, "initView: stringValue " + stringValue);
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_INT, 1234);
            int intValue = DataStoreUtils.INSTANCE.getSyncData(DATASTORE_KEY_INT, -1);
            Log.i(TAG, "initView: intValue " + intValue);
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_INT, 0);
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_INT1, 1);
            DataStoreUtils.INSTANCE.putSyncData(DATASTORE_KEY_INT2, 2);


        });
    }

    @Override
    protected void initData() {
        List<String> microNameList = new ArrayList<>();
        for (int resource : microNameIds) {
            String name = getString(resource);
            microNameList.add(name);
        }
        viewDataBinding.ovensoTestMicro.setStringData(microNameList, 3);

        viewDataBinding.ovensoTestNormal.setIntData(10, 1, 100);
    }

    @Override
    protected void initListener() {
        viewDataBinding.ovensoTestMicro.setOnSelectListener((view, selectedValue) -> {
            Log.i(TAG, "initListener:selectedValue  micro" + selectedValue);
        });

        viewDataBinding.ovensoTestNormal.setOnSelectListener((view, selectedValue) -> {
            Log.i(TAG, "initListener: selectValue : normal " + selectedValue);
        });
    }

}
