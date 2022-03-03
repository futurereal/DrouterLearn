package com.viomi.modulesetting.entity;

import java.io.Serializable;

public class MobBaseRes<T> implements Serializable {

    private final long SerializableID = 49585837L;

    private int code;       // 返回码

    private String desc;    // 返回信息

    private T result;       // 返回内容

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "MobBaseRes{" +
                "SerializableID=" + SerializableID +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}