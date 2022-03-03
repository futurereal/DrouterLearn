package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class QRCodeLoginCodeBase implements Serializable {
    @JSONField(name = "mobBaseRes")
    private QRCodeLoginCode loginQRCode;

    public QRCodeLoginCode getLoginQRCode() {
        return loginQRCode;
    }

    public void setLoginQRCode(QRCodeLoginCode loginQRCode) {
        this.loginQRCode = loginQRCode;
    }

    @Override
    public String toString() {
        return "code: " + loginQRCode.getCode() + "\n" +
                "desc: " + loginQRCode.getDesc() + "\n" +
                "result: " + loginQRCode.getResult();
    }
}
