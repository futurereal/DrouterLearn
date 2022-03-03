package com.miotspecv2.defined.service;

import android.util.Log;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.action.Cancelcooking;
import com.miotspecv2.defined.action.Pause;
import com.miotspecv2.defined.action.Startcook;
import com.miotspecv2.defined.event.LowwaterLevel;
import com.miotspecv2.defined.property.Fault;
import com.miotspecv2.defined.property.Lefttime;
import com.miotspecv2.defined.property.Status;
import com.miotspecv2.defined.property.Temperature;
import com.miotspecv2.defined.property.Workingtime;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Oven extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Oven.toServiceType();
    private static final String TAG = "Oven";

    public Oven(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.OVEN_SERVICE_IID);

        super.addProperty(new Status());
        super.addProperty(new Fault());
        super.addProperty(new Lefttime());
        super.addProperty(new Workingtime());
        super.addProperty(new Temperature());

        if (hasOptionalProperty) {
        }

        super.addAction(new Startcook());
        super.addAction(new Cancelcooking());
        super.addAction(new Pause());

        super.addEvent(new LowwaterLevel());
    }

    /**
     * Properties
     */
    public Status status() {
        Property p = super.getProperty(Status.TYPE);
        if (p != null) {
            if (p instanceof Status) {
                return (Status) p;
            }
        }

        return null;
    }

    public Fault fault() {
        Property p = super.getProperty(Fault.TYPE);
        if (p != null) {
            if (p instanceof Fault) {
                return (Fault) p;
            }
        }

        return null;
    }

    public Lefttime lefttime() {
        Property p = super.getProperty(Lefttime.TYPE);
        if (p != null) {
            if (p instanceof Lefttime) {
                return (Lefttime) p;
            }
        }

        return null;
    }

    public Workingtime workingtime() {
        Property p = super.getProperty(Workingtime.TYPE);
        if (p != null) {
            if (p instanceof Workingtime) {
                return (Workingtime) p;
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
    public Startcook startcook() {
        Action a = super.getAction(Startcook.TYPE);
        if (a != null) {
            if (a instanceof Startcook) {
                return (Startcook) a;
            }
        }

        return null;
    }

    public Cancelcooking cancelcooking() {
        Action a = super.getAction(Cancelcooking.TYPE);
        if (a != null) {
            if (a instanceof Cancelcooking) {
                return (Cancelcooking) a;
            }
        }

        return null;
    }

    public Pause pause() {
        Action a = super.getAction(Pause.TYPE);
        if (a != null) {
            if (a instanceof Pause) {
                return (Pause) a;
            }
        }

        return null;
    }

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        int getStatus();

        int getFault();

        int getLefttime();

        int getWorkingtime();

        int getTemperature();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
        void onStartcook(int status);

        void onCancelcooking(int status);

        void onPause(int status);
    }


    private MiotError onStartcook(ActionInfo action) {
        int pStatus = ((Vuint8) action.getArgumentValue(Status.TYPE)).getValue();
        actionHandler.onStartcook(pStatus);

        return MiotError.OK;
    }

    private MiotError onCancelcooking(ActionInfo action) {
        int pStatus = ((Vuint8) action.getArgumentValue(Status.TYPE)).getValue();
        actionHandler.onCancelcooking(pStatus);

        return MiotError.OK;
    }

    private MiotError onPause(ActionInfo action) {
        int pStatus = ((Vuint8) action.getArgumentValue(Status.TYPE)).getValue();
        actionHandler.onPause(pStatus);

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

            default:
                return MiotError.IOT_RESOURCE_NOT_EXIST;
        }
    }

    @Override
    public MiotError onGet(Property property) {
        Log.e(TAG, "onGet");

        if (propertyGetter == null) {
            return super.onGet(property);
        }

        Miotspecv2Defined.Property p = Miotspecv2Defined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Status:
                property.setValue(propertyGetter.getStatus());
                break;
            case Fault:
                property.setValue(propertyGetter.getFault());
                break;
            case Lefttime:
                property.setValue(propertyGetter.getLefttime());
                break;
            case Workingtime:
                property.setValue(propertyGetter.getWorkingtime());
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
            case Startcook:
                return onStartcook(action);
            case Cancelcooking:
                return onCancelcooking(action);
            case Pause:
                return onPause(action);

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}