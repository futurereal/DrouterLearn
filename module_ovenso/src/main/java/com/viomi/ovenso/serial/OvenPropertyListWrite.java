package com.viomi.ovenso.serial;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyListWrite;

import java.util.List;

public class OvenPropertyListWrite extends PropertyListWrite {
    public OvenPropertyListWrite(List<PropertyEntity> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public List<PropertyEntity> getPropertyList() {
        return propertyList;
    }
}
