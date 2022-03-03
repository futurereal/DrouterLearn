package com.viomi.modulesetting.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.entity
 * @ClassName: QRCodeLogin
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/26 1:43 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 1:43 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class QRCodeLogin implements Serializable {

    @JSONField(name = "code")
    private int code;// 返回码
    @JSONField(name = "desc")
    private String desc;// 返回信息
    @JSONField(name = "result")
    private QRCodeResult result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public QRCodeResult getResult() {
        return result;
    }

    public void setResult(QRCodeResult result) {
        this.result = result;
    }

}
