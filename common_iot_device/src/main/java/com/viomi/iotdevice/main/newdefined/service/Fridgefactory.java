package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.action.Testfrost;
import com.viomi.iotdevice.main.newdefined.property.Forcedfrost;
import com.viomi.iotdevice.main.newdefined.property.Forcenonstop;
import com.viomi.iotdevice.main.newdefined.property.Frosttemp;
import com.viomi.iotdevice.main.newdefined.property.Heatingwire;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Fridgefactory extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Fridgefactory.toServiceType();
    private static final String TAG = "Fridgefactory";

    public Fridgefactory(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.FRIDGEFACTORY_SERVICE_IID);

        super.addProperty(new Frosttemp());
        super.addProperty(new Forcedfrost());
        super.addProperty(new Forcenonstop());
        super.addProperty(new Heatingwire());

        if (hasOptionalProperty) {
        }

        super.addAction(new Testfrost());
    }

    /**
     * Properties
     */
    public Frosttemp frosttemp() {
        Property p = super.getProperty(Frosttemp.TYPE);
        if (p != null) {
            if (p instanceof Frosttemp) {
                return (Frosttemp) p;
            }
        }

        return null;
    }
    public Forcedfrost forcedfrost() {
        Property p = super.getProperty(Forcedfrost.TYPE);
        if (p != null) {
            if (p instanceof Forcedfrost) {
                return (Forcedfrost) p;
            }
        }

        return null;
    }
    public Forcenonstop forcenonstop() {
        Property p = super.getProperty(Forcenonstop.TYPE);
        if (p != null) {
            if (p instanceof Forcenonstop) {
                return (Forcenonstop) p;
            }
        }

        return null;
    }
    public Heatingwire heatingwire() {
        Property p = super.getProperty(Heatingwire.TYPE);
        if (p != null) {
            if (p instanceof Heatingwire) {
                return (Heatingwire) p;
            }
        }

        return null;
    }

    /**
     * Actions
     */
    public Testfrost testfrost(){
        Action a = super.getAction(Testfrost.TYPE);
        if (a != null) {
            if (a instanceof Testfrost) {
                return (Testfrost) a;
            }
        }

        return null;
    }

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        int getFrosttemp();

        boolean getForcedfrost();

        boolean getForcenonstop();

        boolean getHeatingwire();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setForcedfrost(boolean value);

        void setForcenonstop(boolean value);

    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
        void onTestfrost();
    }


    private MiotError onTestfrost(ActionInfo action) {
        actionHandler.onTestfrost();

        return MiotError.OK;
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
            case Forcedfrost:
                propertySetter.setForcedfrost(((Vbool) property.getCurrentValue()).getValue());
                break;
            case Forcenonstop:
                propertySetter.setForcenonstop(((Vbool) property.getCurrentValue()).getValue());
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
            case Frosttemp:
                property.setValue(propertyGetter.getFrosttemp());
                break;
            case Forcedfrost:
                property.setValue(propertyGetter.getForcedfrost());
                break;
            case Forcenonstop:
                property.setValue(propertyGetter.getForcenonstop());
                break;
            case Heatingwire:
                property.setValue(propertyGetter.getHeatingwire());
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
            case Testfrost:
                return onTestfrost(action);

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}