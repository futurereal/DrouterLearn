package com.viomi.modulesetting.config;

import com.viomi.modulesetting.entity.QRCodeBase;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.config
 * @ClassName: OnLoginCallback
 * @Description: 登录与退出登录结果回调
 * @Author: randysu
 * @CreateDate: 2020/3/26 2:05 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 2:05 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface OnLogInOutCallback {

    void loginResult(boolean isMi,QRCodeBase qrCodeBase);

    void logoutResult(boolean isMi);

    void logoutException(boolean isMi, Throwable throwable);
}
