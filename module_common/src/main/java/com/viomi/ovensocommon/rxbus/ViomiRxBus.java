package com.viomi.ovensocommon.rxbus;

import android.util.Log;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 发送:
 * OvenRxBus.getInstance().post(ViomiRxBusEvent.MSG_XXX, object);
 * 订阅:
 * subscription = OvenRxBus.getInstance().subscribe();
 */
public class ViomiRxBus {
    private static final String TAG = "ViomiRxBus";
    //
    private static volatile ViomiRxBus mInstance;
    private final FlowableProcessor<Object> mBus;

    private ViomiRxBus() {
        // toSerialized method made bus thread safe
        mBus = PublishProcessor.create().toSerialized();
    }

    /**
     * 单例模式
     *
     * @return RxBus
     */
    public static ViomiRxBus getInstance() {
        if (mInstance == null) {
            synchronized (ViomiRxBus.class) {
                if (mInstance == null) {
                    mInstance = new ViomiRxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送消息
     *
     * @param msgId  消息id
     * @param object 内容
     */
    public void post(int msgId, Object object) {
//        Log.d(TAG, "post msgId:" + msgId);
        if (hasSubscribers()) {
            post(new ViomiRxBusEvent(msgId, object));
        }
    }

    /**
     * 发送消息
     *
     * @param msgId 消息id
     */
    public void post(int msgId) {
        Log.d(TAG, "post msgId:" + msgId);
        if (hasSubscribers()) {
            mBus.onNext(new ViomiRxBusEvent(msgId, null));
        }
    }

    private void post(Object object) {
        if (hasSubscribers()) {
            mBus.onNext(object);
        }
    }

    private boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    /**
     * 订阅消息
     *
     * @param onNext the on next
     * @return subscription
     */
    public final Disposable subscribe(final Consumer<? super ViomiRxBusEvent> onNext) {
        return toFlowable(ViomiRxBusEvent.class)
                .onBackpressureDrop()
                .onTerminateDetach()
                .subscribe(onNext, throwable -> {
                    // 发生异常会取消订阅
                    Log.e("subscribe error", throwable.toString());
                    throwable.printStackTrace();
                    subscribe(onNext);
                });
    }

    public final Disposable subscribeUi(final Consumer<? super ViomiRxBusEvent> onNext) {
        return toFlowable(ViomiRxBusEvent.class)
                .onBackpressureDrop()
                .onTerminateDetach()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, throwable -> {
                    // 发生异常会取消订阅
                    Log.e("subscribe error", throwable.toString());
                    throwable.printStackTrace();
                    subscribeUi(onNext);
                });
    }

    private <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public final Flowable<ViomiRxBusEvent> toFlowable() {
        return toFlowable(ViomiRxBusEvent.class)
                .onBackpressureDrop()
                .onTerminateDetach()
                //ui线程
                .compose(com.viomi.ovensocommon.rxbus.RxSchedulerUtil.SchedulersFlowableTransformer1());
    }

    // 使用VRout 进行的跳转
    public Disposable register(String moduleName, String key, Consumer onNext, Consumer onError) {
//        mBus.toObservable(moduleName, key).subscribeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
        return null;
    }
}
