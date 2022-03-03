package com.viomi.modulesetting.http.download.lib;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A downloader implemented by {@link OkHttpClient}.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
public final class OkHttpDownloader implements Downloader {
    private final OkHttpClient client;
    private Response response;
    private final AtomicInteger redirectionCount = new AtomicInteger();

    private static OkHttpClient defaultOkHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(Utils.DEFAULT_CONNECT_TIMEOUT, MILLISECONDS)
                .readTimeout(Utils.DEFAULT_READ_TIMEOUT, MILLISECONDS)
                .writeTimeout(Utils.DEFAULT_WRITE_TIMEOUT, MILLISECONDS)
                .build();
    }

    /**
     * Create an instance using a default {@link OkHttpClient}.
     *
     * @return {@link OkHttpDownloader}
     */
    public static OkHttpDownloader create() {
        return new OkHttpDownloader(null);
    }

    /**
     * Create an instance using a {@code client}.
     *
     * @return {@link OkHttpDownloader}
     */
    public static OkHttpDownloader create(OkHttpClient client) {
        return new OkHttpDownloader(client);
    }

    private OkHttpDownloader(OkHttpClient client) {
        this.client = client == null ? defaultOkHttpClient() : client;
    }

    @Override
    public String detectFilename(Uri uri) throws IOException {
        redirectionCount.set(Utils.MAX_REDIRECTION);
        Response response = innerRequest(client, uri, 0);
        String url = response.request().url().toString();
        String contentDisposition = response.header(Utils.CONTENT_DISPOSITION);
        response.close();
        return Utils.getFilenameFromHeader(url, contentDisposition);
    }

    @Override
    public int start(Uri uri, long breakpoint) throws IOException {
        redirectionCount.set(Utils.MAX_REDIRECTION);
        response = innerRequest(client, uri, breakpoint);
        return response.code();
    }

    @Override
    public long contentLength() {
        return response == null ? -1 : response.body().contentLength();
    }

    @Override
    public InputStream byteStream() {
        return response == null ? null : response.body().byteStream();
    }

    @Override
    public void close() {
        if (response != null) {
            response.close();
        }
    }

    @Override
    public Downloader copy() {
        return create(client);
    }

    Response innerRequest(OkHttpClient client, Uri uri, long breakpoint) throws IOException {
        Request.Builder builder = new Request.Builder().url(uri.toString());
        if (breakpoint > 0) {
            builder.header("Accept-Encoding", "identity")
                    .header("Range", "bytes=" + breakpoint + "-")
                    .build();
        }
        Response response = client.newCall(builder.build()).execute();
        int statusCode = response.code();
        switch (statusCode) {
            case 301:
            case 302:
            case 303:
            case Utils.HTTP_TEMP_REDIRECT:
                response.close();
                if (redirectionCount.decrementAndGet() >= 0) {
                    /* take redirect url and call start recursively */
                    String redirectUrl = response.header(Utils.LOCATION);
                    return innerRequest(client, Uri.parse(redirectUrl), breakpoint);
                } else {
                    throw new DownloadException(statusCode, "redirects too many times");
                }
        }

        return response;
    }
}
