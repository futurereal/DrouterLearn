package com.viomi.ovensocommon.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.viomi.common.ViomiProvideUtil;

import java.io.Serializable;

/**
 * @description: 用户信息的数据库类， deviceId 作为主键
 * @data:2021/9/22
 */
@Entity(tableName = "tb_userinfo")
public class UserInfoDb implements Serializable {
    //    {"mobBaseRes":{"code":100,"desc":"处理成功",
//    "result":{"token":"t0GP5QXWRb2N9xSN",
//    "loginData":{"accountType":1,"loginType":4,"userId":3480244,
//    "roles":["VMALL_CUSTOMER"],"openId":null,"userCode":"lZxMLsVlFwexfAR1C7A","headImg":"https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/images/user_head/1/bLU7aZajgX0MIa2rwL7.jpg?GalaxyAccessKeyId=EAKC4WAFZQV4K&Expires=361620353169321&Signature=hlLBbOyhjCiu10uBNRBeSaDRRRo=","mobile":"18565653036","nickName":"心的里程","channelInfoBean":null},"appendAttr":"{\"account\":\"12067566\",\"cid\":-1,\"gender\":1,\"headImg\":\"https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/images/user_head/1/bLU7aZajgX0MIa2rwL7.jpg?GalaxyAccessKeyId=EAKC4WAFZQV4K&Expires=361620353169321&Signature=hlLBbOyhjCiu10uBNRBeSaDRRRo=\",\"mobile\":\"18565653036\",\"nickName\":\"心的里程\",\"userId\":3480244,\"xiaomi\":{\"accessToken\":\"V3_0dZfIpAlyU53gzvhJxnJkKS3MwNYR1ZLT4IeJ2LrjeeKJi6EaJgPUJpITRzO9t-xy3W0yyFpvbUvFbBHCKATm7zy_kjqm27tpDiIpaTzH9gezBEEogVS7NHabJQGWExb\",\"mExpiresIn\":1635047302000,\"macAlgorithm\":\"HmacSHA1\",\"macKey\":\"b5FGZbKwUXJe2bdIMvuVeaUnSoA\",\"miId\":\"60507270\",\"type\":\"android\",\"userId\":\"60507270\"}}","now":1628078390254,"expireTime":1635854390254}}}
    // 绑定设备用
    //QRCodeResult

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "device_id")
    private String deviceId = ViomiProvideUtil.getDeviceId();


    @ColumnInfo(name = "token")
    private String token;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId = "-1";
    // 更新界面
    // QRCodeLogin -->QRCodeResult -->UserInfo
    @ColumnInfo(name = "nike_name")
    private String nickname;
    @ColumnInfo(name = "head_imgurl")
    private String headImgUrl;
    // 云米的用户ID
    @ColumnInfo(name = "account_id")
    private String account;
    @ColumnInfo(name = "mobile")
    private String mobile;
    // UserInfo --->MiUserInfo
    @ColumnInfo(name = "mi_id")
    private String miId;
    @ColumnInfo(name = "mi_userid")
    private String miUserId;
    @ColumnInfo(name = "mi_bind_status")
    private boolean isBindMiot;
    @Ignore
    private boolean isFromQrcode;

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@NonNull String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMiUserId() {
        return miUserId;
    }

    public void setMiUserId(String miUserId) {
        this.miUserId = miUserId;
    }

    public String getMiId() {
        return miId;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

    public boolean isFromQrcode() {
        return isFromQrcode;
    }

    public void setFromQrcode(boolean fromQrcode) {
        isFromQrcode = fromQrcode;
    }

    public boolean isBindMiot() {
        return isBindMiot;
    }

    public void setBindMiot(boolean bindMiot) {
        isBindMiot = bindMiot;
    }

    @Override
    public String toString() {
        return "UserInfoDb{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", account='" + account + '\'' +
                ", mobile='" + mobile + '\'' +
                ", miId='" + miId + '\'' +
                ", miUserId='" + miUserId + '\'' +
                ", isBindMiot='" + isBindMiot + '\'' +
                '}';
    }
}
