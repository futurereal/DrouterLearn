package com.viomi.camera;

/**
 * RxBus 消息定义
 * Created by nanquan on 2018/1/25.
 */
public class CameraRxBusEvent {

    /**
     * 录屏结束
     */
    public static final int MSG_RECORD_WITH_VIDEO = 401;
    public static final int MSG_UPLOAD_VIDEO_SUCCESS = 403;
    public static final int MSG_UPLOAD_PROGRESS = 404;
    public static final int MSG_UPLOAD_VIDEO_FAIL = 405;
    public static final int MSG_UPLOAD_COVER_FAIL = 406;
    public static final int MSG_UPLOAD_PREPARE = 407;
    public static int MSG_UPLOAD_COVER_SUCCESS = 408;

}
