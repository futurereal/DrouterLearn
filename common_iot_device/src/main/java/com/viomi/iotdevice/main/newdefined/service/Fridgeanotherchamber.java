package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.property.OnAnother;
import com.viomi.iotdevice.main.newdefined.property.TargettemperatureAnother;
import com.viomi.iotdevice.main.newdefined.property.TemperatureAnother;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Fridgeanotherchamber extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.FridgechamberAnother.toServiceType();
    private static final String TAG = "Fridgeanotherchamber";

    public Fridgeanotherchamber(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.FRIDGECHAMBER_ANOTHER_SERVICE_IID);

        super.addProperty(new TemperatureAnother());
        super.addProperty(new TargettemperatureAnother());
        super.addProperty(new OnAnother());

        if (hasOptionalProperty) {
        }

    }

    /**
     * Properties
     */
    public TemperatureAnother temperature() {
        Property p = super.getProperty(TemperatureAnother.TYPE);
        if (p != null) {
            if (p instanceof TemperatureAnother) {
                return (TemperatureAnother) p;
            }
        }

        return null;
    }
    public TargettemperatureAnother targettemperature() {
        Property p = super.getProperty(TargettemperatureAnother.TYPE);
        if (p != null) {
            if (p instanceof TargettemperatureAnother) {
                return (TargettemperatureAnother) p;
            }
        }

        return null;
    }
    public OnAnother on() {
        Property p = super.getProperty(OnAnother.TYPE);
        if (p != null) {
            if (p instanceof OnAnother) {
                return (OnAnother) p;
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
            case FridgechamberTargettemperature:
                propertySetter.setTargettemperature(((Vuint8) property.getCurrentValue()).getValue());
                break;
            case FridgechamberOn:
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
            case FridgechamberTemperature:
                property.setValue(propertyGetter.getTemperature());
                break;
            case FridgechamberTargettemperature:
                property.setValue(propertyGetter.getTargettemperature());
                break;
            case FridgechamberOn:
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