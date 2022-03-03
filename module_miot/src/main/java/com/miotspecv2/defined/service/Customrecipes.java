package com.miotspecv2.defined.service;

import android.util.Log;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.property.Recipeone;
import com.miotspecv2.defined.property.Recipetwo;
import com.miotspecv2.defined.property.Recipethree;
import com.miotspecv2.defined.property.Recipefour;
import com.miotspecv2.defined.property.Recipefive;
import com.miotspecv2.defined.property.Recipesix;
import com.miotspecv2.defined.property.Recipeseven;
import com.miotspecv2.defined.property.Recipeeight;
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

public class Customrecipes extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Customrecipes.toServiceType();
    private static final String TAG = "Customrecipes";

    public Customrecipes(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.CUSTOMRECIPES_SERVICE_IID);

        super.addProperty(new Recipeone());
        super.addProperty(new Recipetwo());
        super.addProperty(new Recipethree());
        super.addProperty(new Recipefour());
        super.addProperty(new Recipefive());
        super.addProperty(new Recipesix());
        super.addProperty(new Recipeseven());
        super.addProperty(new Recipeeight());

        if (hasOptionalProperty) {
        }


    }

    /**
     * Properties
     */
    public Recipeone recipeone() {
        Property p = super.getProperty(Recipeone.TYPE);
        if (p != null) {
            if (p instanceof Recipeone) {
                return (Recipeone) p;
            }
        }

        return null;
    }
    public Recipetwo recipetwo() {
        Property p = super.getProperty(Recipetwo.TYPE);
        if (p != null) {
            if (p instanceof Recipetwo) {
                return (Recipetwo) p;
            }
        }

        return null;
    }
    public Recipethree recipethree() {
        Property p = super.getProperty(Recipethree.TYPE);
        if (p != null) {
            if (p instanceof Recipethree) {
                return (Recipethree) p;
            }
        }

        return null;
    }
    public Recipefour recipefour() {
        Property p = super.getProperty(Recipefour.TYPE);
        if (p != null) {
            if (p instanceof Recipefour) {
                return (Recipefour) p;
            }
        }

        return null;
    }
    public Recipefive recipefive() {
        Property p = super.getProperty(Recipefive.TYPE);
        if (p != null) {
            if (p instanceof Recipefive) {
                return (Recipefive) p;
            }
        }

        return null;
    }
    public Recipesix recipesix() {
        Property p = super.getProperty(Recipesix.TYPE);
        if (p != null) {
            if (p instanceof Recipesix) {
                return (Recipesix) p;
            }
        }

        return null;
    }
    public Recipeseven recipeseven() {
        Property p = super.getProperty(Recipeseven.TYPE);
        if (p != null) {
            if (p instanceof Recipeseven) {
                return (Recipeseven) p;
            }
        }

        return null;
    }
    public Recipeeight recipeeight() {
        Property p = super.getProperty(Recipeeight.TYPE);
        if (p != null) {
            if (p instanceof Recipeeight) {
                return (Recipeeight) p;
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
        String getRecipeone();

        String getRecipetwo();

        String getRecipethree();

        String getRecipefour();

        String getRecipefive();

        String getRecipesix();

        String getRecipeseven();

        String getRecipeeight();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setRecipeone(String value);

        void setRecipetwo(String value);

        void setRecipethree(String value);

        void setRecipefour(String value);

        void setRecipefive(String value);

        void setRecipesix(String value);

        void setRecipeseven(String value);

        void setRecipeeight(String value);

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
            case Recipeone:
                propertySetter.setRecipeone(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipetwo:
                propertySetter.setRecipetwo(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipethree:
                propertySetter.setRecipethree(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipefour:
                propertySetter.setRecipefour(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipefive:
                propertySetter.setRecipefive(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipesix:
                propertySetter.setRecipesix(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipeseven:
                propertySetter.setRecipeseven(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Recipeeight:
                propertySetter.setRecipeeight(((Vstring) property.getCurrentValue()).getValue());
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
            case Recipeone:
                property.setValue(propertyGetter.getRecipeone());
                break;
            case Recipetwo:
                property.setValue(propertyGetter.getRecipetwo());
                break;
            case Recipethree:
                property.setValue(propertyGetter.getRecipethree());
                break;
            case Recipefour:
                property.setValue(propertyGetter.getRecipefour());
                break;
            case Recipefive:
                property.setValue(propertyGetter.getRecipefive());
                break;
            case Recipesix:
                property.setValue(propertyGetter.getRecipesix());
                break;
            case Recipeseven:
                property.setValue(propertyGetter.getRecipeseven());
                break;
            case Recipeeight:
                property.setValue(propertyGetter.getRecipeeight());
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