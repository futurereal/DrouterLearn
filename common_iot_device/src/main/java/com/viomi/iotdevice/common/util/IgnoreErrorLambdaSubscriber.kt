package com.viomi.iotdevice.common.util

import io.reactivex.rxjava3.core.FlowableSubscriber
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.exceptions.CompositeException
import io.reactivex.rxjava3.exceptions.Exceptions
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.internal.subscriptions.SubscriptionHelper
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import org.reactivestreams.Subscription
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.atomic.AtomicReference

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/12/31
 *     desc   : 防止下游抛出异常时自动取消订阅.
 *     version: 1.0
 * </pre>
 */
class IgnoreErrorLambdaSubscriber<T>(
    private val onNext: Consumer<in T>, private val onError: Consumer<in Throwable>,
    private val onComplete: Action,
    private val onSubscribe: Consumer<in Subscription>
) : AtomicReference<Subscription>(), FlowableSubscriber<T>, Subscription, Disposable {

    override fun onSubscribe(subscription: Subscription) {
        takeIf { SubscriptionHelper.setOnce(this, subscription) }?.runCatching { onSubscribe.accept(this) }?.onFailure {
            Exceptions.throwIfFatal(it)
            subscription.cancel()
            onError(it)
        }
    }

    override fun onNext(t: T) {
        takeUnless { isDisposed }?.runCatching { onNext.accept(t) }?.onFailure {
            Exceptions.throwIfFatal(it)
            onError(it)
        }
    }

    override fun onError(throwable: Throwable) {
        if (get() !== SubscriptionHelper.CANCELLED) {
            // lazySet(SubscriptionHelper.CANCELLED);
            kotlin.runCatching { onError.accept(throwable) }.onFailure {
                Exceptions.throwIfFatal(it)
                RxJavaPlugins.onError(CompositeException(throwable, it))
            }
        } else {
            RxJavaPlugins.onError(throwable)
        }
    }

    override fun onComplete() {
        takeIf { get() !== SubscriptionHelper.CANCELLED }?.runCatching {
            lazySet(SubscriptionHelper.CANCELLED)
            onComplete.run()
        }?.onFailure {
            Exceptions.throwIfFatal(it)
            RxJavaPlugins.onError(it)
        }
    }

    override fun dispose() {
        cancel()
    }

    override fun isDisposed(): Boolean {
        return get() === SubscriptionHelper.CANCELLED
    }

    override fun request(n: Long) {
        get().request(n)
    }

    override fun cancel() {
        SubscriptionHelper.cancel(this)
    }

    private fun setRxJavaErrorHandler() = takeUnless { RxJavaPlugins.getErrorHandler() != null || RxJavaPlugins.isLockdown() }?.run {
        RxJavaPlugins.setErrorHandler {
            if (it is UndeliverableException) {
                it.printStackTrace()
            }
            if (it is IOException || it is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (it is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (it is NullPointerException || it is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), it)
                return@setErrorHandler
            }
            if (it is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), it)
                return@setErrorHandler
            }
            LogUtil.w("Undeliverable exception", it.localizedMessage?.toString() ?: "")
        }
    }

    companion object {
        private const val serialVersionUID = -7251123623727029452L
    }

    init {
        setRxJavaErrorHandler()
    }
}
