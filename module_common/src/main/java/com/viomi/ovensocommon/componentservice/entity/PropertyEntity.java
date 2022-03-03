package com.viomi.ovensocommon.componentservice.entity;

import com.viomi.common.ViomiProvideUtil;

/**
 * PropertyEntity 里面 Object 属性 不能明确类型序列化有问题
 */
public class PropertyEntity {
    private int sid;
    private int pid;
    private String deviceId;
    private Object content;
    private boolean isNotDelay;

    public PropertyEntity() {
    }

    public PropertyEntity(int sid, int pid) {
        this(sid, pid, null);
    }

    public PropertyEntity(int sid, int pid, Object content) {
        this.sid = sid;
        this.pid = pid;
        this.deviceId = ViomiProvideUtil.getDeviceId();
        this.content = content;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public boolean isNotDelay() {
        return isNotDelay;
    }

    public void setNotDelay(boolean notDelay) {
        isNotDelay = notDelay;
    }

    @Override
    public String toString() {
        return "PropertyEntity{" +
                "sid=" + sid +
                ", pid=" + pid +
                ", deviceId='" + deviceId + '\'' +
                ", content=" + content +
                ", isNotDelay=" + isNotDelay +
                '}';
    }
}
