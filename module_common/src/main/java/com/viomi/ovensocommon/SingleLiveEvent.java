package com.viomi.ovensocommon;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @data:2021/12/15
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private static final String TAG = SingleLiveEvent.class.getName();
    private final AtomicBoolean mPending = new AtomicBoolean(false);

    public SingleLiveEvent() {
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        if (this.hasActiveObservers()) {
            Log.i(TAG, "Multiple observers registered but only one will be notified of changes.");
        }

        super.observe(owner, new Observer<T>() {
            public void onChanged(T t) {
                if (SingleLiveEvent.this.mPending.compareAndSet(true, false)) {
                    observer.onChanged(t);
                }

            }
        });
    }

    @MainThread
    public void setValue(T value) {
        this.mPending.set(true);
        super.setValue(value);
    }

    @MainThread
    public void call() {
        this.setValue((T) null);
    }
}
