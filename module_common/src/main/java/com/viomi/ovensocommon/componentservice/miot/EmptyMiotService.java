package com.viomi.ovensocommon.componentservice.miot;

import android.util.Log;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;
import java.util.Map;

public class EmptyMiotService implements IMiotService {
    private static final String TAG = "EmptyMiotService";

    @Override
    public boolean isMiotBind() {
        return false;
    }

    @Override
    public void reportData(Map propMap) {
        Log.i(TAG, "reportData: ");
    }

    @Override
    public void reportData(PropertyEntity propertyEntity) {
        Log.i(TAG, "reportData: ");
    }

    @Override
    public void reportData(List<PropertyEntity> reportDataEntities) {
        Log.i(TAG, "reportData: ");
    }

    @Override
    public void reportEvent(int sid, int eid) {
        Log.i(TAG, "reportEvent: ");
    }

    @Override
    public void reportAction(String eventName) {
        Log.i(TAG, "reportAction: ");
    }

    @Override
    public void getMiotBindKey(BindKeyCallBack bindKeyCallBack) {
        Log.i(TAG, "getMiotBindKey: ");
        bindKeyCallBack.keyReuslt("", "empty");
    }

    @Override
    public void resetAndRebindMiot() {
        Log.i(TAG, "resetAndRebindMiot: ");
    }
}
