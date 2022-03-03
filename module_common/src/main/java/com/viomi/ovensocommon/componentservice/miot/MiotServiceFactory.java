package com.viomi.ovensocommon.componentservice.miot;

import android.util.Log;

public class MiotServiceFactory {
    private static final String TAG = "MiotServiceFactory";
    private IMiotService accountService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private MiotServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static MiotServiceFactory getInstance() {
        return Inner.accountServiceFactory;
    }

    private static class Inner {
        private static final MiotServiceFactory accountServiceFactory = new MiotServiceFactory();
    }

    /**
     * 接收 Miot 组件实现的 Service 实例
     */
    public void setMiotService(IMiotService accountService) {
        Log.e(TAG, "setMiotService: " + accountService.getClass());
        this.accountService = accountService;
    }

    /**
     * 返回 Miot 组件的
     */
    public IMiotService getMiotService() {
        Log.i(TAG, "getMiotService: ");
        if (accountService == null) {
            accountService = new EmptyMiotService();
        }
        return accountService;
    }
}
