package com.viomi.viot.rxjava2


import com.viomi.viot.utils.LogUtil
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/6/03
 *     desc   : RxBus
 *     version: 1.0
 * </pre>
 */
class VIotRxBus private constructor() {
    private val mBus: FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    fun post(msgId: String, `object`: Any?, extraObject: Any?) = takeIf { hasSubscribers() }?.run { post(VIotBusEvent(msgId, `object`, extraObject)) }

    fun post(msgId: String, `object`: Any?) = takeIf { hasSubscribers() }?.run { post(VIotBusEvent(msgId, `object`, null)) }

    fun post(msgId: String) = takeIf { hasSubscribers() }?.run { mBus.onNext(VIotBusEvent(msgId, null, null)) }

    private fun post(`object`: Any) = takeIf { hasSubscribers() }?.run { mBus.onNext(`object`) }

    private fun hasSubscribers(): Boolean {
        return mBus.hasSubscribers()
    }

    fun subscribe(onNext: Consumer<in VIotBusEvent>): Disposable {
        return toFlowable(VIotBusEvent::class.java)
            .onBackpressureDrop()
            .onTerminateDetach()
            .subscribe(onNext, { throwable: Throwable ->
                LogUtil.e("subscribe error", throwable.toString())
                throwable.printStackTrace()
                subscribe(onNext)
            })
    }

    private fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        return mBus.ofType(tClass)
    }

    fun toFlowable(): Flowable<Any> {
        return mBus
    }

    companion object {
        val instance: VIotRxBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VIotRxBus()
        }
    }
}
