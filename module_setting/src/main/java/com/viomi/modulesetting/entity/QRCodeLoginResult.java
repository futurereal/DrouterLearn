package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.entity
 * @ClassName: QRCodeLoginResult
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/26 1:43 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 1:43 PM
 * @UpdateRemark:
 * @Version: 1.0
 */

public class QRCodeLoginResult implements Serializable {
    @JSONField(name = "accountType")
    private int accountType;
    @JSONField(name = "loginType")
    private int loginType;
    @JSONField(name = "userId")
    private String userId;
    @JSONField(name = "userCode")
    private String userCode;
    @JSONField(name = "roles")
    private List<String> roles;// 账号角色
    @JSONField(name = "headImg")
    private String headImg;// 微信头像链接

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    @Override
    public String toString() {
        return "QRCodeLoginResult{" +
                "accountType=" + accountType +
                ", loginType=" + loginType +
                ", userId=" + userId +
                ", userCode='" + userCode + '\'' +
                ", roles=" + roles +
                ", headImg='" + headImg + '\'' +
                '}';
    }
}
