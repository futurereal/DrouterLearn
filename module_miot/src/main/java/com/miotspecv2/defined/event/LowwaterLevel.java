package com.miotspecv2.defined.event;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.xiaomi.miot.typedef.device.operable.EventOperable;
import com.xiaomi.miot.typedef.urn.EventType;

public class LowwaterLevel extends EventOperable {

    public static final EventType TYPE = Miotspecv2Defined.Event.LowWaterLevel.toEventType();

    public LowwaterLevel() {
        super(TYPE);


        super.setServiceInstanceID(Miotspecv2Defined.OVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.LOW_WATER_LEVEL_EVENT_IID);
    }
}