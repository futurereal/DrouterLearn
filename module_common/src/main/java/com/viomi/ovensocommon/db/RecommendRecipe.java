package com.viomi.ovensocommon.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Ljh on 2020/11/18.
 * Description:
 */
@Entity(tableName = "tb_recommend_recipe")
public class RecommendRecipe implements Parcelable {
    // 菜谱id 名字 模式
    @PrimaryKey
    int recipeId;
    String recipeName;
    int modeId;
    String modeType;
    int cookTime;
    int cookFirepower;
    int cookFirepowerTwo;
    boolean preHeating;//是否需要预热
    String material;
    String cookMethod;
    // 其他自定义的属性
    boolean collected;

    public RecommendRecipe() {
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

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

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getCookFirepower() {
        return cookFirepower;
    }

    public void setCookFirepower(int cookFirepower) {
        this.cookFirepower = cookFirepower;
    }

    public int getCookFirepowerTwo() {
        return cookFirepowerTwo;
    }

    public void setCookFirepowerTwo(int cookFirepowerTwo) {
        this.cookFirepowerTwo = cookFirepowerTwo;
    }

    public boolean isPreHeating() {
        return preHeating;
    }

    public void setPreHeating(boolean preHeating) {
        this.preHeating = preHeating;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCookMethod() {
        return cookMethod;
    }

    public void setCookMethod(String cookMethod) {
        this.cookMethod = cookMethod;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }


    protected RecommendRecipe(Parcel in) {
        recipeId = in.readInt();
        recipeName = in.readString();
        modeId = in.readInt();
        modeType = in.readString();
        cookTime = in.readInt();
        cookFirepower = in.readInt();
        cookFirepowerTwo = in.readInt();
        preHeating = in.readByte() != 0;
        material = in.readString();
        cookMethod = in.readString();
        collected = in.readByte() != 0;
    }

    public static final Creator<RecommendRecipe> CREATOR = new Creator<RecommendRecipe>() {
        @Override
        public RecommendRecipe createFromParcel(Parcel in) {
            return new RecommendRecipe(in);
        }

        @Override
        public RecommendRecipe[] newArray(int size) {
            return new RecommendRecipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(recipeName);
        dest.writeInt(modeId);
        dest.writeString(modeType);
        dest.writeInt(cookTime);
        dest.writeInt(cookFirepower);
        dest.writeInt(cookFirepowerTwo);
        dest.writeByte((byte) (preHeating ? 1 : 0));
        dest.writeString(material);
        dest.writeString(cookMethod);
        dest.writeByte((byte) (collected ? 1 : 0));
    }
}

