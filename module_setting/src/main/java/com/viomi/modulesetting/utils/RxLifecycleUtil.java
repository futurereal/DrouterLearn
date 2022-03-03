package com.viomi.modulesetting.utils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * <b>Project:</b> viomi_washer <br>
 * <b>Package:</b> com.viomi.modulesetting.utils <br>
 * <b>Create Date:</b> 2020/11/10 <br>
 * <b>@author:</b> qingyong <br>
 * <b>Address:</b> qingyong@viomi.com <br>
 * <b>Description:</b> 配合uber的AutoDispose封装一下 <br>
 */
public class RxLifecycleUtil {

    /**
     * Rx绑定生命周期，在onDestory
     *
     * @param lifecycleOwner 观察对象，Activity或者Fragment
     */
    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner, Lifecycle.Event.ON_DESTROY));
    }

}
