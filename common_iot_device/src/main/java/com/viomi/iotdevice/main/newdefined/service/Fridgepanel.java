package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.property.Quickcooling;
import com.viomi.iotdevice.main.newdefined.property.Quickfreeze;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Fridgepanel extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Fridgepanel.toServiceType();
    private static final String TAG = "Fridgepanel";

    public Fridgepanel(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.FRIDGEPANEL_SERVICE_IID);

        super.addProperty(new Quickcooling());
        super.addProperty(new Quickfreeze());

        if (hasOptionalProperty) {
        }

    }

    /**
     * Properties
     */
    public Quickcooling quickcooling() {
        Property p = super.getProperty(Quickcooling.TYPE);
        if (p != null) {
            if (p instanceof Quickcooling) {
                return (Quickcooling) p;
            }
        }

        return null;
    }
    public Quickfreeze quickfreeze() {
        Property p = super.getProperty(Quickfreeze.TYPE);
        if (p != null) {
            if (p instanceof Quickfreeze) {
                return (Quickfreeze) p;
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
        boolean getQuickcooling();

        boolean getQuickfreeze();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setQuickcooling(boolean value);

        void setQuickfreeze(boolean value);

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
            case Quickcooling:
                propertySetter.setQuickcooling(((Vbool) property.getCurrentValue()).getValue());
                break;
            case Quickfreeze:
                propertySetter.setQuickfreeze(((Vbool) property.getCurrentValue()).getValue());
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
            case Quickcooling:
                property.setValue(propertyGetter.getQuickcooling());
                break;
            case Quickfreeze:
                property.setValue(propertyGetter.getQuickfreeze());
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