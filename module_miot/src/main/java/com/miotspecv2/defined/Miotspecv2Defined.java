package com.miotspecv2.defined;

import android.util.Log;

import com.xiaomi.miot.typedef.urn.ActionType;
import com.xiaomi.miot.typedef.urn.EventType;
import com.xiaomi.miot.typedef.urn.PropertyType;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Miotspecv2Defined {

    private static final String TAG = "Miotspecv2Defined";
    private static final String DOMAIN = "miotspecv2";
    private static final String _UUID = "-0000-1000-2000-000000AABBCC";

    public static final int CUSTOMMODE_SERVICE_IID = 5;
    public static final int CUSTOMOVEN_SERVICE_IID = 3;
    public static final int CUSTOMRECIPES_SERVICE_IID = 4;
    public static final int SAFETYCHECK_SERVICE_IID = 6;
    public static final int OVEN_SERVICE_IID = 2;
    public static final int DEVICEINFORMATION_SERVICE_IID = 1;

    public static final int MODEONE_PROPERTY_IID = 1;
    public static final int MODETWO_PROPERTY_IID = 2;
    public static final int MODETHREE_PROPERTY_IID = 3;
    public static final int MODEFOUR_PROPERTY_IID = 4;
    public static final int MODEFIVE_PROPERTY_IID = 5;
    public static final int MODESIX_PROPERTY_IID = 6;
    public static final int MODESEVEN_PROPERTY_IID = 7;
    public static final int MODEEIGHT_PROPERTY_IID = 8;
    public static final int DISHID_PROPERTY_IID = 1;
    public static final int DISHNAME_PROPERTY_IID = 2;
    public static final int MODE_PROPERTY_IID = 3;
    public static final int TEMPZ_PROPERTY_IID = 4;
    public static final int TIMEZ_PROPERTY_IID = 5;
    public static final int TEMPK_PROPERTY_IID = 6;
    public static final int TIMEK_PROPERTY_IID = 7;
    public static final int DOORISOPEN_PROPERTY_IID = 8;
    public static final int WORKTOTALTIME_PROPERTY_IID = 11;
    public static final int FINISHTIME_PROPERTY_IID = 13;
    public static final int WATERTANK_PROPERTY_IID = 14;
    public static final int PREPARETIME_PROPERTY_IID = 15;
    public static final int OVENHARDWAREVER_PROPERTY_IID = 17;
    public static final int OVENLIGHT_PROPERTY_IID = 18;
    public static final int RECIPESTEP_PROPERTY_IID = 20;
    public static final int RECORD_PROPERTY_IID = 22;
    public static final int MICROLEVEL_PROPERTY_IID = 23;
    public static final int MODENAME_PROPERTY_IID = 24;
    public static final int MODESTEP_PROPERTY_IID = 26;
    public static final int MICROTIME_PROPERTY_IID = 27;
    public static final int MIXMODEINDICATOR_PROPERTY_IID = 29;
    public static final int WATERTANKISCLOSE_PROPERTY_IID = 30;
    public static final int SCREENCUSRECIPES_PROPERTY_IID = 31;
    public static final int RECIPEONE_PROPERTY_IID = 1;
    public static final int RECIPETWO_PROPERTY_IID = 2;
    public static final int RECIPETHREE_PROPERTY_IID = 3;
    public static final int RECIPEFOUR_PROPERTY_IID = 4;
    public static final int RECIPEFIVE_PROPERTY_IID = 5;
    public static final int RECIPESIX_PROPERTY_IID = 6;
    public static final int RECIPESEVEN_PROPERTY_IID = 7;
    public static final int RECIPEEIGHT_PROPERTY_IID = 8;
    public static final int CHECKCODE_PROPERTY_IID = 1;
    public static final int STATUS_PROPERTY_IID = 1;
    public static final int FAULT_PROPERTY_IID = 2;
    public static final int LEFTTIME_PROPERTY_IID = 7;
    public static final int WORKINGTIME_PROPERTY_IID = 8;
    public static final int TEMPERATURE_PROPERTY_IID = 10;
    public static final int MANUFACTURER_PROPERTY_IID = 1;
    public static final int MODEL_PROPERTY_IID = 2;
    public static final int SERIALNUMBER_PROPERTY_IID = 3;
    public static final int FIRMWAREREVISION_PROPERTY_IID = 4;

    public static final int PREPARE_ACTION_IID = 10;
    public static final int CANCELPREPARE_ACTION_IID = 8;
    public static final int CLEARFAULT_ACTION_IID = 9;
    public static final int STARTCOOK_ACTION_IID = 1;
    public static final int CANCELCOOKING_ACTION_IID = 2;
    public static final int PAUSE_ACTION_IID = 3;

    public static final int BOOKSTART_EVENT_IID = 2;
    public static final int WORKRECORD_EVENT_IID = 1;
    public static final int LOW_WATER_LEVEL_EVENT_IID =1;


    private Miotspecv2Defined() {
    }

    /**
     * Properties
     * urn:miotspecv2:property:modeone:00000001:Viomiso7:1
     * urn:miotspecv2:property:modetwo:00000002:Viomiso7:1
     * urn:miotspecv2:property:modethree:00000003:Viomiso7:1
     * urn:miotspecv2:property:modefour:00000004:Viomiso7:1
     * urn:miotspecv2:property:modefive:00000005:Viomiso7:1
     * urn:miotspecv2:property:modesix:00000006:Viomiso7:1
     * urn:miotspecv2:property:modeseven:00000007:Viomiso7:1
     * urn:miotspecv2:property:modeeight:00000008:Viomiso7:1
     * urn:miotspecv2:property:dishid:00000001:Viomiso7:1
     * urn:miotspecv2:property:dishname:00000002:Viomiso7:1
     * urn:miotspecv2:property:mode:00000003:Viomiso7:1
     * urn:miotspecv2:property:tempz:00000004:Viomiso7:1
     * urn:miotspecv2:property:timez:00000005:Viomiso7:1
     * urn:miotspecv2:property:tempk:00000006:Viomiso7:1
     * urn:miotspecv2:property:timek:00000007:Viomiso7:1
     * urn:miotspecv2:property:doorisopen:00000008:Viomiso7:1
     * urn:miotspecv2:property:worktotaltime:0000000b:Viomiso7:1
     * urn:miotspecv2:property:finishtime:0000000d:Viomiso7:1
     * urn:miotspecv2:property:watertank:0000000e:Viomiso7:1
     * urn:miotspecv2:property:preparetime:0000000f:Viomiso7:1
     * urn:miotspecv2:property:ovenhardwarever:00000010:Viomiso7:1
     * urn:miotspecv2:property:ovenlight:00000011:Viomiso7:1
     * urn:miotspecv2:property:recipestep:00000013:Viomiso7:1
     * urn:miotspecv2:property:record:00000015:Viomiso7:1
     * urn:miotspecv2:property:microlevel:00000016:Viomiso7:1
     * urn:miotspecv2:property:modename:00000017:Viomiso7:1
     * urn:miotspecv2:property:modestep:00000019:Viomiso7:1
     * urn:miotspecv2:property:microtime:0000001a:Viomiso7:1
     * urn:miotspecv2:property:mixmodeindicator:0000000c:Viomiso7:1
     * urn:miotspecv2:property:watertankisclose:00000009:Viomiso7:1
     * urn:miotspecv2:property:screencusrecipes:0000000a:Viomiso7:1
     * urn:miotspecv2:property:recipeone:00000001:Viomiso7:1
     * urn:miotspecv2:property:recipetwo:00000002:Viomiso7:1
     * urn:miotspecv2:property:recipethree:00000003:Viomiso7:1
     * urn:miotspecv2:property:recipefour:00000004:Viomiso7:1
     * urn:miotspecv2:property:recipefive:00000005:Viomiso7:1
     * urn:miotspecv2:property:recipesix:00000006:Viomiso7:1
     * urn:miotspecv2:property:recipeseven:00000007:Viomiso7:1
     * urn:miotspecv2:property:recipeeight:00000008:Viomiso7:1
     * urn:miotspecv2:property:checkcode:00000001:Viomiso7:1
     * urn:miotspecv2:property:status:00000007:Viomiso7:1
     * urn:miotspecv2:property:fault:00000009:Viomiso7:1
     * urn:miotspecv2:property:lefttime:0000003C:Viomiso7:1
     * urn:miotspecv2:property:workingtime:00000079:Viomiso7:1
     * urn:miotspecv2:property:temperature:00000020:Viomiso7:1
     * urn:miotspecv2:property:manufacturer:00000001:Viomiso7:1
     * urn:miotspecv2:property:model:00000002:Viomiso7:1
     * urn:miotspecv2:property:serialnumber:00000003:Viomiso7:1
     * urn:miotspecv2:property:firmwarerevision:00000005:Viomiso7:1
     */
    public enum Property {
        Undefined(0),
        Modeone(1),
        Modetwo(2),
        Modethree(3),
        Modefour(4),
        Modefive(5),
        Modesix(6),
        Modeseven(7),
        Modeeight(8),
        Dishid(9),
        Dishname(10),
        Mode(11),
        Tempz(12),
        Timez(13),
        Tempk(14),
        Timek(15),
        Doorisopen(16),
        Worktotaltime(17),
        Finishtime(18),
        Watertank(19),
        Preparetime(20),
        Ovenhardwarever(21),
        Ovenlight(22),
        Recipestep(23),
        Record(24),
        Microlevel(25),
        Modename(26),
        Modestep(27),
        Microtime(28),
        Mixmodeindicator(29),
        Watertankisclose(30),
        Screencusrecipes(31),
        Recipeone(32),
        Recipetwo(33),
        Recipethree(34),
        Recipefour(35),
        Recipefive(36),
        Recipesix(37),
        Recipeseven(38),
        Recipeeight(39),
        Checkcode(40),
        Status(41),
        Fault(42),
        Lefttime(43),
        Workingtime(44),
        Temperature(45),
        Manufacturer(46),
        Model(47),
        Serialnumber(48),
        Firmwarerevision(49);

        private final int value;

        Property(int value) {
            this.value = value;
        }

        public static Property valueOf(int value) {
            for (Property c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Property valueOf(PropertyType type) {
            if (!type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Property c : values()) {
                if (c.toString().equals(type.getSubType())) {
                    return c;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public PropertyType toPropertyType() {
            return new PropertyType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }

    /**
     * Actions
     * urn:miotspecv2:action:prepare:00002806:Viomiso7:1
     * urn:miotspecv2:action:cancelprepare:00002803:Viomiso7:1
     * urn:miotspecv2:action:clearfault:00002805:Viomiso7:1
     * urn:miotspecv2:action:startcook:00002806:Viomiso7:1
     * urn:miotspecv2:action:cancelcooking:00002807:Viomiso7:1
     * urn:miotspecv2:action:pause:0000280C:Viomiso7:1
     * ...
     */
    public enum Action {
        Undefined(0),
        Prepare(1),
        Cancelprepare(2),
        Clearfault(3),
        Startcook(4),
        Cancelcooking(5),
        Pause(6);

        private final int value;

        Action(int value) {
            this.value = value;
        }

        public static Action valueOf(int value) {
            for (Action c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Action valueOf(ActionType type) {
            if (!type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Action v : values()) {
                if (v.toString().equals(type.getSubType())) {
                    return v;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public ActionType toActionType() {
            return new ActionType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }

    /**
     * Events
     * urn:viomi-spec:event:bookstart:00005002:Viomiso7:1
     * urn:viomi-spec:event:workrecord:00005001:Viomiso7:1
     * urn:miot-spec-v2:event:low-water-level:0000500A:Viomiso7:1
     * ...
     */
    public enum Event {
        Undefined(0),
        Bookstart(1),
        Workrecord(2),
        LowWaterLevel(3);

        private final int value;

        Event(int value) {
            this.value = value;
        }

        public static Event valueOf(int value) {
            for (Event c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Event valueOf(EventType type) {
            if (!type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Event v : values()) {
                if (v.toString().equals(type.getSubType())) {
                    return v;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public EventType toEventType() {
            return new EventType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }

    /**
     * Servics
     * urn:miotspecv2:service:custommode:00007803:Viomiso7:1
     * urn:miotspecv2:service:customoven:00007801:Viomiso7:1
     * urn:miotspecv2:service:customrecipes:00007802:Viomiso7:1
     * urn:miotspecv2:service:safetycheck:00007804:Viomiso7:1
     * urn:miotspecv2:service:oven:00007862:Viomiso7:1
     * urn:miotspecv2:service:deviceinformation:00007801:Viomiso7:1
     */
    public enum Service {
        Undefined(0),
        Custommode(1),
        Customoven(2),
        Customrecipes(3),
        Safetycheck(4),
        Oven(5),
        Deviceinformation(6);

        private final int value;

        Service(int value) {
            this.value = value;
        }

        public static Service valueOf(int value) {
            for (Service c : values()) {
                if (c.value() == value) {
                    return c;
                }
            }

            Log.e(TAG, "invalid value: " + value);

            return Undefined;
        }

        public static Service valueOf(ServiceType type) {
            if (!type.getDomain().equals(DOMAIN)) {
                return Undefined;
            }

            for (Service v : values()) {
                if (v.toString().equals(type.getSubType())) {
                    return v;
                }
            }

            return Undefined;
        }

        public int value() {
            return value;
        }

        public ServiceType toServiceType() {
            return new ServiceType(DOMAIN, this.toString(), toShortUUID());
        }

        public String toUUID() {
            return String.format("%08X%s", value, _UUID);
        }

        public String toShortUUID() {
            return String.format("%04X", value);
        }
    }
}