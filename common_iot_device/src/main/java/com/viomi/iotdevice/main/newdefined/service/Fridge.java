package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.defined.property.Mode;
import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.property.Fault;
import com.viomi.iotdevice.main.newdefined.property.Temperature;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Fridge extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Fridge.toServiceType();
    private static final String TAG = "Fridge";

    public Fridge(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.FRIDGE_SERVICE_IID);

        super.addProperty(new Fault());
        super.addProperty(new Mode());
        super.addProperty(new Temperature());

        if (hasOptionalProperty) {
        }

    }

    /**
     * Properties
     */
    public Fault fault() {
        Property p = super.getProperty(Fault.TYPE);
        if (p != null) {
            if (p instanceof Fault) {
                return (Fault) p;
            }
        }

        return null;
    }
    public Mode mode() {
        Property p = super.getProperty(Mode.TYPE);
        if (p != null) {
            if (p instanceof Mode) {
                return (Mode) p;
            }
        }

        return null;
    }
    public Temperature temperature() {
        Property p = super.getProperty(Temperature.TYPE);
        if (p != null) {
            if (p instanceof Temperature) {
                return (Temperature) p;
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
        int getFault();

        int getMode();

        int getTemperature();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setMode(int value);

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
            case Mode:
                propertySetter.setMode(((Vuint8) property.getCurrentValue()).getValue());
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
            case Fault:
                property.setValue(propertyGetter.getFault());
                break;
            case Mode:
                property.setValue(propertyGetter.getMode());
                break;
            case Temperature:
                property.setValue(propertyGetter.getTemperature());
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