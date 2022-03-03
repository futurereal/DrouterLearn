package com.viomi.modulesetting.entity;

import androidx.annotation.Nullable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 设置菜单配置实体
 */
public class SettingMenuEntity {
    // 是否选中
    private String name;
    private String iconName;
    private String status;
    private String routePath;
    private boolean isShowArrow;
    private boolean isSelected;

    public SettingMenuEntity() {
    }

    public SettingMenuEntity(String name, String iconName, String status, String routePath) {
        this.name = name;
        this.iconName = iconName;
        this.status = status;
        this.routePath = routePath;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        SettingMenuEntity entity = (SettingMenuEntity) obj;
        return entity.name.equals(this.name) || entity.routePath.equals(this.routePath);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public boolean isShowArrow() {
        return isShowArrow;
    }

    public void setShowArrow(boolean showArrow) {
        isShowArrow = showArrow;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public String toString() {
        return "SettingMenuEntity{" +
                "name='" + name + '\'' +
                ", iconName='" + iconName + '\'' +
                ", status='" + status + '\'' +
                ", routePath='" + routePath + '\'' +
                ", isShowArrow=" + isShowArrow +
                ", isSelected=" + isSelected +
                '}';
    }
}
