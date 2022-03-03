package com.viomi.ovensocommon.componentservice.camera;

public class CameraServiceFactory {

    private ICameraService cameraService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private CameraServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static CameraServiceFactory getInstance() {
        return Inner.cameraServiceFactory;
    }

    private static class Inner {
        private static final CameraServiceFactory cameraServiceFactory = new CameraServiceFactory();
    }

    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setCameraService(ICameraService accountService) {
        this.cameraService = accountService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public ICameraService getCameraService() {
        if (cameraService == null) {
            cameraService = new EmptyCameraService();
        }
        return cameraService;
    }
}
