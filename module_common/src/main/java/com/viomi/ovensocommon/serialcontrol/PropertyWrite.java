package com.viomi.ovensocommon.serialcontrol;

import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;

public abstract class PropertyWrite {
    private static final String TAG = "PropertyWrite";
    private PropertyEntity propertyEntity;
    private static final int DEFAULT_INT = -1;
    protected int sid = DEFAULT_INT;
    protected int pid = DEFAULT_INT;

    public void executeWrite(Object content) {
        initPropertyEntity();
        if (sid == DEFAULT_INT || pid == DEFAULT_INT) {
            throw new NullPointerException("sid pid must init");
        }
        if (propertyEntity == null) {
            propertyEntity = new PropertyEntity();
            propertyEntity.setSid(this.sid);
            propertyEntity.setPid(this.pid);
        }
        propertyEntity.setContent(content);
        PropertyWriteManager.getInstance().setProperty(propertyEntity);
    }

    public abstract void initPropertyEntity();
}
