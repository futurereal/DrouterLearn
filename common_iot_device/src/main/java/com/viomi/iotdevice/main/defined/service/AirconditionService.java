package com.viomi.iotdevice.main.defined.service;

import android.util.Log;

import com.viomi.iotdevice.main.defined.ViomiDefined;
import com.viomi.iotdevice.main.defined.action.Set_power;
import com.viomi.iotdevice.main.defined.action.Set_wind_level;
import com.viomi.iotdevice.main.defined.property.Power;
import com.viomi.iotdevice.main.defined.property.Wind_level;
import com.viomi.iotdevice.main.newdefined.property.Mode;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class AirconditionService extends ServiceOperable {

    public static final ServiceType TYPE = ViomiDefined.Service.AirconditionService.toServiceType();
    private static final String TAG = "AirconditionService";

    public AirconditionService(boolean hasOptionalProperty) {
        super(TYPE);

        super.addProperty(new Power());
        super.addProperty(new Mode());
        super.addProperty(new Wind_level());

        if (hasOptionalProperty) {
        }

        super.addAction(new Set_wind_level());
        super.addAction(new Set_power());
    }

    /**
     * Properties
     */
    public Power power() {
        Property p = super.getProperty(Power.TYPE);
        if (p != null) {
            if (p instanceof Power) {
                return (Power) p;
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
    public Wind_level wind_level() {
        Property p = super.getProperty(Wind_level.TYPE);
        if (p != null) {
            if (p instanceof Wind_level) {
                return (Wind_level) p;
            }
        }

        return null;
    }

    /**
     * Actions
     */
    public Set_wind_level set_wind_level(){
        Action a = super.getAction(Set_wind_level.TYPE);
        if (a != null) {
            if (a instanceof Set_wind_level) {
                return (Set_wind_level) a;
            }
        }

        return null;
    }
    public Set_power set_power(){
        Action a = super.getAction(Set_power.TYPE);
        if (a != null) {
            if (a instanceof Set_power) {
                return (Set_power) a;
            }
        }

        return null;
    }

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        int getPower();

        int getMode();

        int getWind_level();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setPower(int value);

        void setMode(int value);

        void setWind_level(int value);

    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
        void onSet_wind_level(int wind_level);
        void onSet_power(int power);
    }


    private MiotError onSet_wind_level(ActionInfo action) {
        int pWind_level = ((Vint) action.getArgumentValue(Wind_level.TYPE)).getValue();
        actionHandler.onSet_wind_level(pWind_level);

        return MiotError.OK;
    }
    private MiotError onSet_power(ActionInfo action) {
        int pPower = ((Vint) action.getArgumentValue(Power.TYPE)).getValue();
        actionHandler.onSet_power(pPower);

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

        ViomiDefined.Property p = ViomiDefined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Power:
                propertySetter.setPower(((Vint) property.getCurrentValue()).getValue());
                break;
            case Mode:
                propertySetter.setMode(((Vint) property.getCurrentValue()).getValue());
                break;
            case Wind_level:
                propertySetter.setWind_level(((Vint) property.getCurrentValue()).getValue());
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

        ViomiDefined.Property p = ViomiDefined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Power:
                property.setValue(propertyGetter.getPower());
                break;
            case Mode:
                property.setValue(propertyGetter.getMode());
                break;
            case Wind_level:
                property.setValue(propertyGetter.getWind_level());
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

        ViomiDefined.Action a = ViomiDefined.Action.valueOf(action.getType());
        switch (a) {
            case Set_wind_level:
                return onSet_wind_level(action);
            case Set_power:
                return onSet_power(action);

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}