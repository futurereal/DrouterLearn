package com.viomi.modulesetting.http.download;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.http.download.lib.DownloadCallback;
import com.viomi.modulesetting.http.download.lib.DownloadManager;
import com.viomi.modulesetting.http.download.lib.DownloadRequest;
import com.viomi.modulesetting.http.download.lib.OkHttpDownloader;
import com.viomi.modulesetting.http.download.lib.Priority;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Copyright (C), 2014-2019, 佛山云米科技有限公司
 *
 * @Description: 文件下载抽象类
 */
public abstract class AbsFileDownloadManager implements FileDownloadConfig {

    private static final String TAG = AbsFileDownloadManager.class.getName();

    private static final long time_out = 20 * 1000;

    private final DownloadManager downloadManager;
    private int downloadTaskId = 100;
    protected DownloadFeedbackCallback superCallback;

    public abstract void setCallback(DownloadFeedbackCallback callback);

    public AbsFileDownloadManager() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(time_out, TimeUnit.MILLISECONDS)
                // 读取超时
                .readTimeout(time_out, TimeUnit.MILLISECONDS)
                // 写入超时
                .writeTimeout(time_out, TimeUnit.MILLISECONDS)
                .build();
        downloadManager =
                new DownloadManager.Builder().context(Utils.getApp())
                        .downloader(OkHttpDownloader.create(client))
                        .threadPoolSize(3)
                        .logger(message -> Log.d("TAG", message))
                        .build();
    }

    // 已下载完整的旧文件需要删除
    @Override
    public boolean deleteFile(String savePath, File deleteFile) {
        if (deleteFile == null) {
            return false;
        }

        if (deleteFile.isFile()) {
            return deleteFile.delete();
        }

        return false;
    }

    @Override
    public int startToDownloadFile(String url, String savePath, File saveFile) {
        deleteFile(savePath, saveFile);
        DownloadRequest request = new DownloadRequest.Builder()
                .url(url)
                .destinationDirectory(savePath)
                .destinationFilePath(saveFile.getAbsolutePath())
                .downloadCallback(new AbsFileDownloadCallback())
                .retryTime(500)
                .retryInterval(5, TimeUnit.SECONDS)
                .progressInterval(1, TimeUnit.SECONDS)
                .priority(Priority.HIGH)
                .allowedNetworkTypes(DownloadRequest.NETWORK_MOBILE)
                .allowedNetworkTypes(DownloadRequest.NETWORK_WIFI)
                .build();
        downloadTaskId = downloadManager.add(request);
        return downloadTaskId;
    }

    @Override
    public void cancelDownloadTask() {
        Log.d(TAG, "cancelDownloadTask downloadTaskId:" + downloadTaskId);
        if (downloadManager == null) {
            return;
        }
        if (downloadManager.isDownloading(downloadTaskId)) {
            downloadManager.cancel(downloadTaskId);
        }
    }

    @SuppressLint("UsableSpace")
    public class AbsFileDownloadCallback extends DownloadCallback {
        @Override
        public void onStart(int downloadId, long totalBytes) {
            Log.d(TAG, "start download: " + downloadId);
            Log.d(TAG, "totalBytes: " + totalBytes);
            // 判断是否还有足够的剩余空间
            long usableSpace = Environment.getExternalStorageDirectory().getUsableSpace();
            Log.i(TAG, "usableSpace:" + usableSpace / 1024 / 1024 + "totalBytes " + totalBytes / 1024 / 1024);
            if (superCallback == null) {
                Log.i(TAG, "onStart: callback is null return ");
                return;
            }
            if (usableSpace > totalBytes) {
                superCallback.onStart(downloadId, totalBytes);
                return;
            }
            superCallback.onFailure(downloadId, 401, ApplicationUtils.getContext().getString(R.string.modeset_no_enughtspace));
            if (downloadManager.isDownloading(downloadTaskId)) {
                downloadManager.cancel(downloadTaskId);
            }
        }

        @Override
        public void onRetry(int downloadId) {
            Log.d(TAG, "retry downloadId: " + downloadId);
            if (superCallback != null) {
                superCallback.onRetry(downloadId);
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
            Log.d(TAG, "onProgress downloadId: " + downloadId
                    + "  bytesWritten: " + bytesWritten
                    + "  totalBytes: " + totalBytes);

            if (superCallback != null) {
                superCallback.onProgress(downloadId, bytesWritten, totalBytes);
            }
        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
            Log.d(TAG, "success: " + downloadId
                    + " filePath: " + filePath
                    + " size: " + new File(filePath).length());

            if (superCallback != null) {
                superCallback.onSuccess(downloadId, filePath);
            }
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
            Log.d(TAG, "fail: " + downloadId + " " + statusCode + " " + errMsg);
            if (superCallback != null) {
                superCallback.onFailure(downloadId, statusCode, errMsg);
            }
        }
    }

}
