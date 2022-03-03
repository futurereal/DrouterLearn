package com.viomi.ovenso.bean;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * <b>Project:</b> viomi_washer <br>
 * <b>Package:</b> com.viomi.modulesetting.entity <br>
 * <b>Create Date:</b> 2020/11/13 <br>
 * <b>@author:</b> qingyong <br>
 * <b>Address:</b> qingyong@viomi.com <br>
 * <b>Description:</b> 主题 <br>
 */
public class ThemeItemEntity {

    /**
     * 名称
     */
    private String name;
    /**
     * 小图，在列表选项中展示的
     */
    private String themeDrawablethumbnail;
    /**
     * 大图，主题图片的名称
     */
    private String themeDrawable;
    /**
     * 是否选中
     */
    private boolean isSelect;

    public ThemeItemEntity() {
    }

    public ThemeItemEntity(String name, String themeDrawablethumbnail, String themeDrawable) {
        this.name = name;
        this.themeDrawablethumbnail = themeDrawablethumbnail;
        this.themeDrawable = themeDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThemeDrawablethumbnail() {
        return themeDrawablethumbnail;
    }

    public void setThemeDrawablethumbnail(String themeDrawablethumbnail) {
        this.themeDrawablethumbnail = themeDrawablethumbnail;
    }

    public String getThemeDrawable() {
        return themeDrawable;
    }

    public void setThemeDrawable(String themeDrawable) {
        this.themeDrawable = themeDrawable;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
