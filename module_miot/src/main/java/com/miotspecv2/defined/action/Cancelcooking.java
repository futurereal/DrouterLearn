package com.miotspecv2.defined.action;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.property.Status;
import com.miotspecv2.defined.property.Fault;
import com.miotspecv2.defined.property.Lefttime;
import com.miotspecv2.defined.property.Workingtime;
import com.miotspecv2.defined.property.Temperature;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Cancelcooking extends ActionOperable {

    public static final ActionType TYPE = Miotspecv2Defined.Action.Cancelcooking.toActionType();

    public Cancelcooking() {
        super(TYPE);

        super.addArgument(Status.TYPE.toString());

        super.setServiceInstanceID(Miotspecv2Defined.OVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.CANCELCOOKING_ACTION_IID);
    }
}