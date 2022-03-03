package com.viomi.iotdevice.main.newdefined.property;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Frosttemp extends PropertyOperable<Vint> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Frosttemp.toPropertyType();

    private static AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.NOTIFY);

    private static DataType FORMAT = DataType.INT;

    public Frosttemp() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.FRIDGEFACTORY_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.FROSTTEMP_PROPERTY_IID);
    }

    public int getValue() {
        return ((Vint) super.getCurrentValue()).getValue();
    }

    public void setValue(int value) {
        super.setDataValue(new Vint(value));
    }
}