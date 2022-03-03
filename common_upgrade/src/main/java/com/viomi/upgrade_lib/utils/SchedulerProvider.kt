package com.viomi.upgrade_lib.utils

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by mai on 2020/12/2.
 */
class SchedulerProvider {


    companion object {
        fun io(): Scheduler? {
            return Schedulers.io()
        }

        fun ui(): Scheduler? {
            return AndroidSchedulers.mainThread()
        }

        fun <T> applySchedulers(): ObservableTransformer<T, T>? {
            return ObservableTransformer { observable: Observable<T> ->
                observable.observeOn(
                    ui()
                ).subscribeOn(io())
            }
        }

        fun <T> applySchedulers2(): ObservableTransformer<T, T>? {
            return ObservableTransformer { observable: Observable<T> ->
                observable.observeOn(
                    io()
                ).subscribeOn(io())
            }
        }
    }

}