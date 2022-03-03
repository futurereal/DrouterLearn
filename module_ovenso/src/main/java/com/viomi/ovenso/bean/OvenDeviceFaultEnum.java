package com.viomi.ovenso.bean;


import com.viomi.ovenso.microwave.R;

/**
 * Created by Ljh on 2020/9/21.
 * Description:错误码
 */
public enum OvenDeviceFaultEnum {
    FAULT_PAN_HIGH(0x01 << 7, R.string.ovenso_devicefalut_pan_high, R.string.ovenso_devicefalut_pan_content),
    FAULT_PAN_LOW(0x01 << 8, R.string.ovenso_devicefalut_pan_low, R.string.ovenso_devicefalut_pan_content),
    FAULT_CAVITY_HIGH(0x01 << 9, R.string.ovenso_devicefalut_cavity_high, R.string.ovenso_devicefalut_cavity_content),
    FAULT_CAVITY_LOW(0x01 << 10, R.string.ovenso_devicefalut_cavity_low, R.string.ovenso_devicefalut_cavity_content),
    FAULT_NTF_CAVITY(0x01 << 11, R.string.ovenso_devicefalut_cavity_ntf, R.string.ovenso_devicefalut_cavity_ntf_content),
    FAULT_NTF_PAN(0x01 << 12, R.string.ovenso_devicefalut_pan_ntf, R.string.ovenso_devicefalut_pan_ntf_content),
    FAULT_DISCONNECT(0x01 << 13, R.string.disconnect_title, R.string.disconnect_title_content),
    FAULT_CAMERA(0x01 << 15, R.string.ovenso_devicefalut_camera, R.string.ovenso_devicefalut_camera_content),
    FAULT_FAN(0x01 << 16, R.string.ovenso_devicefalut_fan, R.string.ovenso_devicefalut_fan_content),
    FAULT_MOTOR(0x01 << 17, R.string.ovenso_devicefalut_motor, R.string.ovenso_devicefalut_motor_content);
    public int value;
    public int titleId;
    public int msgId;

    OvenDeviceFaultEnum(int value, int titleId, int msgId) {
        this.value = value;
        this.titleId = titleId;
        this.msgId = msgId;
    }
}

