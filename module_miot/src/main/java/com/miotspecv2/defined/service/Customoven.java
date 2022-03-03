package com.miotspecv2.defined.service;

import android.util.Log;

import com.miotspecv2.defined.Miotspecv2Defined;
import com.miotspecv2.defined.action.Cancelprepare;
import com.miotspecv2.defined.action.Clearfault;
import com.miotspecv2.defined.action.Prepare;
import com.miotspecv2.defined.event.Bookstart;
import com.miotspecv2.defined.event.Workrecord;
import com.miotspecv2.defined.property.Dishid;
import com.miotspecv2.defined.property.Dishname;
import com.miotspecv2.defined.property.Doorisopen;
import com.miotspecv2.defined.property.Finishtime;
import com.miotspecv2.defined.property.Microlevel;
import com.miotspecv2.defined.property.Microtime;
import com.miotspecv2.defined.property.Mixmodeindicator;
import com.miotspecv2.defined.property.Mode;
import com.miotspecv2.defined.property.Modename;
import com.miotspecv2.defined.property.Modestep;
import com.miotspecv2.defined.property.Ovenhardwarever;
import com.miotspecv2.defined.property.Ovenlight;
import com.miotspecv2.defined.property.Preparetime;
import com.miotspecv2.defined.property.Recipestep;
import com.miotspecv2.defined.property.Record;
import com.miotspecv2.defined.property.Screencusrecipes;
import com.miotspecv2.defined.property.Tempk;
import com.miotspecv2.defined.property.Tempz;
import com.miotspecv2.defined.property.Timek;
import com.miotspecv2.defined.property.Timez;
import com.miotspecv2.defined.property.Watertank;
import com.miotspecv2.defined.property.Watertankisclose;
import com.miotspecv2.defined.property.Worktotaltime;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.data.value.Vuint16;
import com.xiaomi.miot.typedef.data.value.Vuint32;
import com.xiaomi.miot.typedef.data.value.Vuint8;
import com.xiaomi.miot.typedef.device.Action;
import com.xiaomi.miot.typedef.device.ActionInfo;
import com.xiaomi.miot.typedef.device.operable.ServiceOperable;
import com.xiaomi.miot.typedef.error.MiotError;
import com.xiaomi.miot.typedef.property.Property;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Customoven extends ServiceOperable {

    public static final ServiceType TYPE = Miotspecv2Defined.Service.Customoven.toServiceType();
    private static final String TAG = "Customoven";

    public Customoven(boolean hasOptionalProperty) {
        super(TYPE);

        super.setInstanceID(Miotspecv2Defined.CUSTOMOVEN_SERVICE_IID);

        super.addProperty(new Dishid());
        super.addProperty(new Dishname());
        super.addProperty(new Mode());
        super.addProperty(new Tempz());
        super.addProperty(new Timez());
        super.addProperty(new Tempk());
        super.addProperty(new Timek());
        super.addProperty(new Doorisopen());
        super.addProperty(new Worktotaltime());
        super.addProperty(new Finishtime());
        super.addProperty(new Watertank());
        super.addProperty(new Preparetime());
        super.addProperty(new Ovenhardwarever());
        super.addProperty(new Ovenlight());
        super.addProperty(new Recipestep());
        super.addProperty(new Record());
        super.addProperty(new Microlevel());
        super.addProperty(new Modename());
        super.addProperty(new Modestep());
        super.addProperty(new Microtime());
        super.addProperty(new Mixmodeindicator());
        super.addProperty(new Watertankisclose());
        super.addProperty(new Screencusrecipes());

        if (hasOptionalProperty) {
        }

        super.addAction(new Prepare());
        super.addAction(new Cancelprepare());
        super.addAction(new Clearfault());

        super.addEvent(new Bookstart());
        super.addEvent(new Workrecord());
    }

    /**
     * Properties
     */
    public Dishid dishid() {
        Property p = super.getProperty(Dishid.TYPE);
        if (p != null) {
            if (p instanceof Dishid) {
                return (Dishid) p;
            }
        }

        return null;
    }
    public Dishname dishname() {
        Property p = super.getProperty(Dishname.TYPE);
        if (p != null) {
            if (p instanceof Dishname) {
                return (Dishname) p;
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
    public Tempz tempz() {
        Property p = super.getProperty(Tempz.TYPE);
        if (p != null) {
            if (p instanceof Tempz) {
                return (Tempz) p;
            }
        }

        return null;
    }
    public Timez timez() {
        Property p = super.getProperty(Timez.TYPE);
        if (p != null) {
            if (p instanceof Timez) {
                return (Timez) p;
            }
        }

        return null;
    }
    public Tempk tempk() {
        Property p = super.getProperty(Tempk.TYPE);
        if (p != null) {
            if (p instanceof Tempk) {
                return (Tempk) p;
            }
        }

        return null;
    }
    public Timek timek() {
        Property p = super.getProperty(Timek.TYPE);
        if (p != null) {
            if (p instanceof Timek) {
                return (Timek) p;
            }
        }

        return null;
    }
    public Doorisopen doorisopen() {
        Property p = super.getProperty(Doorisopen.TYPE);
        if (p != null) {
            if (p instanceof Doorisopen) {
                return (Doorisopen) p;
            }
        }

        return null;
    }
    public Worktotaltime worktotaltime() {
        Property p = super.getProperty(Worktotaltime.TYPE);
        if (p != null) {
            if (p instanceof Worktotaltime) {
                return (Worktotaltime) p;
            }
        }

        return null;
    }
    public Finishtime finishtime() {
        Property p = super.getProperty(Finishtime.TYPE);
        if (p != null) {
            if (p instanceof Finishtime) {
                return (Finishtime) p;
            }
        }

        return null;
    }
    public Watertank watertank() {
        Property p = super.getProperty(Watertank.TYPE);
        if (p != null) {
            if (p instanceof Watertank) {
                return (Watertank) p;
            }
        }

        return null;
    }
    public Preparetime preparetime() {
        Property p = super.getProperty(Preparetime.TYPE);
        if (p != null) {
            if (p instanceof Preparetime) {
                return (Preparetime) p;
            }
        }

        return null;
    }
    public Ovenhardwarever ovenhardwarever() {
        Property p = super.getProperty(Ovenhardwarever.TYPE);
        if (p != null) {
            if (p instanceof Ovenhardwarever) {
                return (Ovenhardwarever) p;
            }
        }

        return null;
    }
    public Ovenlight ovenlight() {
        Property p = super.getProperty(Ovenlight.TYPE);
        if (p != null) {
            if (p instanceof Ovenlight) {
                return (Ovenlight) p;
            }
        }

        return null;
    }
    public Recipestep recipestep() {
        Property p = super.getProperty(Recipestep.TYPE);
        if (p != null) {
            if (p instanceof Recipestep) {
                return (Recipestep) p;
            }
        }

        return null;
    }
    public Record record() {
        Property p = super.getProperty(Record.TYPE);
        if (p != null) {
            if (p instanceof Record) {
                return (Record) p;
            }
        }

        return null;
    }
    public Microlevel microlevel() {
        Property p = super.getProperty(Microlevel.TYPE);
        if (p != null) {
            if (p instanceof Microlevel) {
                return (Microlevel) p;
            }
        }

        return null;
    }
    public Modename modename() {
        Property p = super.getProperty(Modename.TYPE);
        if (p != null) {
            if (p instanceof Modename) {
                return (Modename) p;
            }
        }

        return null;
    }
    public Modestep modestep() {
        Property p = super.getProperty(Modestep.TYPE);
        if (p != null) {
            if (p instanceof Modestep) {
                return (Modestep) p;
            }
        }

        return null;
    }
    public Microtime microtime() {
        Property p = super.getProperty(Microtime.TYPE);
        if (p != null) {
            if (p instanceof Microtime) {
                return (Microtime) p;
            }
        }

        return null;
    }
    public Mixmodeindicator mixmodeindicator() {
        Property p = super.getProperty(Mixmodeindicator.TYPE);
        if (p != null) {
            if (p instanceof Mixmodeindicator) {
                return (Mixmodeindicator) p;
            }
        }

        return null;
    }
    public Watertankisclose watertankisclose() {
        Property p = super.getProperty(Watertankisclose.TYPE);
        if (p != null) {
            if (p instanceof Watertankisclose) {
                return (Watertankisclose) p;
            }
        }

        return null;
    }
    public Screencusrecipes screencusrecipes() {
        Property p = super.getProperty(Screencusrecipes.TYPE);
        if (p != null) {
            if (p instanceof Screencusrecipes) {
                return (Screencusrecipes) p;
            }
        }

        return null;
    }

    /**
     * Actions
     */
    public Prepare prepare(){
        Action a = super.getAction(Prepare.TYPE);
        if (a != null) {
            if (a instanceof Prepare) {
                return (Prepare) a;
            }
        }

        return null;
    }
    public Cancelprepare cancelprepare(){
        Action a = super.getAction(Cancelprepare.TYPE);
        if (a != null) {
            if (a instanceof Cancelprepare) {
                return (Cancelprepare) a;
            }
        }

        return null;
    }
    public Clearfault clearfault(){
        Action a = super.getAction(Clearfault.TYPE);
        if (a != null) {
            if (a instanceof Clearfault) {
                return (Clearfault) a;
            }
        }

        return null;
    }

    /**
     * PropertyGetter
     */
    public interface PropertyGetter {
        int getDishid();

        String getDishname();

        int getMode();

        int getTempz();

        int getTimez();

        int getTempk();

        int getTimek();

        boolean getDoorisopen();

        int getWorktotaltime();

        int getFinishtime();

        boolean getWatertank();

        int getPreparetime();

        String getOvenhardwarever();

        boolean getOvenlight();

        int getRecipestep();

        int getRecord();

        int getMicrolevel();

        String getModename();

        int getModestep();

        int getMicrotime();

        int getMixmodeindicator();

        boolean getWatertankisclose();

        String getScreencusrecipes();

    }

    /**
     * PropertySetter
     */
    public interface PropertySetter {
        void setDishid(int value);

        void setDishname(String value);

        void setMode(int value);

        void setTempz(int value);

        void setTimez(int value);

        void setTempk(int value);

        void setTimek(int value);

        void setPreparetime(int value);

        void setOvenlight(boolean value);

        void setRecord(int value);

        void setMicrolevel(int value);

        void setModename(String value);

        void setMicrotime(int value);

        void setScreencusrecipes(String value);

    }

    /**
     * ActionsHandler
     */
    public interface ActionHandler {
        void onPrepare(int dishid, int preparetime);
        void onCancelprepare();
        void onClearfault();
    }


    private MiotError onPrepare(ActionInfo action) {
        int pDishid = ((Vuint8) action.getArgumentValue(Dishid.TYPE)).getValue();
        int pPreparetime = ((Vuint16) action.getArgumentValue(Preparetime.TYPE)).getValue();
        actionHandler.onPrepare(pDishid, pPreparetime);

        return MiotError.OK;
    }
    private MiotError onCancelprepare(ActionInfo action) {
        actionHandler.onCancelprepare();

        return MiotError.OK;
    }
    private MiotError onClearfault(ActionInfo action) {
        actionHandler.onClearfault();

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
            case Dishid:
                propertySetter.setDishid(((Vuint8) property.getCurrentValue()).getValue());
                break;
            case Dishname:
                propertySetter.setDishname(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Mode:
                propertySetter.setMode(((Vuint8) property.getCurrentValue()).getValue());
                break;
            case Tempz:
                propertySetter.setTempz(((Vuint16) property.getCurrentValue()).getValue());
                break;
            case Timez:
                propertySetter.setTimez(((Vuint32) property.getCurrentValue()).getValue());
                break;
            case Tempk:
                propertySetter.setTempk(((Vuint16) property.getCurrentValue()).getValue());
                break;
            case Timek:
                propertySetter.setTimek(((Vuint32) property.getCurrentValue()).getValue());
                break;
            case Preparetime:
                propertySetter.setPreparetime(((Vuint16) property.getCurrentValue()).getValue());
                break;
            case Ovenlight:
                propertySetter.setOvenlight(((Vbool) property.getCurrentValue()).getValue());
                break;
            case Record:
                propertySetter.setRecord(((Vuint8) property.getCurrentValue()).getValue());
                break;
            case Microlevel:
                propertySetter.setMicrolevel(((Vuint8) property.getCurrentValue()).getValue());
                break;
            case Modename:
                propertySetter.setModename(((Vstring) property.getCurrentValue()).getValue());
                break;
            case Microtime:
                propertySetter.setMicrotime(((Vuint16) property.getCurrentValue()).getValue());
                break;
            case Screencusrecipes:
                propertySetter.setScreencusrecipes(((Vstring) property.getCurrentValue()).getValue());
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
            case Dishid:
                property.setValue(propertyGetter.getDishid());
                break;
            case Dishname:
                property.setValue(propertyGetter.getDishname());
                break;
            case Mode:
                property.setValue(propertyGetter.getMode());
                break;
            case Tempz:
                property.setValue(propertyGetter.getTempz());
                break;
            case Timez:
                property.setValue(propertyGetter.getTimez());
                break;
            case Tempk:
                property.setValue(propertyGetter.getTempk());
                break;
            case Timek:
                property.setValue(propertyGetter.getTimek());
                break;
            case Doorisopen:
                property.setValue(propertyGetter.getDoorisopen());
                break;
            case Worktotaltime:
                property.setValue(propertyGetter.getWorktotaltime());
                break;
            case Finishtime:
                property.setValue(propertyGetter.getFinishtime());
                break;
            case Watertank:
                property.setValue(propertyGetter.getWatertank());
                break;
            case Preparetime:
                property.setValue(propertyGetter.getPreparetime());
                break;
            case Ovenhardwarever:
                property.setValue(propertyGetter.getOvenhardwarever());
                break;
            case Ovenlight:
                property.setValue(propertyGetter.getOvenlight());
                break;
            case Recipestep:
                property.setValue(propertyGetter.getRecipestep());
                break;
            case Record:
                property.setValue(propertyGetter.getRecord());
                break;
            case Microlevel:
                property.setValue(propertyGetter.getMicrolevel());
                break;
            case Modename:
                property.setValue(propertyGetter.getModename());
                break;
            case Modestep:
                property.setValue(propertyGetter.getModestep());
                break;
            case Microtime:
                property.setValue(propertyGetter.getMicrotime());
                break;
            case Mixmodeindicator:
                property.setValue(propertyGetter.getMixmodeindicator());
                break;
            case Watertankisclose:
                property.setValue(propertyGetter.getWatertankisclose());
                break;
            case Screencusrecipes:
                property.setValue(propertyGetter.getScreencusrecipes());
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
            case Prepare:
                return onPrepare(action);
            case Cancelprepare:
                return onCancelprepare(action);
            case Clearfault:
                return onClearfault(action);

            default:
                Log.e(TAG, "invalid action: " + a);
                break;
        }

        return MiotError.IOT_RESOURCE_NOT_EXIST;
    }
}