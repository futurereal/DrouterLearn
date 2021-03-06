package com.viomi.modulesetting.http.download.lib;

import android.content.Context;
import android.net.Uri;


/**
 * A manager used to manage the downloading.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
public final class DownloadManager {
    private final Context context;
    private final Downloader downloader;
    private final int threadPoolSize;
    private final Logger logger;
    private DownloadRequestQueue downloadRequestQueue;

    DownloadManager(Builder builder) {
        context = Preconditions.checkNotNull(builder.context, "context == null").getApplicationContext();
        downloader = Preconditions.checkNotNull(builder.downloader, "downloader == null");
        threadPoolSize = builder.threadPoolSize;
        logger = builder.logger;
        downloadRequestQueue = new DownloadRequestQueue(threadPoolSize, logger);
        downloadRequestQueue.start();
    }

    /**
     * Add one download request into the queue.
     *
     * @param request download request
     * @return download id, if the id is not set, then manager will generate one.
     * if the request is in downloading, then -1 will be returned
     */
    public int add(DownloadRequest request) {
        request = Preconditions.checkNotNull(request, "request == null");
        if (isDownloading(request.uri().toString())) {
            return -1;
        }

        request.setContext(context);
        request.setDownloader(downloader.copy());

        /* add download request into download request queue */
        return downloadRequestQueue.add(request) ? request.downloadId() : -1;
    }

    /**
     * Query download from download request queue.
     *
     * @param downloadId download id
     * @return download state
     */
    DownloadState query(int downloadId) {
        return downloadRequestQueue.query(downloadId);
    }

    /**
     * Query download from download request queue.
     *
     * @param url download url
     * @return download state
     */
    DownloadState query(String url) {
        return downloadRequestQueue.query(Uri.parse(url));
    }

    /**
     * To check if the download was in the request queue.
     *
     * @param downloadId downalod id
     * @return true if was downloading, otherwise return false
     */
    public boolean isDownloading(int downloadId) {
        return query(downloadId) != DownloadState.INVALID;
    }

    /**
     * To check if the download was in the request queue.
     *
     * @param url downalod url
     * @return true if was downloading, otherwise return false
     */
    public boolean isDownloading(String url) {
        return query(url) != DownloadState.INVALID;
    }

    /**
     * Get the download task size.
     *
     * @return the task size
     */
    public int getTaskSize() {
        return downloadRequestQueue == null ? 0 : downloadRequestQueue.getDownloadingSize();
    }

    /**
     * Cancel the download according to download id.
     *
     * @param downloadId download id
     */
    public void cancel(int downloadId) {
        downloadRequestQueue.cancel(downloadId);
    }

    /**
     * Cancel all the downloading in queue.
     */
    public void cancelAll() {
        downloadRequestQueue.cancelAll();
    }

    /**
     * Release all the resource.
     */
    public void release() {
        if (downloadRequestQueue != null) {
            downloadRequestQueue.release();
            downloadRequestQueue = null;
        }
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Context context;
        private Downloader downloader;
        private int threadPoolSize;
        private Logger logger;

        public Builder() {
            this.downloader = Utils.createDefaultDownloader();
            this.threadPoolSize = 3;
            this.logger = Logger.EMPTY;
        }

        Builder(DownloadManager downloadManager) {
            this.context = downloadManager.context;
            this.downloader = downloadManager.downloader;
            this.threadPoolSize = downloadManager.threadPoolSize;
            this.logger = downloadManager.logger;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder downloader(Downloader downloader) {
            this.downloader = downloader;
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public DownloadManager build() {
            return new DownloadManager(this);
        }
    }
}
