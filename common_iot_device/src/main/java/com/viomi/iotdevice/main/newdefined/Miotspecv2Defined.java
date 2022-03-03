package com.viomi.iotdevice.main.newdefined;

import android.util.Log;

import com.xiaomi.miot.typedef.urn.ActionType;
import com.xiaomi.miot.typedef.urn.PropertyType;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class Miotspecv2Defined {

    private static final String TAG = "Miotspecv2Defined";
    private static final String DOMAIN = "miotspecv2";
    private static final String _UUID = "-0000-1000-2000-000000AABBCC";

    public static final int FRIDGEFACTORY_SERVICE_IID = 6;
    public static final int FRIDGE_SERVICE_IID = 2;
    public static final int FRIDGECHAMBER_SERVICE_IID = 3;
    public static final int FRIDGECHAMBER_ANOTHER_SERVICE_IID = 4;
    public static final int FRIDGEPANEL_SERVICE_IID = 5;
    public static final int DEVICEINFORMATION_SERVICE_IID = 1;

    public static final int FROSTTEMP_PROPERTY_IID = 1;
    public static final int FORCEDFROST_PROPERTY_IID = 2;
    public static final int FORCENONSTOP_PROPERTY_IID = 3;
    public static final int HEATINGWIRE_PROPERTY_IID = 4;
    public static final int FAULT_PROPERTY_IID = 1;
    public static final int MODE_PROPERTY_IID = 4;
    public static final int FRIDGE_TEMPERATURE_PROPERTY_IID = 5;
    public static final int TEMPERATURE_PROPERTY_IID = 1;
    public static final int TARGETTEMPERATURE_PROPERTY_IID = 2;
    public static final int ON_PROPERTY_IID = 3;
    public static final int CHAMBER_TEMPERATURE_PROPERTY_IID = 1;
    public static final int CHAMBER_TARGETTEMPERATURE_PROPERTY_IID = 2;
    public static final int CHAMBER_ON_PROPERTY_IID = 3;
    public static final int QUICKCOOLING_PROPERTY_IID = 1;
    public static final int QUICKFREEZE_PROPERTY_IID = 2;
    public static final int MANUFACTURER_PROPERTY_IID = 1;
    public static final int MODEL_PROPERTY_IID = 2;
    public static final int SERIALNUMBER_PROPERTY_IID = 3;
    public static final int FIRMWAREREVISION_PROPERTY_IID = 4;

    public static final int TESTFROST_ACTION_IID = 1;


    private Miotspecv2Defined() {
    }

    /**
     * Properties
     * urn:miotspecv2:property:frosttemp:00000001
     * urn:miotspecv2:property:forcedfrost:00000002
     * urn:miotspecv2:property:forcenonstop:00000003
     * urn:miotspecv2:property:heatingwire:00000004
     * urn:miotspecv2:property:fault:00000009
     * urn:miotspecv2:property:mode:00000008
     * urn:miotspecv2:property:temperature:00000020
     * urn:miotspecv2:property:temperature:00000020
     * urn:miotspecv2:property:targettemperature:00000021
     * urn:miotspecv2:property:on:00000006
     * urn:miotspecv2:property:quickcooling:00000001
     * urn:miotspecv2:property:quickfreeze:00000002
     * urn:miotspecv2:property:manufacturer:00000001
     * urn:miotspecv2:property:model:00000002
     * urn:miotspecv2:property:serialnumber:00000003
     * urn:miotspecv2:property:firmwarerevision:00000005
     */
    public enum Property {
        Undefined(0),
        Frosttemp(1),
        Forcedfrost(2),
        Forcenonstop(3),
        Heatingwire(4),
        Fault(5),
        Mode(6),
        FridgeTemperature(7),
        Temperature(8),
        Targettemperature(9),
        On(10),
        FridgechamberTemperature(11),
        FridgechamberTargettemperature(12),
        FridgechamberOn(13),
        Quickcooling(14),
        Quickfreeze(15),
        Manufacturer(16),
        Model(17),
        Serialnumber(18),
        Firmwarerevision(19);

        private int value;

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
     * urn:miotspecv2:action:testfrost:00002801
     * ...
     */
    public enum Action {
        Undefined(0),
        Testfrost(1);

        private int value;

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
            if (! type.getDomain().equals(DOMAIN)) {
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
     * Servics
     * urn:miotspecv2:service:fridgefactory:00007802
     * urn:miotspecv2:service:fridge:00007819
     * urn:miotspecv2:service:fridgechamber:0000781A
     * urn:miotspecv2:service:fridgepanel:00007801
     * urn:miotspecv2:service:deviceinformation:00007801
     */
    public enum Service {
        Undefined(0),
        Fridgefactory(1),
        Fridge(2),
        Fridgechamber(3),
        FridgechamberAnother(4),
        Fridgepanel(5),
        Deviceinformation(6);


        private int value;

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
            if (! type.getDomain().equals(DOMAIN)) {
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