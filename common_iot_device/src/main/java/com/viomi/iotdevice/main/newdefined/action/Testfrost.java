package com.viomi.iotdevice.main.newdefined.action;


import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Testfrost extends ActionOperable {

    public static final ActionType TYPE = Miotspecv2Defined.Action.Testfrost.toActionType();

    public Testfrost() {
        super(TYPE);


        super.setServiceInstanceID(Miotspecv2Defined.FRIDGEFACTORY_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.TESTFROST_ACTION_IID);
    }
}