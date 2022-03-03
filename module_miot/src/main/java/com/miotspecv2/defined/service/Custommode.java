package com.miotspecv2.defined.service;

import android.util.Log;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.property.Modeone;
import com.miotspecv2.defined.property.Modetwo;
import com.miotspecv2.defined.property.Modethree;
import com.miotspecv2.defined.property.Modefour;
import com.miotspecv2.defined.property.Modefive;
import com.miotspecv2.defined.property.Modesix;
import com.miotspecv2.defined.property.Modeseven;
import com.miotspecv2.defined.property.Modeeight;
import com.xiaomi.miot.typedef.data.DataValue;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.data.value.Vfloat;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.data.value.Vuint16;
import com.xiaomi.miot.typedef.data.value.Vuint32;
import com.xiaomi.miot.typedef.data.value.Vuint64;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Custommode extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Custommode.toServiceType();
    private static final String TAG = "Custommode";

    public Custommode(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.CUSTOMMODE_SERVICE_IID);

        super.addProperty(new Modeone());
        super.addProperty(new Modetwo());
        super.addProperty(new Modethree());
        super.addProperty(new Modefour());
        super.addProperty(new Modefive());
        super.addProperty(new Modesix());
        super.addProperty(new Modeseven());
        super.addProperty(new Modeeight());

        if (hasOptionalProperty) {
        }


    }

    /**
     * Properties
     */
    public Modeone modeone() {
        Property p = super.getProperty(Modeone.TYPE);
        if (p != null) {
            if (p instanceof Modeone) {
                return (Modeone) p;
            }
        }

        return null;
    }
    public Modetwo modetwo() {
        Property p = super.getProperty(Modetwo.TYPE);
        if (p != null) {
            if (p instanceof Modetwo) {
                return (Modetwo) p;
            }
        }

        return null;
    }
    public Modethree modethree() {
        Property p = super.getProperty(Modethree.TYPE);
        if (p != null) {
            if (p instanceof Modethree) {
                return (Modethree) p;
            }
        }

        return null;
    }
    public Modefour modefour() {
        Property p = super.getProperty(Modefour.TYPE);
        if (p != null) {
            if (p instanceof Modefour) {
                return (Modefour) p;
            }
        }

        return null;
    }
    public Modefive modefive() {
        Property p = super.getProperty(Modefive.TYPE);
        if (p != null) {
            if (p instanceof Modefive) {
                return (Modefive) p;
            }
        }

        return null;
    }
    public Modesix modesix() {
        Property p = super.getProperty(Modesix.TYPE);
        if (p != null) {
            if (p instanceof Modesix) {
                return (Modesix) p;
            }
        }

        return null;
    }
    public Modeseven modeseven() {
        Property p = super.getProperty(Modeseven.TYPE);
        if (p != null) {
            if (p instanceof Modeseven) {
                return (Modeseven) p;
            }
        }

        return null;
    }
    public Modeeight modeeight() {
        Property p = super.getProperty(Modeeight.TYPE);
        if (p != null) {
            if (p instanceof Modeeight) {
                return (Modeeight) p;
            }
        }

        return null;
    }

    /**
     * Actions
     */

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        String getModeone();

        String getModetwo();

        String getModethree();

        String getModefour();

        String getModefive();

        String getModesix();

        String getModeseven();

        String getModeeight();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setModeone(String value);

        void setModetwo(String value);

        void setModethree(String value);

        void setModefour(String value);

        void setModefive(String value);

        void setModesix(String value);

        void setModeseven(String value);

        void setModeeight(String value);

    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
    }



    /**
     * Handle actions invocation & properties operation
     */
    private ActionHandler actionHandler;
    private PropertyGetter propertyGetter;
    private PropertySetter propertySetter;

    public void setHandler(ActionHandler handler, PropertyGetter getter, PropertySetter setter) {
        actionHandler = handler;
        propertyGetter = getter;
        propertySetter = setter;
    }

    @Override
    public MiotError onSet(Property property) {
        Log.e(TAG, "onSet");

        if (propertySetter == null) {
            return super.onSet(property);
        }

        Miotspecv2Defined.Property p = Miotspecv2Defined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Modeone:
                propertySetter.setModeone(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modetwo:
                propertySetter.setModetwo(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modethree:
                propertySetter.setModethree(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modefour:
                propertySetter.setModefour(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modefive:
                propertySetter.setModefive(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modesix:
                propertySetter.setModesix(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modeseven:
                propertySetter.setModeseven(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Modeeight:
                propertySetter.setModeeight(((Vstring) property.getCurrentValue()).getValue());
                break;

            default:
                return MiotError.IOT_RESOURCE_NOT_EXIST;
        }

        return MiotError.OK;
    }

    @Override
    public MiotError onGet(Property property) {
        Log.e(TAG, "onGet");

        if (propertyGetter == null) {
            return super.onGet(property);
        }

        Miotspecv2Defined.Property p = Miotspecv2Defined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Modeone:
                property.setValue(propertyGetter.getModeone());
                break;
            case Modetwo:
                property.setValue(propertyGetter.getModetwo());
                break;
            case Modethree:
                property.setValue(propertyGetter.getModethree());
                break;
            case Modefour:
                property.setValue(propertyGetter.getModefour());
                break;
            case Modefive:
                property.setValue(propertyGetter.getModefive());
                break;
            case Modesix:
                property.setValue(propertyGetter.getModesix());
                break;
            case Modeseven:
                property.setValue(propertyGetter.getModeseven());
                break;
            case Modeeight:
                property.setValue(propertyGetter.getModeeight());
                break;

            default:
                return MiotError.IOT_RESOURCE_NOT_EXIST;
        }

        return MiotError.OK;
    }

    @Override
    public MiotError onAction(ActionInfo action) {
        Log.e(TAG, "onAction: " + action.getType().toString());

        if (actionHandler == null) {
            return super.onAction(action);
        }

        Miotspecv2Defined.Action a = Miotspecv2Defined.Action.valueOf(action.getType());
        switch (a) {

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}