package com.viomi.ovenso.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @description:
 * @data:2021/10/31 temperature 可以是数字，也可能是高中低 火力
 */
public class TemperatureEntity implements Parcelable {
    private int temperature;
    private boolean isSlected;

    public TemperatureEntity(int temperature, boolean isSlected) {
        this.temperature = temperature;
        this.isSlected = isSlected;
    }

    protected TemperatureEntity(Parcel in) {
        temperature = in.readInt();
        isSlected = in.readByte() != 0;
    }

    public static final Creator<TemperatureEntity> CREATOR = new Creator<TemperatureEntity>() {
        @Override
        public TemperatureEntity createFromParcel(Parcel in) {
            return new TemperatureEntity(in);
        }

        @Override
        public TemperatureEntity[] newArray(int size) {
            return new TemperatureEntity[size];
        }
    };

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public boolean isSlected() {
        return isSlected;
    }

    public void setSlected(boolean slected) {
        isSlected = slected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(temperature);
        dest.writeByte((byte) (isSlected ? 1 : 0));
    }
}
