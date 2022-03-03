package com.viomi.modulesetting.entity;

/**
 * 小米标识信息
 * Created by William on 2018/1/19.
 */
public class MiIdentification {
    private String mac;// mac 地址
    private String deviceId;// 设备 Id
    private String miToken;// 小米账号 Token
    private String miInfo;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMiToken() {
        return miToken;
    }

    public void setMiToken(String miToken) {
        this.miToken = miToken;
    }

    public String getMiInfo() {
        return miInfo;
    }

    public void setMiInfo(String miInfo) {
        this.miInfo = miInfo;
    }
}
