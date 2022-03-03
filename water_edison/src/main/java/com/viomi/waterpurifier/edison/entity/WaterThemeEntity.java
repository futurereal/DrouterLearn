package com.viomi.waterpurifier.edison.entity;

/**
 * @description:
 * @data:2021/10/21
 */
public class WaterThemeEntity {
    private int nameId;
    private int resourceId;
    private boolean selected;


    public WaterThemeEntity(int nameId, int resourceId, boolean selected) {
        this.nameId = nameId;
        this.resourceId = resourceId;
        this.selected = selected;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
