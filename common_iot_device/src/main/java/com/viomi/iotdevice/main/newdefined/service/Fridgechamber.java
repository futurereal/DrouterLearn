package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.property.On;
import com.viomi.iotdevice.main.newdefined.property.Targettemperature;
import com.viomi.iotdevice.main.newdefined.property.Temperature;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Fridgechamber extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Fridgechamber.toServiceType();
    private static final String TAG = "Fridgechamber";

    public Fridgechamber(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.FRIDGECHAMBER_SERVICE_IID);

        super.addProperty(new Temperature());
        super.addProperty(new Targettemperature());
        super.addProperty(new On());

        if (hasOptionalProperty) {
        }

    }

    /**
     * Properties
     */
    public Temperature temperature() {
        Property p = super.getProperty(Temperature.TYPE);
        if (p != null) {
            if (p instanceof Temperature) {
                return (Temperature) p;
            }
        }

        return null;
    }
    public Targettemperature targettemperature() {
        Property p = super.getProperty(Targettemperature.TYPE);
        if (p != null) {
            if (p instanceof Targettemperature) {
                return (Targettemperature) p;
            }
        }

        return null;
    }
    public On on() {
        Property p = super.getProperty(On.TYPE);
        if (p != null) {
            if (p instanceof On) {
                return (On) p;
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
        int getTemperature();

        int getTargettemperature();

        boolean getOn();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setTargettemperature(int value);

        void setOn(boolean value);

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
            case Targettemperature:
                propertySetter.setTargettemperature(((Vint) property.getCurrentValue()).getValue());
                break;
            case On:
                propertySetter.setOn(((Vbool) property.getCurrentValue()).getValue());
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
            case Temperature:
                property.setValue(propertyGetter.getTemperature());
                break;
            case Targettemperature:
                property.setValue(propertyGetter.getTargettemperature());
                break;
            case On:
                property.setValue(propertyGetter.getOn());
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