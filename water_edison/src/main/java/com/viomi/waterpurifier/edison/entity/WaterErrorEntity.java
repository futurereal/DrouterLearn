package com.viomi.waterpurifier.edison.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author hailang
 * @date 2020/9/14 001417:11
 */
public class WaterErrorEntity implements Parcelable {
    private int index;
    private String title;
    private String discription;
    private Object value;

    public WaterErrorEntity() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public WaterErrorEntity(int index, String title, String discription, Object value) {
        this.index = index;
        this.title = title;
        this.discription = discription;
        this.value = value;
    }

    protected WaterErrorEntity(Parcel in) {
        index = in.readInt();
        title = in.readString();
        discription = in.readString();
    }

    public static final Creator<WaterErrorEntity> CREATOR = new Creator<WaterErrorEntity>() {
        @Override
        public WaterErrorEntity createFromParcel(Parcel in) {
            return new WaterErrorEntity(in);
        }

        @Override
        public WaterErrorEntity[] newArray(int size) {
            return new WaterErrorEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(title);
        dest.writeString(discription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WaterErrorEntity waterErrorEntity = (WaterErrorEntity) o;
        return this.index == waterErrorEntity.index;
    }

    @Override
    public String toString() {
        return "WaterErrorEntity{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", discription='" + discription + '\'' +
                ", value=" + value +
                '}';
    }
}
