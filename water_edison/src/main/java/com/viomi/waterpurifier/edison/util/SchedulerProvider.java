package com.viomi.waterpurifier.edison.util;

import androidx.annotation.NonNull;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @data:2021/12/15
 */
public class SchedulerProvider {
    private static SchedulerProvider instance;

    public static synchronized SchedulerProvider getInstance() {
        if (instance == null) {
            Class var0 = SchedulerProvider.class;
            synchronized (SchedulerProvider.class) {
                instance = new SchedulerProvider();
            }
        }

        return instance;
    }

    private SchedulerProvider() {
    }

    @NonNull
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @NonNull
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }

    @NonNull
    public <T> ObservableTransformer<T, T> applySchedulers() {
        return (observable) -> {
            return observable.subscribeOn(this.io()).observeOn(this.ui());
        };
    }
}
