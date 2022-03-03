package com.viomi.modulesetting.utils.softap;

/**
 * @author bailing
 * date 2020/9/11
 * description：
 */
public class SoftApData {

    /**
     * 名称
     */
    private String wifiSsid;
    /**
     * 密码
     */
    private String wifiPwd;
    /**
     * mac
     */
    private String wifiMac;
    /**
     * ip
     */
    private String wifiIp;
    /**
     * 开关 1：开，0：关
     */
    private int wifiOnOff;

    public String getWifiSsid() {
        return wifiSsid;
    }

    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getWifiIp() {
        return wifiIp;
    }

    public void setWifiIp(String wifiIp) {
        this.wifiIp = wifiIp;
    }

    public int getWifiOnOff() {
        return wifiOnOff;
    }

    public void setWifiOnOff(int wifiOnOff) {
        this.wifiOnOff = wifiOnOff;
    }
}
