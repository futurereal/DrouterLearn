package com.viomi.iotdevice.common.util

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2018/1/3
 *     desc   : 统一管理观察者模式中线程调度（使用 RxJava2 过程中会频繁
 *              调用 subscribeOn() 和 observeOn()，通过 Transformer 结合
 *              Observable.compose() 可实现复用）
 *     version: 1.0
 * </pre>
 */
object RxSchedulerUtil {
    fun <T> schedulerTransformer1(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> schedulerFlowableTransformer1(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> schedulerTransformer2(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()) }
    }

    fun <T> schedulerFlowableTransformer2(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()) }
    }

    fun <T> schedulerTransformer3(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()) }
    }

    fun <T> schedulerFlowableTransformer3(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()) }
    }

    fun <T> schedulerTransformer4(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> schedulerFlowableTransformer4(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> -> upstream.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> schedulerTransformer5(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.computation()).observeOn(Schedulers.io()) }
    }

    fun <T> schedulerFlowableTransformer5(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> -> upstream.subscribeOn(Schedulers.computation()).observeOn(Schedulers.io()) }
    }

    fun <T> schedulerTransformer6(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> -> upstream.subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()) }
    }

    fun <T> schedulerTransformer7(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}
