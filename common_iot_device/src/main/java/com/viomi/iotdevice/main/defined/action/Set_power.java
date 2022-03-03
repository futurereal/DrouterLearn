package com.viomi.iotdevice.main.defined.action;

import com.viomi.iotdevice.main.defined.ViomiDefined;
import com.viomi.iotdevice.main.defined.property.Power;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Set_power extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.Set_power.toActionType();

    public Set_power() {
        super(TYPE);

        super.addArgument(Power.TYPE.toString());
    }
}