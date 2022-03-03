package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Firmdetail implements Serializable {

    /**
     * totalSize : 69059649
     * descript : 3032 测试版 验证洗衣机
     */
    @JSONField(name = "totalSize")
    private int totalSize;
    @JSONField(name = "version")
    private String version;
    @JSONField(name = "descript")
    private String descript;

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
