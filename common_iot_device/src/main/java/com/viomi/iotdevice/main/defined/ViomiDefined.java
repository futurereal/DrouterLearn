package com.viomi.iotdevice.main.defined;

import android.util.Log;

import com.xiaomi.miot.typedef.urn.ActionType;
import com.xiaomi.miot.typedef.urn.PropertyType;
import com.xiaomi.miot.typedef.urn.ServiceType;

public class ViomiDefined {

    private static final String TAG = "ViomiDefined";
    private static final String DOMAIN = "Viomi";
    private static final String _UUID = "-0000-1000-2000-000000AABBCC";

    private ViomiDefined() {
    }

    /**
     * Properties
     * urn:Viomi:property:power:0000
     * urn:Viomi:property:mode:0001
     * urn:Viomi:property:wind_level:0002
     */
    public enum Property {
        Undefined(0),
        Power(1),
        Mode(2),
        Wind_level(3);

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
     * urn:Viomi:action:set_wind_level:0001
     * urn:Viomi:action:set_power:0000
     * ...
     */
    public enum Action {
        Undefined(0),
        Set_wind_level(1),
        Set_power(2);

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
     * urn:Viomi:service:AirconditionService:0000
     */
    public enum Service {
        Undefined(0),
        AirconditionService(1);

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