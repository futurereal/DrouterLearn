package com.viomi.ovensocommon.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 烹饪参数的 实体类
 */
public class CookParamEntity implements Parcelable {
    private int modeId;
    private String modeType;
    private String modeName;
    // 以 s 为单位
    private float defineTime;
    private float timeRangeMin;
    private float timeRangeMax;
    // 火力 ，可能是  温度 或者  微波的火力值
    private int defineFirepower;
    private int firepowerRangeMin;
    private int firepowerRangeMax;
    private int defineFirepowerTwo;

    public CookParamEntity(Builder builder) {
        this.modeId = builder.modeId;
        this.modeType = builder.modeType;
        this.modeName = builder.modeName;
        this.defineTime = builder.defineTime;
        this.timeRangeMin = builder.timeRangeMin;
        this.timeRangeMax = builder.timeRangeMax;
        this.defineFirepower = builder.defineFirepower;
        this.firepowerRangeMin = builder.firepowerRangeMin;
        this.firepowerRangeMax = builder.firepowerRangeMax;
        this.defineFirepowerTwo = builder.defineFirepowerTwo;
    }

    public CookParamEntity() {

    }

    protected CookParamEntity(Parcel in) {
        modeId = in.readInt();
        modeType = in.readString();
        modeName = in.readString();
        defineTime = in.readFloat();
        timeRangeMin = in.readFloat();
        timeRangeMax = in.readFloat();
        defineFirepower = in.readInt();
        firepowerRangeMin = in.readInt();
        firepowerRangeMax = in.readInt();
        defineFirepowerTwo = in.readInt();
    }

    public static final Creator<CookParamEntity> CREATOR = new Creator<CookParamEntity>() {
        @Override
        public CookParamEntity createFromParcel(Parcel in) {
            return new CookParamEntity(in);
        }

        @Override
        public CookParamEntity[] newArray(int size) {
            return new CookParamEntity[size];
        }
    };

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public float getDefineTime() {
        return defineTime;
    }

    public void setDefineTime(float defineTime) {
        this.defineTime = defineTime;
    }

    public float getTimeRangeMin() {
        return timeRangeMin;
    }

    public void setTimeRangeMin(float timeRangeMin) {
        this.timeRangeMin = timeRangeMin;
    }

    public float getTimeRangeMax() {
        return timeRangeMax;
    }

    public void setTimeRangeMax(float timeRangeMax) {
        this.timeRangeMax = timeRangeMax;
    }

    public void setTimeRangeMax(int timeRangeMax) {
        this.timeRangeMax = timeRangeMax;
    }

    public int getDefineFirepower() {
        return defineFirepower;
    }

    public void setDefineFirepower(int defineFirepower) {
        this.defineFirepower = defineFirepower;
    }

    public int getFirepowerRangeMin() {
        return firepowerRangeMin;
    }

    public void setFirepowerRangeMin(int firepowerRangeMin) {
        this.firepowerRangeMin = firepowerRangeMin;
    }

    public int getFirepowerRangeMax() {
        return firepowerRangeMax;
    }

    public void setFirepowerRangeMax(int firepowerRangeMax) {
        this.firepowerRangeMax = firepowerRangeMax;
    }

    public int getDefineFirepowerTwo() {
        return defineFirepowerTwo;
    }

    public void setDefineFirepowerTwo(int defineFirepowerTwo) {
        this.defineFirepowerTwo = defineFirepowerTwo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(modeId);
        dest.writeString(modeType);
        dest.writeString(modeName);
        dest.writeFloat(defineTime);
        dest.writeFloat(timeRangeMin);
        dest.writeFloat(timeRangeMax);
        dest.writeInt(defineFirepower);
        dest.writeInt(firepowerRangeMin);
        dest.writeInt(firepowerRangeMax);
        dest.writeInt(defineFirepowerTwo);
    }

    public class Builder {
        private final int modeId;
        private final String modeType;
        private final String modeName;
        // 以 s 为单位
        private final float defineTime;
        private final float timeRangeMin;
        private final float timeRangeMax;
        // 火力 ，可能是  温度 或者  微波的火力值
        private final int defineFirepower;
        private final int firepowerRangeMin;
        private final int firepowerRangeMax;
        private int defineFirepowerTwo;

        public Builder(int modeId, String modeType, String modeName, float defineTime, float timeRangeMin, float timeRangeMax,
                       int defineFirepower, int firepowerRangeMin, int firepowerRangeMax) {
            this.modeId = modeId;
            this.modeType = modeType;
            this.modeName = modeName;
            this.defineTime = defineTime;
            this.timeRangeMin = timeRangeMin;
            this.timeRangeMax = timeRangeMax;
            this.defineFirepower = defineFirepower;
            this.firepowerRangeMin = firepowerRangeMin;
            this.firepowerRangeMax = firepowerRangeMax;
        }

        public Builder setdefineFirepowerTwo(int defineFirepowerTwo) {
            this.defineFirepowerTwo = defineFirepowerTwo;
            return this;
        }

        public CookParamEntity build() {
            return new CookParamEntity(this);
        }

    }

    @Override
    public String toString() {
        return "CookParamEntity{" +
                "modeId=" + modeId +
                ", modeType='" + modeType + '\'' +
                ", modeName='" + modeName + '\'' +
                ", defineTime=" + defineTime +
                ", timeRangeMin=" + timeRangeMin +
                ", timeRangeMax=" + timeRangeMax +
                ", defineFirepower=" + defineFirepower +
                ", firepowerRangeMin=" + firepowerRangeMin +
                ", firepowerRangeMax=" + firepowerRangeMax +
                ", defineFirepowerTwo=" + defineFirepowerTwo +
                '}';
    }
}
