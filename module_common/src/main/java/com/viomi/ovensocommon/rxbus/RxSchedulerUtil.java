package com.viomi.ovensocommon.rxbus;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 统一管理观察者模式中线程调度（使用 RxJava2 过程中会频繁
 * 调用 subscribeOn() 和 observeOn()，通过 Transformer 结合
 * Observable.compose() 可实现复用）
 * <p>
 * Created by William on 2018/1/3.
 */
public final class RxSchedulerUtil {

    public static <T> ObservableTransformer<T, T> SchedulersTransformer1() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> SchedulersFlowableTransformer1() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer2() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> SchedulersFlowableTransformer2() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer3() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public static <T> FlowableTransformer<T, T> SchedulersFlowableTransformer3() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer4() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> SchedulersFlowableTransformer4() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer5() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> SchedulersFlowableTransformer5() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer6() {
        return upstream -> upstream.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation());
    }

    public static <T> ObservableTransformer<T, T> SchedulersTransformer7() {
        return upstream -> upstream.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
