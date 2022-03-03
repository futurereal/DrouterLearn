package com.viomi.modulesetting.entity;

import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.entity
 * @ClassName: QRCodeBase
 * @Description: 登录二维码 Json
 * @Author: randysu
 * @CreateDate: 2020/3/26 1:41 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 1:41 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class QRCodeBase implements Serializable {
    private static final String TAG = "QRCodeBase";
    @JSONField(name = "mobBaseRes")
    private QRCodeLogin loginQRCode;

    public QRCodeLogin getLoginQRCode() {
        return loginQRCode;
    }

    public void setLoginQRCode(QRCodeLogin loginQRCode) {
        this.loginQRCode = loginQRCode;
    }

    public String getViotUserId() {
        String viotUserId = loginQRCode.getResult().getLoginResult().getUserId();
        Log.i(TAG, "getViotUserId: " + viotUserId);
        return viotUserId;
    }

    public void setViotUserId(String viotUserId) {
        loginQRCode.getResult().getLoginResult().setUserId(viotUserId);
    }


    @Override
    public String toString() {
        return loginQRCode == null ? "null" : "code: " + loginQRCode.getCode() + "\n" +
                "desc: " + loginQRCode.getDesc() + "\n" +
                "result: " + loginQRCode.getResult();
    }
}
