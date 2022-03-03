package com.miotspecv2.defined.viomi;

import java.util.Map;

/**
 * Created by Ljh on 2021/3/9.
 * Description:连接小米服务器
 */
public interface MiotDataCallback {
    /**
     * Miot平台Get/Set/Action方法
     */
    void setAllPropertyHandler();

    /**
     * Miot平台PropertyChanged方法
     */
    void reportProperties(Map prop);


//    void registerEvent(Device mDevice,EventPack pack);

    void getAllProperty();
}

