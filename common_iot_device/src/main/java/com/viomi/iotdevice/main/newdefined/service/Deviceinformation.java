package com.viomi.iotdevice.main.newdefined.service;

import android.util.Log;

import com.viomi.iotdevice.main.newdefined.Miotspecv2Defined;
import com.viomi.iotdevice.main.newdefined.property.Firmwarerevision;
import com.viomi.iotdevice.main.newdefined.property.Manufacturer;
import com.viomi.iotdevice.main.newdefined.property.Model;
import com.viomi.iotdevice.main.newdefined.property.Serialnumber;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Deviceinformation extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Deviceinformation.toServiceType();
    private static final String TAG = "Deviceinformation";

    public Deviceinformation(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.DEVICEINFORMATION_SERVICE_IID);

        super.addProperty(new Manufacturer());
        super.addProperty(new Model());
        super.addProperty(new Serialnumber());
        super.addProperty(new Firmwarerevision());

        if (hasOptionalProperty) {
        }

    }

    /**
     * Properties
     */
    public Manufacturer manufacturer() {
        Property p = super.getProperty(Manufacturer.TYPE);
        if (p != null) {
            if (p instanceof Manufacturer) {
                return (Manufacturer) p;
            }
        }

        return null;
    }
    public Model model() {
        Property p = super.getProperty(Model.TYPE);
        if (p != null) {
            if (p instanceof Model) {
                return (Model) p;
            }
        }

        return null;
    }
    public Serialnumber serialnumber() {
        Property p = super.getProperty(Serialnumber.TYPE);
        if (p != null) {
            if (p instanceof Serialnumber) {
                return (Serialnumber) p;
            }
        }

        return null;
    }
    public Firmwarerevision firmwarerevision() {
        Property p = super.getProperty(Firmwarerevision.TYPE);
        if (p != null) {
            if (p instanceof Firmwarerevision) {
                return (Firmwarerevision) p;
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
        String getManufacturer();

        String getModel();

        String getSerialnumber();

        String getFirmwarerevision();

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

        //return MiotError.OK;
    }

    @Override
    public MiotError onGet(Property property) {
        Log.e(TAG, "onGet");

        if (propertyGetter == null) {
            return super.onGet(property);
        }

        Miotspecv2Defined.Property p = Miotspecv2Defined.Property.valueOf(property.getDefinition().getType());
        switch (p) {
            case Manufacturer:
                property.setValue(propertyGetter.getManufacturer());
                break;
            case Model:
                property.setValue(propertyGetter.getModel());
                break;
            case Serialnumber:
                property.setValue(propertyGetter.getSerialnumber());
                break;
            case Firmwarerevision:
                property.setValue(propertyGetter.getFirmwarerevision());
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