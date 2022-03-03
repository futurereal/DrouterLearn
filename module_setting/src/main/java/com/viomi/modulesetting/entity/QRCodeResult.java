package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class QRCodeResult implements Serializable {
    @JSONField(name = "token")
    private String token;// 云米账号 Token
    @JSONField(name = "LoginData")
    private QRCodeLoginResult loginResult;
    @JSONField(name = "appendAttr")
    private String appendAttr;// 包含云米账号和小米账号信息
    private UserInfo userInfo;// 包含云米账号和小米账号信息

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppendAttr() {
        return appendAttr;
    }

    public void setAppendAttr(String appendAttr) {
        this.appendAttr = appendAttr;
    }

    public QRCodeLoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(QRCodeLoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void parseAppendAttr() {
        userInfo = JSON.parseObject(appendAttr, UserInfo.class);
    }

    @Override
    public String toString() {
        return "QRCodeResult{" +
                "token='" + token + '\'' +
                ", loginResult=" + loginResult +
                ", appendAttr='" + appendAttr + '\'' +
                '}';
    }
}
