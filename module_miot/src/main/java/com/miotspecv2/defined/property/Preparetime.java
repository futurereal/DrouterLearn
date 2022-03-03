package com.miotspecv2.defined.property;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vuint16;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Preparetime extends PropertyOperable<Vuint16> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Preparetime.toPropertyType();

    private static final AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static final DataType FORMAT = DataType.UINT16;

    public Preparetime() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.PREPARETIME_PROPERTY_IID);
    }

    public int getValue() {
        return ((Vuint16) super.getCurrentValue()).getValue();
    }

    public void setValue(int value) {
        super.setDataValue(new Vuint16(value));
    }
}