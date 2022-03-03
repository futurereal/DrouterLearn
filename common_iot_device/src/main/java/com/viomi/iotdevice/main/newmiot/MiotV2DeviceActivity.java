package com.viomi.iotdevice.main.newmiot;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.viomi.iotdevice.IotSerialConfig;
import com.viomi.iotdevice.R;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.callback.IotSerialCallback;
import com.viomi.iotdevice.common.callback.ProgressCallback;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotProperty;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;
import com.viomi.iotdevice.main.newmiot.repository.DevRepository;

import java.util.Iterator;
import java.util.Map;


/**
 * @author lzw_9
 */
public class MiotV2DeviceActivity extends AppCompatActivity {

    private final static String TAG = "MiotV2DeviceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miotv2_device);
        init();
    }


    private void init() {
        ViomiIotManager.Companion.getInstance().setHandler(new IotSerialCallback() {
            @Override
            public void props(Map prop, String serialData) {
                //屬性上报
                Iterator<Map.Entry<String, Object>> iterator = prop.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    Log.d(TAG, "prop，name=" + entry.getKey() + ",value=" + entry.getValue());
                    Miotv2Manager.getInstance().propertyReport(entry.getKey(), entry.getValue());//上报到iot云端

                    switch (entry.getKey()) {
                        case "1.1":
                            DevRepository.getInstance().getDevProp().manufacturer_1_1 = (String) entry.getValue();
                            break;
                        case "1.2":
                            DevRepository.getInstance().getDevProp().model_1_2 = (String) entry.getValue();
                            break;
                        case "1.3":
                            DevRepository.getInstance().getDevProp().serialnumber_1_3 = (String) entry.getValue();
                            break;
                        case "1.4":
                            DevRepository.getInstance().getDevProp().firmwarerevision_1_4 = (String) entry.getValue();
                            break;
                        case "2.1":
                            DevRepository.getInstance().getDevProp().fault_2_1 = (int) entry.getValue();
                            break;
                        case "2.4":
                            DevRepository.getInstance().getDevProp().mode_2_4 = (int) entry.getValue();
                            break;
                        case "2.5":
                            DevRepository.getInstance().getDevProp().temperature_2_5 = (int) entry.getValue();
                            break;
                        case "3.1":
                            DevRepository.getInstance().getDevProp().fridgetemperature_3_1 = (int) entry.getValue();
                            break;
                        case "3.2":
                            DevRepository.getInstance().getDevProp().targettemperature_3_2 = (int) entry.getValue();
                            break;
                        case "3.3":
                            DevRepository.getInstance().getDevProp().on_3_3 = ((int) entry.getValue() == 1);
                            break;
                        case "4.1":
                            DevRepository.getInstance().getDevProp().temperatureanother_4_1 = (int) entry.getValue();
                            break;
                        case "4.2":
                            DevRepository.getInstance().getDevProp().targettemperatureanother_4_2 = (int) entry.getValue();
                            break;
                        case "4.3":
                            DevRepository.getInstance().getDevProp().onanother_4_3 = ((int) entry.getValue() == 1);
                            break;
                        case "5.1":
                            DevRepository.getInstance().getDevProp().quickcooling_5_1 = ((int) entry.getValue() == 1);
                            break;
                        case "5.2":
                            DevRepository.getInstance().getDevProp().quickfreeze5_2 = ((int) entry.getValue() == 1);
                            break;
                        case "6.1":
                            DevRepository.getInstance().getDevProp().frosttemp_6_1 = (int) entry.getValue();
                            break;
                        case "6.2":
                            DevRepository.getInstance().getDevProp().forcedfrost_6_2 = ((int) entry.getValue() == 1);
                            break;
                        case "6.3":
                            DevRepository.getInstance().getDevProp().forcenonstop_6_3 = ((int) entry.getValue() == 1);
                            break;
                        case "6.4":
                            DevRepository.getInstance().getDevProp().heatingwire_6_4 = ((int) entry.getValue() == 1);
                            break;

                    }
                    DevRepository.getInstance().saveJsonString();
                }

            }

            @Override
            public void event(EventPack pack, String serialData) {
                //事件上报
                Log.d(TAG, "event，name=" + pack.getName());
                Iterator<Object> iterator = pack.getPropList().iterator();
                while (iterator.hasNext()) {
                    Log.d(TAG, "event params，prop=" + iterator.next());
                    Miotv2Manager.getInstance().eventReport(pack);//事件上报到iot云端
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
                Miotv2Manager.getInstance().setModel(model);//开机上报设备model，设置到iot sdk
            }

            @Override
            public void mcu_version(String version) {
                Log.d(TAG, "mcu_version=" + version);
            }

            @Override
            public void enterTestMode() {

            }
        });
    }


    /**
     * 初始化串口
     */
    public void open(View view) {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        } catch (Exception e) {
            Log.e("getLocalMacAddress", "fail,msg=" + e.getMessage());
        }
        IotSerialConfig config = new IotSerialConfig.Builder()
                .setIotType(IotSerialConfig.IotType.PROTOCOL_VIOT)
                //.setWifiSerialBaudRate(115200);//ESP32
                //.setWifiSerialPath("/dev/ttyS4");//ESP32
                .setMcuSerialBaudRate(115200)
                //.setMcuSerialPath("/dev/ttyMT2");//绿联
                .setMcuSerialPath("/dev/ttyS0")
                .setDebugEnv(true)
                .setMac(mac)
                //.setDid("152586161");
                .build();
        ViomiIotManager.Companion.getInstance().enableLog(true);
        ViomiIotManager.Companion.getInstance().init(this, config);
    }

    public ViotGetPropRequestBody getPropBody() {
        ViotGetPropRequestBody body = new ViotGetPropRequestBody();
        //冰箱
        ViotProperty mode2 = new ViotProperty();
        mode2.setSid(Miotv2Manager.getInstance().getDevice().fridge().mode().getServiceInstanceID());
        mode2.setPid(Miotv2Manager.getInstance().getDevice().fridge().mode().getInstanceID());
        body.getPropList().add(mode2);
        ViotProperty temperature2 = new ViotProperty();
        temperature2.setSid(Miotv2Manager.getInstance().getDevice().fridge().temperature().getServiceInstanceID());
        temperature2.setPid(Miotv2Manager.getInstance().getDevice().fridge().temperature().getInstanceID());
        body.getPropList().add(temperature2);
        ViotProperty fault2 = new ViotProperty();
        fault2.setSid(Miotv2Manager.getInstance().getDevice().fridge().fault().getServiceInstanceID());
        fault2.setPid(Miotv2Manager.getInstance().getDevice().fridge().fault().getInstanceID());
        body.getPropList().add(fault2);
        //冷藏室
        ViotProperty temperature3 = new ViotProperty();
        temperature3.setSid(Miotv2Manager.getInstance().getDevice().fridgechamber().temperature().getServiceInstanceID());
        temperature3.setPid(Miotv2Manager.getInstance().getDevice().fridgechamber().temperature().getInstanceID());
        body.getPropList().add(temperature3);
        ViotProperty targettemperature3 = new ViotProperty();
        targettemperature3.setSid(Miotv2Manager.getInstance().getDevice().fridgechamber().targettemperature().getServiceInstanceID());
        targettemperature3.setPid(Miotv2Manager.getInstance().getDevice().fridgechamber().targettemperature().getInstanceID());
        body.getPropList().add(targettemperature3);
        ViotProperty on3 = new ViotProperty();
        on3.setSid(Miotv2Manager.getInstance().getDevice().fridgechamber().on().getServiceInstanceID());
        on3.setPid(Miotv2Manager.getInstance().getDevice().fridgechamber().on().getInstanceID());
        body.getPropList().add(on3);
        //冷冻室
        ViotProperty temperature5 = new ViotProperty();
        temperature5.setSid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().temperature().getServiceInstanceID());
        temperature5.setPid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().temperature().getInstanceID());
        body.getPropList().add(temperature5);
        ViotProperty targettemperature5 = new ViotProperty();
        targettemperature5.setSid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().targettemperature().getServiceInstanceID());
        targettemperature5.setPid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().targettemperature().getInstanceID());
        body.getPropList().add(targettemperature5);
        ViotProperty on4 = new ViotProperty();
        on4.setSid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().on().getServiceInstanceID());
        on4.setPid(Miotv2Manager.getInstance().getDevice().fridgeanotherchamber().on().getInstanceID());
        body.getPropList().add(on4);
        //冰箱面板
        ViotProperty quickcooling = new ViotProperty();
        quickcooling.setSid(Miotv2Manager.getInstance().getDevice().fridgepanel().quickcooling().getServiceInstanceID());
        quickcooling.setPid(Miotv2Manager.getInstance().getDevice().fridgepanel().quickcooling().getInstanceID());
        body.getPropList().add(quickcooling);
        ViotProperty quickfreeze = new ViotProperty();
        quickfreeze.setSid(Miotv2Manager.getInstance().getDevice().fridgepanel().quickfreeze().getServiceInstanceID());
        quickfreeze.setPid(Miotv2Manager.getInstance().getDevice().fridgepanel().quickfreeze().getInstanceID());
        body.getPropList().add(quickfreeze);
        return body;
    }

    /**
     * 获取设备属性
     */
    public void getprop(View view) {
        ViomiIotManager.Companion.getInstance().getProperties(getPropBody(), (resultCode, result, desc) -> {

        });
    }

    public ViotSetPropRequestBody setModeBody() {
        ViotSetPropRequestBody body = new ViotSetPropRequestBody();
        ViotProperty propMode = new ViotProperty();
        propMode.setSid(Miotv2Manager.getInstance().getDevice().fridge().mode().getServiceInstanceID());
        propMode.setPid(Miotv2Manager.getInstance().getDevice().fridge().mode().getInstanceID());
        propMode.setValue(1);//0 none 1智能模式 2假日模式
        body.getPropList().add(propMode);
        return body;
    }

    /***
     * 设置设备属性
     */
    public void setprop(View view) {
        ViomiIotManager.Companion.getInstance().setProperties(setModeBody(), (resultCode, result, desc) -> {

        });
    }

    /**
     * 设置方法
     */
    public void action(View view) {
        ViotActionRequestBody requestBody = new ViotActionRequestBody();
        requestBody.setSid(1);
        requestBody.setAid(3);
        ViotProperty prop1 = new ViotProperty();
        prop1.setPid(2);
        prop1.setValue(10);

        ViotProperty prop2 = new ViotProperty();
        prop2.setPid(2);
        prop2.setValue("none");
        requestBody.getPropList().add(prop1);
        requestBody.getPropList().add(prop2);
        ViomiIotManager.Companion.getInstance().action(requestBody, new CommonCallback() {
            @Override
            public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                if (resultCode == 0) {

                } else {

                }
            }
        });
    }


    /***
     * 升级mcu固件
     * @param view
     */
    public void mcuOta(View view) {
        try {
            ViomiIotManager.Companion.getInstance().otaStart("/sdcard/mcu/ota.bin", new ProgressCallback() {
                @Override
                public void onResult(boolean isProcessing, int progress, String desc) {
                    Log.i(TAG, "mcuOta，isProcessing  =" + isProcessing + ",progress=" + progress);
                    if (!isProcessing && progress == 100) {
                        Log.i(TAG, "mcuOta success!");
                        Toast.makeText(MiotV2DeviceActivity.this, "mcu升级完成", Toast.LENGTH_SHORT).show();
                    } else if (progress <= 0) {
                        Log.i(TAG, "mcuOta fail,msg=" + desc);
                        Toast.makeText(MiotV2DeviceActivity.this, "mcu升级失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "ota fail，msg =" + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "ota 异常", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * 绑定小米账号
     * @param view
     */
    public void bindDevice(View view) {
        Miotv2Manager.getInstance().resetDevice(getApplicationContext(), "882225307");
        Toast.makeText(this, "设备绑定到2231902661", Toast.LENGTH_SHORT).show();
    }


    /***
     * 解绑小米账号
     * @param view
     */
    public void unbindDevice(View view) {
        Miotv2Manager.getInstance().resetDevice(getApplicationContext(), null);
        Toast.makeText(this, "设备解绑", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViomiIotManager.Companion.getInstance().close();
        Miotv2Manager.getInstance().close();
    }
}
