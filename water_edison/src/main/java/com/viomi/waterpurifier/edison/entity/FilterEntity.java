package com.viomi.waterpurifier.edison.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @author :
 * @date 2021/12/15
 */
public class FilterEntity implements Parcelable {
    String filterName;
    int lefePercent;
    int siid;
    int aiid;
    int lifeLevelSiid;
    int lifeLevelPiid;
    int timeSiid;
    int timePiid;
    int useFlowSiid;
    int useFlowPiid;
    int qrcodeImgResourceId;

    public FilterEntity() {

    }

    protected FilterEntity(Parcel in) {
        filterName = in.readString();
        lefePercent = in.readInt();
        siid = in.readInt();
        aiid = in.readInt();
        lifeLevelSiid = in.readInt();
        lifeLevelPiid = in.readInt();
        timeSiid = in.readInt();
        timePiid = in.readInt();
        useFlowSiid = in.readInt();
        useFlowPiid = in.readInt();
        qrcodeImgResourceId = in.readInt();
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getLefePercent() {
        return lefePercent;
    }

    public void setLefePercent(int lefePercent) {
        this.lefePercent = lefePercent;
    }

    public int getSiid() {
        return siid;
    }

    public void setSiid(int siid) {
        this.siid = siid;
    }

    public int getAiid() {
        return aiid;
    }

    public void setAiid(int aiid) {
        this.aiid = aiid;
    }

    public int getLifeLevelSiid() {
        return lifeLevelSiid;
    }

    public void setLifeLevelSiid(int lifeLevelSiid) {
        this.lifeLevelSiid = lifeLevelSiid;
    }

    public int getLifeLevelPiid() {
        return lifeLevelPiid;
    }

    public void setLifeLevelPiid(int lifeLevelPiid) {
        this.lifeLevelPiid = lifeLevelPiid;
    }

    public int getTimeSiid() {
        return timeSiid;
    }

    public void setTimeSiid(int timeSiid) {
        this.timeSiid = timeSiid;
    }

    public int getTimePiid() {
        return timePiid;
    }

    public void setTimePiid(int timePiid) {
        this.timePiid = timePiid;
    }

    public int getUseFlowSiid() {
        return useFlowSiid;
    }

    public void setUseFlowSiid(int useFlowSiid) {
        this.useFlowSiid = useFlowSiid;
    }

    public int getUseFlowPiid() {
        return useFlowPiid;
    }

    public void setUseFlowPiid(int useFlowPiid) {
        this.useFlowPiid = useFlowPiid;
    }

    public int getQrcodeImgResourceId() {
        return qrcodeImgResourceId;
    }

    public void setQrcodeImgResourceId(int qrcodeImgResourceId) {
        this.qrcodeImgResourceId = qrcodeImgResourceId;
    }

    public static final Creator<FilterEntity> CREATOR = new Creator<FilterEntity>() {
        @Override
        public FilterEntity createFromParcel(Parcel in) {
            return new FilterEntity(in);
        }

        @Override
        public FilterEntity[] newArray(int size) {
            return new FilterEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterEntity that = (FilterEntity) o;
        return lifeLevelSiid == that.lifeLevelSiid && lifeLevelPiid == that.lifeLevelPiid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lifeLevelSiid, lifeLevelPiid);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filterName);
        dest.writeInt(lefePercent);
        dest.writeInt(siid);
        dest.writeInt(aiid);
        dest.writeInt(lifeLevelSiid);
        dest.writeInt(lifeLevelPiid);
        dest.writeInt(timeSiid);
        dest.writeInt(timePiid);
        dest.writeInt(useFlowSiid);
        dest.writeInt(useFlowPiid);
        dest.writeInt(qrcodeImgResourceId);
    }

    @Override
    public String toString() {
        return "FilterEntity{" +
                "filterName='" + filterName + '\'' +
                ", lefePercent=" + lefePercent +
                ", siid=" + siid +
                ", aiid=" + aiid +
                ", lifeLevelSiid=" + lifeLevelSiid +
                ", lifeLevelPiid=" + lifeLevelPiid +
                ", timeSiid=" + timeSiid +
                ", timePiid=" + timePiid +
                ", useFlowSiid=" + useFlowSiid +
                ", useFlowPiid=" + useFlowPiid +
                ", qrcodeImgResourceId=" + qrcodeImgResourceId +
                '}';
    }
}
