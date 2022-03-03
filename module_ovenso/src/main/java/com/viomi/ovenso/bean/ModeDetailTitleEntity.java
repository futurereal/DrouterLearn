package com.viomi.ovenso.bean;

/**
 * @description:
 * @data:2021/9/13
 */
public class ModeDetailTitleEntity {
    private String titleName;
    private int index;
    private boolean isSelect;

    public ModeDetailTitleEntity() {
    }

    public ModeDetailTitleEntity(String titleName, int index, boolean isSelect) {
        this.titleName = titleName;
        this.index = index;
        this.isSelect = isSelect;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "ModeDetailTitleEntity{" +
                "titleName='" + titleName + '\'' +
                ", index=" + index +
                ", isSelect=" + isSelect +
                '}';
    }
}
