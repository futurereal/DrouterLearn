package com.miotspecv2.defined.event;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.device.operable.EventOperable;
import com.xiaomi.miot.typedef.urn.EventType;

public class Workrecord extends EventOperable {

    public static final EventType TYPE = Miotspecv2Defined.Event.Workrecord.toEventType();

    public Workrecord() {
        super(TYPE);
        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.WORKRECORD_EVENT_IID);
    }
}