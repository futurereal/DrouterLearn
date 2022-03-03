package com.viomi.iotdevice.main.newdefined.property;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Quickfreeze extends PropertyOperable<Vbool> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Quickfreeze.toPropertyType();

    private static AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static DataType FORMAT = DataType.BOOL;

    public Quickfreeze() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.FRIDGEPANEL_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.QUICKFREEZE_PROPERTY_IID);
    }

    public boolean getValue() {
        return ((Vbool) super.getCurrentValue()).getValue();
    }

    public void setValue(boolean value) {
        super.setDataValue(new Vbool(value));
    }
}