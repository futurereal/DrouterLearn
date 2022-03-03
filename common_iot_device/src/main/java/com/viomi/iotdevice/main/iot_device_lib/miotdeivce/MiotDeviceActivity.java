package com.viomi.iotdevice.main.iot_device_lib.miotdeivce;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.viomi.iotdevice.R;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.callback.IotSerialCallback;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.main.iot_device_lib.miotdeivce.device.DeviceRepository;
import com.viomi.iotdevice.main.iot_device_lib.miotdeivce.device.MiotManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * @author lzw_9
 */
public class MiotDeviceActivity extends AppCompatActivity {

    private final static String TAG = "MiotDeviceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miotdevice);
        init();
    }


    private void init() {
        ViomiIotManager.Companion.getInstance().setHandler(new IotSerialCallback() {
            @Override
            public void props(Map prop, @NonNull String serialData) {
                //屬性上报
                for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) prop.entrySet()) {
                    Log.d(TAG, "prop，name=" + entry.getKey() + ",value=" + entry.getValue());
                    MiotManager.getInstance().propertyReport(entry.getKey(), entry.getValue());//上报到iot云端

                    switch (entry.getKey()) {//缓存属性
                        case "power":
                            DeviceRepository.getInstance().getDeviceProp().power = (int) entry.getValue();
                            break;
                        case "mode":
                            DeviceRepository.getInstance().getDeviceProp().mode = (int) entry.getValue();
                            break;
                        case "wind_level":
                            DeviceRepository.getInstance().getDeviceProp().wind_level = (int) entry.getValue();
                            break;
                        default:
                            break;
                    }
                }

            }

            @Override
            public void event(EventPack pack, @NonNull String serialData) {
                //事件上报
                Log.d(TAG, "event，name=" + pack.getName());
                for (Object o : pack.getPropList()) {
                    Log.d(TAG, "event params，prop=" + o);
                    MiotManager.getInstance().eventReport(pack);//事件上报到iot云端
                }
            }


            @Override
            public void json_send(String json) {
                //json格式數據上报，仅适用于miot
                Log.d(TAG, "json send=" + json);

            }

            @Override
            public void model(String model) {
                Log.d(TAG, "model=" + model);
                MiotManager.getInstance().setModel(model);//开机上报设备model，设置到iot sdk
            }

            @Override
            public void mcu_version(String version) {
                Log.d(TAG, "mcu_version=" + version);
            }

            @Override
            public void enterTestMode() {

            }
        });
        ViomiIotManager.Companion.getInstance().enableLog(true);


    }


    /**
     * 初始化串口
     */
    public void open(View view) {
        try {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
        } catch (Exception e) {
            Log.e("getLocalMacAddress", "fail,msg=" + e.getMessage());
        }
        Toast.makeText(this, "初始化串口", Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取设备属性
     */
    public void getprop(View view) {
        String[] props = new String[]{"mode", "settemp"};
        ViomiIotManager.Companion.getInstance().action("get_prop", props, (resultCode, result, desc) -> {
            Log.e(TAG, "getprop result,code=" + resultCode + ",data=" + result + ",desc=" + desc);
            if (resultCode != 0) {
                Log.i(TAG, "getprop fail,msg=" + desc);
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    String mode = jsonArray.getString(0);
                    int rcsettemp = jsonArray.getInt(1);
                    Log.i(TAG, "getprop,success,mode=" + mode + ",settemp=" + rcsettemp);
                    //         Toast.makeText(MainActivity.this, "获取属性成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "getprop fail,msg=" + jsonObject.getString("message"));
                    //                  Toast.makeText(MainActivity.this, "获取属性失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /***
     * 设置方法
     */
    public void action(View view) {
        //设置冷藏室温度为4度
        Integer[] props = new Integer[]{25};
        ViomiIotManager.Companion.getInstance().action("set_temp", props, new CommonCallback() {
            @Override
            public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                if (resultCode != 0) {
                    Log.i(TAG, "setRCSetTemp fail,msg=" + desc);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        Log.i(TAG, "setRCSetTemp success ");
                        //   Toast.makeText(MainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "setRCSetTemp fail,msg=" + jsonObject.getString("message"));
                        //     Toast.makeText(MainActivity.this, "设置失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发送 Json 数据
     */
    public void actionJson(View view) {
        String json = "{\"method\":\"set_mode\",\"params\":{\"mode\":5, \"temp\": 25.5, \"wind_level\":[2], \"wind_percent\":[80]}}";
        ViomiIotManager.Companion.getInstance().actionJson(json, (code, value, msg) -> {
            if (code == 0) {
                Log.i(TAG, "actionJson success！json result= " + value);
            } else {
                Log.e(TAG, "actionJson Fail！code= " + code + ",msg=" + msg);
            }
        });
    }


    /**
     * 升级 mcu 固件
     */
    public void mcuOta(View view) {
        try {
            ViomiIotManager.Companion.getInstance().otaStart("/sdcard/mcu/ota.bin", (isProcessing, progress, desc) -> {
                Log.i(TAG, "mcuOta，isProcessing  =" + isProcessing + ",progress=" + progress);
                if (!isProcessing && progress == 100) {
                    Log.i(TAG, "mcuOta success!");
                    Toast.makeText(MiotDeviceActivity.this, "mcu升级完成", Toast.LENGTH_SHORT).show();
                } else if (progress <= 0) {
                    Log.i(TAG, "mcuOta fail,msg=" + desc);
                    Toast.makeText(MiotDeviceActivity.this, "mcu升级失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "ota fail，msg =" + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "ota 异常", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 绑定小米账号
     */
    public void bindDevice(View view) {
        MiotManager.getInstance().resetDevice(getApplicationContext(), "888819681");
        Toast.makeText(this, "设备绑定到888819681", Toast.LENGTH_SHORT).show();
    }


    /**
     * 解绑小米账号
     */
    public void unbindDevice(View view) {
        MiotManager.getInstance().resetDevice(getApplicationContext(), null);
        Toast.makeText(this, "设备解绑", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViomiIotManager.Companion.getInstance().close();
        MiotManager.getInstance().close();
    }
}
