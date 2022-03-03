package com.miotspecv2.defined.property;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Modesix extends PropertyOperable<Vstring> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Modesix.toPropertyType();

    private static final AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static final DataType FORMAT = DataType.STRING;

    public Modesix() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMMODE_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.MODESIX_PROPERTY_IID);
    }

    public String getValue() {
        return ((Vstring) super.getCurrentValue()).getValue();
    }

    public void setValue(String value) {
        super.setDataValue(new Vstring(value));
    }
}