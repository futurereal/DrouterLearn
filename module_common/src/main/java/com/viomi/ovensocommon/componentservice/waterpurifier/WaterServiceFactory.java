package com.viomi.ovensocommon.componentservice.waterpurifier;

import android.util.Log;

public class WaterServiceFactory {

    private IWaterService waterService;

    //不允许外部创建
    private WaterServiceFactory() {
    }

    public static WaterServiceFactory getInstance() {
        return Inner.factory;
    }

    public IWaterService getWaterService() {
        if (waterService == null) {
            waterService = new EmptyWaterService();
        }
        return waterService;
    }

    public void setWaterService(IWaterService waterService) {
        Log.d("WPServiceFactory", "setWaterPurifierService: ");
        this.waterService = waterService;
    }

    // 静态内部类
    private static class Inner {
        private static final WaterServiceFactory factory = new WaterServiceFactory();
    }
}
