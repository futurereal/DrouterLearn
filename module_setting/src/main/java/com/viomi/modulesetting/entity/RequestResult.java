package com.viomi.modulesetting.entity;

import java.io.Serializable;

/**
 * 网络请求返回
 * Created by nanquan on 2018/2/6.
 */
public class RequestResult<T> implements Serializable {

    private final long SerializableID = 123345456L;

    private MobBaseRes<T> mobBaseRes;

    public MobBaseRes<T> getMobBaseRes() {
        return mobBaseRes;
    }

    public void setMobBaseRes(MobBaseRes<T> mobBaseRes) {
        this.mobBaseRes = mobBaseRes;
    }
}
