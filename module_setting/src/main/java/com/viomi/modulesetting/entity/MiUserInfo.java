package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 小米账号信息
 * Created by William on 2018/1/29.
 */
public class MiUserInfo implements Serializable {
    @JSONField(name = "miId")
    private String miId;// 小米账号
    @JSONField(name = "accessToken")
    private String accessToken;// 小米账号 Token
    @JSONField(name = "userId")
    private String userId;// 小米 id
    @JSONField(name = "type")
    private String type;// 系统类型（android 或 IOS）
    @JSONField(name = "mExpiresIn")
    private long mExpiresIn;
    @JSONField(name = "macKey")
    private String macKey;
    @JSONField(name = "macAlgorithm")
    private String macAlgorithm;

    public String getMiId() {
        return miId;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getmExpiresIn() {
        return mExpiresIn;
    }

    public void setmExpiresIn(long mExpiresIn) {
        this.mExpiresIn = mExpiresIn;
    }

    public String getMacKey() {
        return macKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }
}