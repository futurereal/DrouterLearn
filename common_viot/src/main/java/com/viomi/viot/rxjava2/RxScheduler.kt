package com.viomi.viot.rxjava2

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
 *     time   : 2020/5/13
 *     desc   : Rxjava 线程调度器
 *     version: 1.0
 * </pre>
 */
object RxScheduler {

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
