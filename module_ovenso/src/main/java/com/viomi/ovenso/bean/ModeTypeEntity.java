package com.viomi.ovenso.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.viomi.ovensocommon.db.CookParamEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 烤箱模式的 实体类
 */
public class ModeTypeEntity implements Parcelable {
    private String modeType;
    private String name;
    private int modeId;
    private String resIdBg;
    private String resIdBgCombine;
    private String resIdTip;
    private ArrayList<CookParamEntity> cookParamEntityList;

    public ModeTypeEntity() {
    }

    protected ModeTypeEntity(Parcel in) {
        modeType = in.readString();
        name = in.readString();
        modeId = in.readInt();
        resIdBg = in.readString();
        resIdBgCombine = in.readString();
        resIdTip = in.readString();
        cookParamEntityList = in.createTypedArrayList(CookParamEntity.CREATOR);
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getResIdBg() {
        return resIdBg;
    }

    public void setResIdBg(String resIdBg) {
        this.resIdBg = resIdBg;
    }

    public String getResIdBgCombine() {
        return resIdBgCombine;
    }

    public void setResIdBgCombine(String resIdBgCombine) {
        this.resIdBgCombine = resIdBgCombine;
    }

    public String getResIdTip() {
        return resIdTip;
    }

    public void setResIdTip(String resIdTip) {
        this.resIdTip = resIdTip;
    }

    public ArrayList<CookParamEntity> getCookParamEntityList() {
        return cookParamEntityList;
    }

    public void setCookParamEntityList(ArrayList<CookParamEntity> cookParamEntityList) {
        this.cookParamEntityList = cookParamEntityList;
    }

    public static final Creator<ModeTypeEntity> CREATOR = new Creator<ModeTypeEntity>() {
        @Override
        public ModeTypeEntity createFromParcel(Parcel in) {
            return new ModeTypeEntity(in);
        }

        @Override
        public ModeTypeEntity[] newArray(int size) {
            return new ModeTypeEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(modeType);
        dest.writeString(name);
        dest.writeInt(modeId);
        dest.writeString(resIdBg);
        dest.writeString(resIdBgCombine);
        dest.writeString(resIdTip);
        dest.writeTypedList(cookParamEntityList);
    }


    @Override
    public String toString() {
        return "ModeTypeEntity{" +
                "modeType='" + modeType + '\'' +
                "modeId='" + modeId + '\'' +
                ", name='" + name + '\'' +
                ", resIdBg='" + resIdBg + '\'' +
                ", resIdBgCombine='" + resIdBgCombine + '\'' +
                ", resIdTip='" + resIdTip + '\'' +
                ", modelKeyList=" + (cookParamEntityList == null ? 0 : cookParamEntityList.size()) +
                '}';
    }

   /* @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModeTypeEntity that = (ModeTypeEntity) o;
        return modeId == that.modeId && name == that.name;
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(modeType, name, modeId, resIdBg, resIdBgCombine, resIdTip, cookParamEntityList);
    }

    public static ModeTypeEntity deepCopy(ModeTypeEntity currrentModeTypeEntity) {
        ModeTypeEntity modeTypeEntity = new ModeTypeEntity();
        modeTypeEntity.modeId = currrentModeTypeEntity.modeId;
        modeTypeEntity.name = currrentModeTypeEntity.name;
        modeTypeEntity.modeType = currrentModeTypeEntity.modeType;
        modeTypeEntity.resIdBg = currrentModeTypeEntity.resIdBg;
        modeTypeEntity.resIdBgCombine = currrentModeTypeEntity.resIdBgCombine;
        modeTypeEntity.resIdBgCombine = currrentModeTypeEntity.resIdBgCombine;
        modeTypeEntity.resIdTip = currrentModeTypeEntity.resIdTip;
        List<CookParamEntity> cookParamEntityList = currrentModeTypeEntity.getCookParamEntityList();
        ArrayList<CookParamEntity> newCookParamList = new ArrayList<>(cookParamEntityList.size());
        for (int i = 0; i < cookParamEntityList.size(); i++) {
            CookParamEntity newCookParameter = new CookParamEntity();
            CookParamEntity currentCookParamEntity = cookParamEntityList.get(i);
            newCookParameter.setModeId(currentCookParamEntity.getModeId());
            newCookParameter.setDefineFirepower(currentCookParamEntity.getDefineFirepower());
            newCookParameter.setDefineTime(currentCookParamEntity.getDefineTime());
            newCookParameter.setModeType(currentCookParamEntity.getModeType());
            newCookParamList.add(newCookParameter);
        }
        modeTypeEntity.setCookParamEntityList(newCookParamList);
        return modeTypeEntity;

    }


}