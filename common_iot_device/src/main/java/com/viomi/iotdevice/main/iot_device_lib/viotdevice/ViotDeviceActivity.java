package com.viomi.iotdevice.main.iot_device_lib.viotdevice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.viomi.iotdevice.IotSerialConfig;
import com.viomi.iotdevice.R;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotProperty;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;

public class ViotDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viotdevice);

        findViewById(R.id.get_properties).setOnClickListener(v -> {
            ViotGetPropRequestBody viotGetPropRequestBody = new ViotGetPropRequestBody();
            ViotProperty prop = new ViotProperty();
            prop.setSid(1);
            prop.setPid(1);

            ViotProperty prop1 = new ViotProperty();
            prop1.setSid(1);
            prop1.setPid(2);

            ViotProperty prop2 = new ViotProperty();
            prop2.setSid(2);
            prop2.setPid(2);

            viotGetPropRequestBody.getPropList().add(prop);
            viotGetPropRequestBody.getPropList().add(prop1);
            viotGetPropRequestBody.getPropList().add(prop2);

            ViomiIotManager.Companion.getInstance().getProperties(viotGetPropRequestBody, (resultCode, result, desc) -> {

            });
        });

        findViewById(R.id.set_properties).setOnClickListener(v -> {
            ViotSetPropRequestBody viotSetPropRequestBody = new ViotSetPropRequestBody();
            ViotProperty prop = new ViotProperty();
            prop.setSid(2);
            prop.setPid(2);
            prop.setValue("holiday");
            viotSetPropRequestBody.getPropList().add(prop);

            ViomiIotManager.Companion.getInstance().setProperties(viotSetPropRequestBody, (resultCode, result, desc) -> {

            });
        });

        findViewById(R.id.action).setOnClickListener(v -> {
            ViotActionRequestBody viotActionRequestBody = new ViotActionRequestBody();
            viotActionRequestBody.setSid(3);
            viotActionRequestBody.setAid(5);

            ViomiIotManager.Companion.getInstance().action(viotActionRequestBody, (resultCode, result, desc) -> {

            });
        });

        IotSerialConfig.Builder builder = new IotSerialConfig.Builder();
        builder.setIotType(IotSerialConfig.IotType.PROTOCOL_VIOT);
        builder.setWifiSerialBaudRate(115200);
        builder.setWifiSerialPath("/dev/ttyS4");
        builder.setDebugEnv(true);
//        builder.setMac("7c:49:eb:fb:7f:0e");
//        builder.setDid("152586161");
        IotSerialConfig config = new IotSerialConfig(builder);
        ViomiIotManager.Companion.getInstance().enableLog(true);
        ViomiIotManager.Companion.getInstance().init(this, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViomiIotManager.Companion.getInstance().close();
    }
}
