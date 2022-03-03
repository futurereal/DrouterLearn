package com.viomi.modulesetting.entity;

/**
 * @author bailing
 * date 2021/5/13
 * description：
 */
public class FirmUpdateResult {

    /**
     * fwName : VIO-WB-ESP32S
     * latestFwVer : 1.1.1
     * downloadUrl : htttp://www.123.com
     */

    private String fwName;
    private String latestFwVer;
    private String downloadUrl;
    /**
     * fwId : 633
     * fwType : 1
     * status : 1
     * createdTime : 1620890223000
     * updatedTime : 1620890223000
     */

    private int fwId;
    private int fwType;
    private int status;
    private long createdTime;
    private long updatedTime;

    public String getFwName() {
        return fwName;
    }

    public void setFwName(String fwName) {
        this.fwName = fwName;
    }

    public String getLatestFwVer() {
        return latestFwVer;
    }

    public void setLatestFwVer(String latestFwVer) {
        this.latestFwVer = latestFwVer;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getFwId() {
        return fwId;
    }

    public void setFwId(int fwId) {
        this.fwId = fwId;
    }

    public int getFwType() {
        return fwType;
    }

    public void setFwType(int fwType) {
        this.fwType = fwType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }
}
