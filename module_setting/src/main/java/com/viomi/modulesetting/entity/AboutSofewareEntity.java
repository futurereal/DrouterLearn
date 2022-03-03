package com.viomi.modulesetting.entity;

/**
 * @description:关于软件List的实体类
 * @data:2021/7/20
 */
public class AboutSofewareEntity {
    private String title;
    private String content;
    private String routPath;
    private boolean isError = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoutPath() {
        return routPath;
    }

    public void setRoutPath(String routPath) {
        this.routPath = routPath;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
