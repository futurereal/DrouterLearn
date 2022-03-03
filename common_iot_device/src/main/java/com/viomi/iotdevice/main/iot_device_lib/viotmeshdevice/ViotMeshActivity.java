package com.viomi.iotdevice.main.iot_device_lib.viotmeshdevice;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.viomi.iotdevice.IotSerialConfig;
import com.viomi.iotdevice.R;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.SerialParams;
import com.viomi.iotdevice.iotmesh.callback.IotMeshCallback;
import com.viomi.iotdevice.iotmesh.entity.DeviceToWiFiMeshEntity;
import com.viomi.iotdevice.iotmesh.entity.WiFiToDeviceMeshEntity;
import com.viomi.iotdevice.main.iot_device_lib.util.ToolUtil;

public class ViotMeshActivity extends AppCompatActivity implements View.OnClickListener, IotMeshCallback {
    private TextView mWiFiInfoTextView, mWiFiResultTextView, mWiFiResult2TextView, mWiFiSSIDTextView, mWiFiMacTextView;
    private EditText mEditText;
    private SimpleDraweeView mSimpleDraweeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viot_mesh);

        mWiFiInfoTextView = findViewById(R.id.wifi_info);
        mWiFiResultTextView = findViewById(R.id.wifi_result);
        mWiFiResult2TextView = findViewById(R.id.wifi_result_2);
        mWiFiSSIDTextView = findViewById(R.id.wifi_ssid);
        mWiFiMacTextView = findViewById(R.id.wifi_mac);
        mEditText = findViewById(R.id.wifi_input);
        mSimpleDraweeView = findViewById(R.id.wifi_qr_code);

        findViewById(R.id.wifi_reset).setOnClickListener(this);
        findViewById(R.id.wifi_create_qr).setOnClickListener(this);

        // 初始化 Viot SDK.
        IotSerialConfig.Builder builder = new IotSerialConfig.Builder();
        builder.setIotType(IotSerialConfig.IotType.PROTOCOL_VIOT);
        builder.setWifiSerialBaudRate(115200);
        builder.setWifiSerialPath("/dev/ttyS4");
        builder.setDebugEnv(true);
        IotSerialConfig config = new IotSerialConfig(builder);
        ViomiIotManager.Companion.getInstance().enableLog(true);
        ViomiIotManager.Companion.getInstance().init(this, config);

        ViomiIotManager.Companion.getInstance().setMeshCallback(this);
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBaseReceiver);
        ViomiIotManager.Companion.getInstance().close();
        ViomiIotManager.Companion.getInstance().removeMesh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_reset:
                ViomiIotManager.Companion.getInstance().resetMesh();
                mWiFiInfoTextView.setText("WiFi 信息：");
                mWiFiResultTextView.setText("配网结果：");
                mWiFiResult2TextView.setText("配网结果：");
                break;
            case R.id.wifi_create_qr:
                if (!ToolUtil.isNetworkConnect()) {
                    Toast.makeText(this, "请先连接WiFi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mEditText.getText().toString().length() < 8) {
                    Toast.makeText(this, "请输入正确的WiFi密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                SerialParams.Companion.getInstance().saveWiFiPassword(mEditText.getText().toString());
                ViomiIotManager.Companion.getInstance().getDeviceToWiFiMeshQR();
                break;
        }
    }

    @Override
    public void onWiFiInfoCallback(String ssid, String password) {
        mWiFiInfoTextView.setText("WiFi 信息，SSID: " + ssid + "password: " + password);
    }

    @Override
    public void onMeshQRRefresh(boolean isSuccess, String url) {
        if (isSuccess) {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(url))
                    .setResizeOptions(new ResizeOptions(150, 150))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(mSimpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            mSimpleDraweeView.setController(controller);
        }
    }

    @Override
    public void onDeviceToWiFiMeshResult(boolean isSuccess, DeviceToWiFiMeshEntity deviceToWiFiMeshEntity) {
        if (isSuccess) {
            String result = JSON.toJSONString(deviceToWiFiMeshEntity);
            mWiFiResultTextView.setText(result);
            mWiFiResult2TextView.setText(result);
        }
    }

    @Override
    public void onWiFiToDeviceMeshResult(boolean isSuccess, WiFiToDeviceMeshEntity entity) {

    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBaseReceiver, intentFilter);
    }

    private void checkWiFiStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        mWiFiSSIDTextView.setText("WiFi SSID：" + wifiInfo.getSSID());
        mWiFiMacTextView.setText("WiFi mac: " + wifiInfo.getBSSID());
        SerialParams.Companion.getInstance().saveWiFiSSID(wifiInfo.getSSID());
        SerialParams.Companion.getInstance().saveWiFiMac(wifiInfo.getBSSID());
    }

    private BroadcastReceiver mBaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case WifiManager.WIFI_STATE_CHANGED_ACTION: // WiFi 状态改变
                    case ConnectivityManager.CONNECTIVITY_ACTION: // 网络状态改变
                    case WifiManager.RSSI_CHANGED_ACTION: // WiFi 信号强弱监听
                        checkWiFiStatus();
                        break;
                }
            }
        }
    };
}
