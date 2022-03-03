package com.viomi.ovensocommon.componentservice.waterpurifier;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;

public class EmptyWaterService implements IWaterService {

    @Override
    public boolean getChildLock() {
        return false;
    }


    @Override
    public String getMineralLevel() {
        return "";
    }

    @Override
    public void reportStandByTime(int standByIndex) {

    }

    @Override
    public void isPowerOffLauncher() {

    }

    @Override
    public void MiotLoginStatusChange(boolean isBind) {

    }

    @Override
    public void ViotLoginStatusChange(boolean isBind) {

    }


    @Override
    public void dealPropertyFromPlug(PropertyEntity propertyEntity) {

    }

    @Override
    public void doActionFromPlug(int siid, int aiid, List<PropertyEntity> paramsProp) {

    }

    @Override
    public void dealPropertyChangeFromFirm(PropertyEntity propertyEntity) {

    }

    @Override
    public void dealEventChangeFromFirm(PropertyEntity propertyEntity) {

    }
}
