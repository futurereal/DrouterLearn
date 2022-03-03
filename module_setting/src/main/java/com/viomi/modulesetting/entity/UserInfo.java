package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 用户相关信息
 * Created by William on 2018/1/29.
 */
public class UserInfo implements Serializable {
    @JSONField(name = "nickName")
    private String nickname;// 云米账号用户昵称
    @JSONField(name = "headImg")
    private String headImg;// 云米账号头像 Url
    @JSONField(name = "account")
    private String account;// 云米账号
    @JSONField(name = "mobile")
    private String mobile;// 手机号码
    @JSONField(name = "cid")
    private int cid;// 渠道 id
    @JSONField(name = "gender")
    private int gender = -1;// 性别: 0.女，1.男，-1 未知
    @JSONField(name = "xiaomi")
    private MiUserInfo miUserInfo;// 小米账号信息
    private String token;
    private String userCode;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public MiUserInfo getMiUserInfo() {
        return miUserInfo;
    }

    public void setMiUserInfo(MiUserInfo miUserInfo) {
        this.miUserInfo = miUserInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nickname='" + nickname + '\'' +
                ", headImg='" + headImg + '\'' +
                ", account='" + account + '\'' +
                ", mobile='" + mobile + '\'' +
                ", cid=" + cid +
                ", gender=" + gender +
                ", miUserInfo=" + miUserInfo +
                ", token='" + token + '\'' +
                ", userCode='" + userCode + '\'' +
                '}';
    }

}
