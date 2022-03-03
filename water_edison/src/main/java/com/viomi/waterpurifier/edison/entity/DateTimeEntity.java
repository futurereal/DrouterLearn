package com.viomi.waterpurifier.edison.entity;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: WaterPurifier
 * @Package: com.viomi.waterpurifier.entity.mvvm
 * @ClassName: DateTimeEntity
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/4/29 9:25 AM
 * @UpdateUser:
 * @UpdateDate: 2020/4/29 9:25 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class DateTimeEntity {
    private String date;
    private String hour;
    private String minutes;
    private String second;
    private String amPmValue;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getAmPmValue() {
        return amPmValue;
    }

    public void setAmPmValue(String amPmValue) {
        this.amPmValue = amPmValue;
    }

    @NonNull
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
