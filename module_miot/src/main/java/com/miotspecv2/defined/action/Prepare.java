package com.miotspecv2.defined.action;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.property.Dishid;
import com.miotspecv2.defined.property.Dishname;
import com.miotspecv2.defined.property.Mode;
import com.miotspecv2.defined.property.Tempz;
import com.miotspecv2.defined.property.Timez;
import com.miotspecv2.defined.property.Tempk;
import com.miotspecv2.defined.property.Timek;
import com.miotspecv2.defined.property.Doorisopen;
import com.miotspecv2.defined.property.Worktotaltime;
import com.miotspecv2.defined.property.Finishtime;
import com.miotspecv2.defined.property.Watertank;
import com.miotspecv2.defined.property.Preparetime;
import com.miotspecv2.defined.property.Ovenhardwarever;
import com.miotspecv2.defined.property.Ovenlight;
import com.miotspecv2.defined.property.Recipestep;
import com.miotspecv2.defined.property.Record;
import com.miotspecv2.defined.property.Microlevel;
import com.miotspecv2.defined.property.Modename;
import com.miotspecv2.defined.property.Modestep;
import com.miotspecv2.defined.property.Microtime;
import com.miotspecv2.defined.property.Mixmodeindicator;
import com.miotspecv2.defined.property.Watertankisclose;
import com.miotspecv2.defined.property.Screencusrecipes;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class Prepare extends ActionOperable {

    public static final ActionType TYPE = Miotspecv2Defined.Action.Prepare.toActionType();

    public Prepare() {
        super(TYPE);

        super.addArgument(Dishid.TYPE.toString());
        super.addArgument(Preparetime.TYPE.toString());

        super.setServiceInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);
        super.setDeviceInstanceID(0);
        super.setInstanceID(Miotspecv2Defined.PREPARE_ACTION_IID);
    }
}