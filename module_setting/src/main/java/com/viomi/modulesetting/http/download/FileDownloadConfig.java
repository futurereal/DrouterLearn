package com.viomi.modulesetting.http.download;

import java.io.File;

/**
 * <p>descript：文件下载必备方法<p>
 * <p>author：randysu<p>
 * <p>create time：2018/10/31<p>
 * <p>update time：2018/10/31<p>
 * <p>version：1<p>
 */
public interface FileDownloadConfig {

    /**
     * 删除已经下载的完整的旧文件
     *
     * @return boolean
     */
    boolean deleteFile(String savePath, File deleteFile);

    /**
     * 下载文件
     *
     * @param url      网络地址
     * @param savePath 保存路径（没有具体文件名）
     * @param saveFile 保存文件
     */
    int startToDownloadFile(String url, String savePath, File saveFile);

    /**
     * 取消下载任务
     */
    void cancelDownloadTask();

}
