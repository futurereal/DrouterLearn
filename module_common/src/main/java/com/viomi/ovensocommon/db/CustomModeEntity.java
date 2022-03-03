package com.viomi.ovensocommon.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

/**
 * @description:自定义模式， 包含自定义菜谱 和自定义模式
 * @data:2021/9/22 ‘组合模式id,组合模式名字,子模式1id, 时间,温度 ,子模式2id,时间,温度’
 */
@Entity(tableName = "tb_custom_mode")
public class CustomModeEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "index_name")
    private String indexName;
    @ColumnInfo(name = "mode_id")
    private int modeId;
    //用户上报插件
    @ColumnInfo(name = "mode_full_content")
    private String modeFullContent;
    @ColumnInfo(name = "mode_name")
    private String modeName;

    @Ignore
    private ArrayList<CookParamEntity> cookParamEntities;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }

    public String getModeFullContent() {
        return modeFullContent;
    }

    public void setModeFullContent(String modeFullContent) {
        this.modeFullContent = modeFullContent;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public ArrayList<CookParamEntity> getCookParamEntities() {
        return cookParamEntities;
    }

    public void setCookParamEntities(ArrayList<CookParamEntity> cookParamEntities) {
        this.cookParamEntities = cookParamEntities;
    }
}
