package com.viomi.iotdevice.main.iot_device_lib.miotdeivce.device;

/***
 * 管理设备属性
 */
public class DeviceRepository {

    private static DeviceRepository mInstance;// 实例
    private DeviceProp mDeviceProp;

    public static DeviceRepository getInstance() {
        if (mInstance == null) {
            synchronized (DeviceRepository.class) {
                if (mInstance == null) {
                    mInstance = new DeviceRepository();
                }
            }
        }
        return mInstance;
    }

    DeviceRepository(){
        mDeviceProp=new DeviceProp();
    }


    public DeviceProp getDeviceProp() {
        return mDeviceProp;
    }


    public void setDeviceProp(DeviceProp deviceProp) {
        if(deviceProp==null){
            return;
        }
        mDeviceProp.mode = deviceProp.mode;
        mDeviceProp.power = deviceProp.power;
        mDeviceProp.wind_level = deviceProp.wind_level;
    }

}
