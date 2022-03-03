package com.viomi.ovenso.bean;

/**
 * Created by Ljh on 2020/12/11.
 * Description:主题
 */
public enum OvenThemeType {
    THEME1("主题1", "img_theme1", "img_theme1"),
    THEME2("主题2", "img_theme2", "img_theme2");
    public String name, themeDrawablethumbnail, themeDrawable;

    OvenThemeType(String name, String themeDrawablethumbnail, String themeDrawable) {
        this.name = name;
        this.themeDrawablethumbnail = themeDrawablethumbnail;
        this.themeDrawable = themeDrawable;
    }
}

