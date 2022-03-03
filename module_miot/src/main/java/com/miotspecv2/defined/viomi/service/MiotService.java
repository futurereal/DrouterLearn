package com.miotspecv2.defined.viomi.service;

import android.util.Log;

import com.miotspecv2.defined.viomi.MiotDeviceHelper;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.BindKeyCallBack;
import com.viomi.ovensocommon.componentservice.miot.IMiotService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiotService implements IMiotService {
    private static final String TAG = "MiotService";
    private final MiotDeviceHelper miotDeviceHelper;

    public MiotService() {
        miotDeviceHelper = MiotDeviceHelper.getInstance();
    }

    @Override
    public boolean isMiotBind() {
        return false;
    }

    @Override
    public void reportData(Map propMap) {
        Log.i(TAG, "reportData:propMap  ");
        miotDeviceHelper.reportProperties(propMap);
    }

    /**
     * Miot 上报单个属性
     */
    @Override
    public void reportData(PropertyEntity propertyEntity) {
        Log.i(TAG, "reportData: propertyEntity ");
        ArrayList<PropertyEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(propertyEntity);
        reportData(reportDataEntities);
    }

    /**
     * Miot 上报多个属性
     */
    @Override
    public void reportData(List<PropertyEntity> reportDataEntities) {
        Map<String, Object> propMiot = new HashMap<>(reportDataEntities.size());
        for (PropertyEntity propertyEntity : reportDataEntities) {
            propMiot.put(propertyEntity.getSid() + "." + propertyEntity.getPid(), propertyEntity.getContent());
        }
        Log.i(TAG, "reportData: " + reportDataEntities.size());
        miotDeviceHelper.reportProperties(propMiot);
    }

    @Override
    public void reportEvent(int sid, int eid) {

    }

    @Override
    public void reportAction(String eventName) {
        Log.i(TAG, "reportAction: " + eventName);
        miotDeviceHelper.reportEvent(eventName);
    }

    @Override
    public void getMiotBindKey(BindKeyCallBack bindKeyCallBack) {
        Log.i(TAG, "getMiotBindKey: ");
        miotDeviceHelper.getMiotBindKey(bindKeyCallBack);
    }

    @Override
    public void resetAndRebindMiot() {
        Log.i(TAG, "resetAndRebindMiot: ");
        miotDeviceHelper.resetAndRebindMiot();
    }
}
