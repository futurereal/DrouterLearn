package com.viomi.iotdevice.common.rxbus

import com.viomi.iotdevice.common.util.LogUtil
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : 发送: RxBus.getInstance().post(BusEvent.MSG_XXX, object);
 *              订阅: subscription = RxBus.getInstance().subscribe();
 *     version: 1.0
 * </pre>
 */
/**
 *
 */
internal class RxBus private constructor() {
    private val mBus: FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    /**
     * 发送消息
     *
     * @param msgId  消息id
     * @param object 内容
     */
    fun post(msgId: Int, `object`: Any?) = takeIf { hasSubscribers() }?.run { post(BusEvent(msgId, `object`)) }

    /**
     * 发送消息
     *
     * @param msgId 消息id
     */
    fun post(msgId: Int) = takeIf { hasSubscribers() }?.run { mBus.onNext(BusEvent(msgId, null)) }

    private fun post(`object`: Any) = takeIf { hasSubscribers() }?.run { mBus.onNext(`object`) }

    private fun hasSubscribers(): Boolean {
        return mBus.hasSubscribers()
    }

    /**
     * 订阅消息
     */
    fun subscribe(onNext: Consumer<in BusEvent>): Disposable {
        return toFlowable(BusEvent::class.java)
            .onBackpressureDrop()
            .onTerminateDetach()
            .subscribe(onNext) { throwable ->
                LogUtil.e("subscribe error", throwable.toString())
                throwable.printStackTrace()
                subscribe(onNext)
            }
    }

    private fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        return mBus.ofType(tClass)
    }

    fun toFlowable(): Flowable<Any> {
        return mBus
    }

    companion object {
        val instance: RxBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RxBus()
        }
    }
}
