package com.viomi.modulesetting.http.download;

/**
 * <p>descript：下载进度回调<p>
 * <p>author：randysu<p>
 * <p>create time：2018/10/30<p>
 * <p>update time：2018/10/30<p>
 * <p>version：1<p>
 */

public interface DownloadFeedbackCallback {

    /**
     * 开始下载
     *
     * @param downloadId
     * @param totalBytes
     */
    void onStart(int downloadId, long totalBytes);

    /**
     * 重试
     *
     * @param downloadId
     */
    void onRetry(int downloadId);

    /**
     * 下载进度回调
     *
     * @param downloadId
     * @param bytesWritten
     * @param totalBytes
     */
    void onProgress(int downloadId, long bytesWritten, long totalBytes);

    /**
     * 下载成功
     *
     * @param downloadId
     * @param filePath
     */
    void onSuccess(int downloadId, String filePath);

    /**
     * 下载失败
     *
     * @param downloadId
     * @param statusCode
     * @param errMsg
     */
    void onFailure(int downloadId, int statusCode, String errMsg);

}
