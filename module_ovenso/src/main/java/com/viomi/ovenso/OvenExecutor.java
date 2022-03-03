package com.viomi.ovenso;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ljh on 2020/3/25.
 */
public class OvenExecutor {

    private static final String SUB_TAG = OvenExecutor.class.getCanonicalName();

    private final ExecutorService mDiskIO;

    private final ExecutorService mNetworkIO;

    private final Executor mMainThread;

    public OvenExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
        mNetworkIO = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        mMainThread = new MainThreadExecutor();
    }

    public ExecutorService diskIO() {
        return mDiskIO;
    }

    public ExecutorService netWorkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {

        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
