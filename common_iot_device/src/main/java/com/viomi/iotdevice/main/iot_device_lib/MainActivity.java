package com.viomi.iotdevice.main.iot_device_lib;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.viomi.iotdevice.R;
import com.viomi.iotdevice.main.iot_device_lib.miotdeivce.MiotDeviceActivity;
import com.viomi.iotdevice.main.iot_device_lib.viotmeshdevice.ViotMeshActivity;
import com.viomi.iotdevice.main.newmiot.MiotV2DeviceActivity;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int READ_WRITE_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    public void toMiotDevice(View v) {
        startActivity(new Intent(this, MiotDeviceActivity.class));
    }


    public void toViotDevice(View v) {
        //startActivity(new Intent(this, ViotDeviceActivity.class));
        startActivity(new Intent(this, MiotV2DeviceActivity.class));
    }

    public void toViotMeshDevice(View v) {
        startActivity(new Intent(this, ViotMeshActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(READ_WRITE_STORAGE)
    public void checkPermission(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)){
            Log.w(TAG,"已获得权限");
        }else{
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要获取读写本地数据的权限",
                    READ_WRITE_STORAGE, perms);
        }
    }
}
