package com.viomi.modulesetting.presenter;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.presente
 * @ClassName: BasePresenter
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/17 5:35 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/17 5:35 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface BasePresenter<T> {

    /**
     * 订阅
     */
    void subscribe(T view);

    /**
     * 取消订阅
     */
    void unSubscribe();

}
