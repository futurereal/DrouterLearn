package com.miotspecv2.defined.action;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Cancelprepare extends ActionOperable {

    public static final ActionType TYPE = Miotspecv2Defined.Action.Cancelprepare.toActionType();

    public Cancelprepare() {
        super(TYPE);


        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.CANCELPREPARE_ACTION_IID);
    }
}