package com.viomi.ovensocommon.componentservice.ovenso;

public class OvensoServiceFactory {

    private IOvensoService ovensoService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private OvensoServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static OvensoServiceFactory getInstance() {
        return Inner.ovensoServiceFactory;
    }

    private static class Inner {
        private static final OvensoServiceFactory ovensoServiceFactory = new OvensoServiceFactory();
    }

    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setOvenService(IOvensoService ovensoService) {
        this.ovensoService = ovensoService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public IOvensoService getOvenService() {
        if (ovensoService == null) {
            ovensoService = new EmptyOvensoService();
        }
        return ovensoService;
    }
}
