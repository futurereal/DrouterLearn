package com.viomi.waterpurifier.edison.entity;

/**
 * @description:
 * @data:2021/11/22
 */
public class FilterWashTitleEntity {
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_COMING = 2;
    public static final int STATUS_FINISH = 2;

    private String titleName;
    private int stepStatus;

    public FilterWashTitleEntity() {

    }

    public FilterWashTitleEntity(String titleName, int stepStatus) {
        this.titleName = titleName;
        this.stepStatus = stepStatus;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(int stepStatus) {
        this.stepStatus = stepStatus;
    }
}
