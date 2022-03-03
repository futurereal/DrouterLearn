package com.miotspecv2.defined.property;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Mode extends PropertyOperable<Vuint8> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Mode.toPropertyType();

    private static final AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static final DataType FORMAT = DataType.UINT8;

    public Mode() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.MODE_PROPERTY_IID);
    }

    public int getValue() {
        return ((Vuint8) super.getCurrentValue()).getValue();
    }

    public void setValue(int value) {
        super.setDataValue(new Vuint8(value));
    }
}