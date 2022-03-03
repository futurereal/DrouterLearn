/**
 * Copyright 2017 区长
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viomi.camera.http;

import android.util.Log;


import java.io.IOException;
import java.io.OutputStream;

/**
 * 带进度的输出流
 */
class ProgressOutputStream extends OutputStream {
    private static final String TAG = "ProgressOutputStream";
    private final OutputStream stream;
    private final ProgressCallback listener;

    private final long total;
    private long totalWritten;
    private int beforeProgress = 0;

    ProgressOutputStream(OutputStream stream, ProgressCallback listener, long total) {
        this.stream = stream;
        this.listener = listener;
        this.total = total;
    }

    @Override
    public void write(byte[] mByte, int off, int len) throws IOException {
        Log.i(TAG, "write: this.total  " + total + " totalWritten: " + totalWritten);
        this.stream.write(mByte, off, len);
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return;
        }
        if (len < mByte.length) {
            this.totalWritten += len;
        } else {
            this.totalWritten += mByte.length;
        }
        int progress = (int) (totalWritten * 1.0f / total * 100);
        if (beforeProgress == progress) {
            return;
        }
        Log.i(TAG, "write: progress: " + progress);
        this.listener.onProgressChanged(this.totalWritten, this.total, progress);
        beforeProgress = progress;
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b);
        Log.i(TAG, "write: b  " + b);
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1);
            return;
        }
        this.totalWritten++;
        Log.i(TAG, "write: single  " + totalWritten);
        int progress = (int) (totalWritten * 1.0f / total * 100);
        if (beforeProgress == progress) {
            return;
        }
        Log.i(TAG, "write: progress: " + progress);
        this.listener.onProgressChanged(this.totalWritten, this.total, progress);
    }

    @Override
    public void close() throws IOException {
        Log.i(TAG, "close: ");
        if (this.stream != null) {
            this.stream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        Log.i(TAG, "flush: ");
        if (this.stream != null) {
            this.stream.flush();
        }
    }
}
