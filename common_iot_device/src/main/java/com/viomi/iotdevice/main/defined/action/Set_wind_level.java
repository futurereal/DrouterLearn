package com.viomi.iotdevice.main.defined.action;

import com.viomi.iotdevice.main.defined.ViomiDefined;
import com.viomi.iotdevice.main.defined.property.Wind_level;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Set_wind_level extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.Set_wind_level.toActionType();

    public Set_wind_level() {
        super(TYPE);

        super.addArgument(Wind_level.TYPE.toString());
    }
}