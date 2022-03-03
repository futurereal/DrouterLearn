package com.viomi.modulesetting.http.download.lib;

import java.io.IOException;

/**
 * An exception reprensent an error for download.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
public final class DownloadException extends IOException {
    private final int code;

    DownloadException(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    int getCode() {
        return code;
    }
}
