package com.miotspecv2.defined.property;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class Checkcode extends PropertyOperable<Vstring> {

    public static PropertyType TYPE = Miotspecv2Defined.Property.Checkcode.toPropertyType();

    private static final AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.NOTIFY);

    private static final DataType FORMAT = DataType.STRING;

    public Checkcode() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
        super.setServiceInstanceID(Miotspecv2Defined.SAFETYCHECK_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.CHECKCODE_PROPERTY_IID);
    }

    public String getValue() {
        return ((Vstring) super.getCurrentValue()).getValue();
    }

    public void setValue(String value) {
        super.setDataValue(new Vstring(value));
    }
}