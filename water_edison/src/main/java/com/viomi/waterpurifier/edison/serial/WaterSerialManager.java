package com.viomi.waterpurifier.edison.serial;

import android.util.Log;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.waterpurifier.edison.entity.FilterEntity;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/12/13
 */
public class WaterSerialManager {
    private static final String TAG = "WaterSerialManager";

    private static volatile WaterSerialManager mInstance;

    private WaterSerialManager() {
    }

    public static WaterSerialManager getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        synchronized (WaterSerialManager.class) {
            if (mInstance == null) {
                mInstance = new WaterSerialManager();
            }
        }
        return mInstance;
    }

    public void writeProperty(PropertyEntity propertyEntity) {
        Log.i(TAG, "setOtherProperty: ");
        List<PropertyEntity> propertyEntities = new ArrayList<>();
        propertyEntities.add(propertyEntity);
        writePropertyList(propertyEntities);
    }

    public void writePropertyList(List<PropertyEntity> propertyEntities) {
        Log.i(TAG, "WaterSerialManager: ");
        WaterPropertyListWrite waterPropertyListWrite = new WaterPropertyListWrite(propertyEntities);
        waterPropertyListWrite.executeWrite();
    }

    public void doAction(int siid, int aiid, List<PropertyEntity> propertyEntityList) {
        boolean isError = MessageDialogManager.getInstance().checkErrorAndShowDialog();
        Log.i(TAG, "waterOutListener : " + isError);
        if (isError) {
            return;
        }
        SerialControl.setAction("", siid, aiid, propertyEntityList);
    }

    public void resetFilter(FilterEntity filterEntity) {
        PropertyEntity propertyUsedTime = new PropertyEntity(filterEntity.getTimeSiid(), filterEntity.getTimePiid(), 0);
        PropertyEntity propertyUsedFlow = new PropertyEntity(filterEntity.getUseFlowSiid(), filterEntity.getUseFlowPiid(), 0);
        List<PropertyEntity> propertyEntityList = new ArrayList<PropertyEntity>();
        propertyEntityList.add(propertyUsedTime);
        propertyEntityList.add(propertyUsedFlow);
        doAction(filterEntity.getSiid(), filterEntity.getAiid(), propertyEntityList);
    }
}
