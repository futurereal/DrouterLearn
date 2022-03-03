package com.viomi.modulesetting.entity;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.entity
 * @ClassName: SettingMiIdentification
 * @Description: 小米标识信息
 * @Author: randysu
 * @CreateDate: 2020/3/17 1:37 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/17 1:37 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SettingMiIdentification {

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
