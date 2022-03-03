package com.viomi.waterpurifier.edison.serial;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyListWrite;

import java.util.List;

public class WaterPropertyListWrite extends PropertyListWrite {
    public WaterPropertyListWrite(List<PropertyEntity> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public List<PropertyEntity> getPropertyList() {
        return propertyList;
    }
}
