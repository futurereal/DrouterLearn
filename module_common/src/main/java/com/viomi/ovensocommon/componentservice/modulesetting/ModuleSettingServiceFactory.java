package com.viomi.ovensocommon.componentservice.modulesetting;

public class ModuleSettingServiceFactory {

    private IModuleSettingService viotService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private ModuleSettingServiceFactory() {
        
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static ModuleSettingServiceFactory getInstance() {
        return Inner.accountServiceFactory;
    }

    private static class Inner {
        private static final ModuleSettingServiceFactory accountServiceFactory = new ModuleSettingServiceFactory();
    }

    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setViotService(IModuleSettingService accountService) {
        this.viotService = accountService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public IModuleSettingService getViotService() {
        if (viotService == null) {
            viotService = new EmptyModuleSettingService();
        }
        return viotService;
    }
}
