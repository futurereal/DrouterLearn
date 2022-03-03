package com.viomi.ffmpeg.codec.utils;

/**
 * @author lixinqi
 * @date 2021/09/13
 */
public interface VideoEditCallback {
    /**
     * 当前进度
     **/
    void onProgressUpdate(int progress);

    void onEditFinish(String destFilePah);
}
