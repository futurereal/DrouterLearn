package com.viomi.camera;

public interface CameraOprCallback {
    void capturePic();

    void recordVideo();

    void stopRecord(boolean save);

}
