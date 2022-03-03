package com.viomi.iotdevice.main.newmiot.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.viomi.iotdevice.main.iot_device_lib.ViomiApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class DevRepository {
    private static DevRepository mInstance;
    private DevProp mDevProp;
    private static final String name = "deviceInfo";

    public static DevRepository getInstance() {
        if (mInstance == null) {
            synchronized (DevRepository.class) {
                if (mInstance == null) {
                    mInstance = new DevRepository();
                }
            }
        }
        return mInstance;
    }

    DevRepository() {
        mDevProp = new DevProp();
        toJsonObject();
    }

    public DevProp getDevProp() {
        return mDevProp;
    }

    public void saveJsonString() {
        SharedPreferences sp = getSharedPreferences();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("manufacturer", this.mDevProp.manufacturer_1_1);
            jsonObject.put("model", this.mDevProp.model_1_2);
            jsonObject.put("serialnumber", this.mDevProp.serialnumber_1_3);
            jsonObject.put("firmwarerevision", this.mDevProp.firmwarerevision_1_4);
            jsonObject.put("fault", this.mDevProp.fault_2_1);
            jsonObject.put("mode", this.mDevProp.mode_2_4);
            jsonObject.put("temperature", this.mDevProp.temperature_2_5);
            jsonObject.put("fridgetemperature", this.mDevProp.fridgetemperature_3_1);
            jsonObject.put("targettemperature", this.mDevProp.targettemperature_3_2);
            jsonObject.put("on", this.mDevProp.on_3_3);
            jsonObject.put("temperatureanother", this.mDevProp.temperatureanother_4_1);
            jsonObject.put("targettemperatureanother", this.mDevProp.targettemperatureanother_4_2);
            jsonObject.put("onanother", this.mDevProp.onanother_4_3);
            jsonObject.put("quickcooling", this.mDevProp.quickcooling_5_1);
            jsonObject.put("quickfreeze", this.mDevProp.quickfreeze5_2);
            jsonObject.put("frosttemp", this.mDevProp.frosttemp_6_1);
            jsonObject.put("forcedfrost", this.mDevProp.forcedfrost_6_2);
            jsonObject.put("forcenonstop", this.mDevProp.forcenonstop_6_3);
            jsonObject.put("heatingwire", this.mDevProp.heatingwire_6_4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("DevProperty", jsonObject.toString());
        editor.apply();
    }

    private void toJsonObject() {
        SharedPreferences sp = getSharedPreferences();
        String jsonString = sp.getString("DevProperty", "");
        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(jsonString);
            this.mDevProp.manufacturer_1_1 = object.getString("manufacturer");
            this.mDevProp.model_1_2 = object.getString("model");
            this.mDevProp.serialnumber_1_3 = object.getString("serialnumber");
            this.mDevProp.firmwarerevision_1_4 = object.getString("firmwarerevision");
            this.mDevProp.fault_2_1 = object.getInt("fault");
            this.mDevProp.mode_2_4 = object.getInt("mode");
            this.mDevProp.temperature_2_5 = object.getInt("temperature");
            this.mDevProp.fridgetemperature_3_1 = object.getInt("fridgetemperature");
            this.mDevProp.targettemperature_3_2 = object.getInt("targettemperature");
            this.mDevProp.on_3_3 = object.getBoolean("on");
            this.mDevProp.temperatureanother_4_1 = object.getInt("temperatureanother");
            this.mDevProp.targettemperatureanother_4_2 = object.getInt("targettemperatureanother");
            this.mDevProp.onanother_4_3 = object.getBoolean("onanother");
            this.mDevProp.quickcooling_5_1 = object.getBoolean("quickcooling");
            this.mDevProp.quickfreeze5_2 = object.getBoolean("quickfreeze");
            this.mDevProp.frosttemp_6_1 = object.getInt("frosttemp");
            this.mDevProp.forcedfrost_6_2 = object.getBoolean("forcedfrost");
            this.mDevProp.forcenonstop_6_3 = object.getBoolean("forcenonstop");
            this.mDevProp.heatingwire_6_4 = object.getBoolean("heatingwire");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getSharedPreferences() {
        return ViomiApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
