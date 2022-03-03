package com.viomi.modulesetting.config;

import android.content.Context;

import com.viomi.modulesetting.entity.QRCodeBase;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Package: com.viomi.settingpagelib.config
 * @ClassName: DeviceBindCallback
 * @Description: 设备绑定回调，这些方法是外部调用方进行具体实现，因为涉及到具体设备的独立配置、属性设置等个性化功能
 * @Author: randysu
 * @CreateDate: 2020/4/2 4:22 PM
 * @UpdateUser:
 * @UpdateDate: 2020/4/2 4:22 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public abstract class DeviceBindBaseMethod {

    /**
     * 具体实现方法
     */
    public void deviceBindImpl(Context context, QRCodeBase qrCodeBase) {
        initDeviceConfig(context, qrCodeBase);
        registerProperty(context, qrCodeBase);
        bindUploadProp();
    }

    /**
     * 初始化设备配置
     */
    public abstract void initDeviceConfig(Context context, QRCodeBase qrCodeBase);

    /**
     * 注册设备属性
     */
    public abstract void registerProperty(Context context, QRCodeBase qrCodeBase);

    /**
     * 属性上报
     */
    public abstract void bindUploadProp();

    /**
     * 程序终止后释放资源,未使用
     */
    @Deprecated
    public abstract void terminal();

}
