package com.viomi.ovensocommon.serialcontrol;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

import java.util.List;

public abstract class PropertyListWrite {
    private static final String TAG = "PropertyListWrite";
    protected List<PropertyEntity> propertyList;

    public abstract List<PropertyEntity> getPropertyList();

    public void executeWrite() {
        List<PropertyEntity> propertyEntities = getPropertyList();
        if (propertyEntities == null) {
            throw new NullPointerException("list mus not null");
        }
        PropertyWriteManager.getInstance().setPropertyList(propertyEntities);
    }
}
