package com.viomi.modulesetting.utils.softap;

/**
 * @author hailang
 * @date 2020/9/8 000810:45
 */
public class HotSpotDevice {
    public String devName;
    public String devMac;
    public String devIp;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevMac() {
        return devMac;
    }

    public void setDevMac(String dev_mac) {
        this.devMac = dev_mac;
    }

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }

    @Override
    public String toString() {
        return "HotSpotDevice{" +
                "dev_name='" + devName + '\'' +
                ", dev_mac='" + devMac + '\'' +
                ", dev_ip='" + devIp + '\'' +
                '}';
    }
}
