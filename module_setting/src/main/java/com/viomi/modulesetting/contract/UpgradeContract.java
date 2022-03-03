package com.viomi.modulesetting.contract;

import com.viomi.modulesetting.presenter.BasePresenter;
import com.viomi.upgrade_lib.entity.CheckVersionResult;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 版本升级Contract
 */
public interface UpgradeContract {

    interface View {
        void updateVersion(CheckVersionResult result);

        void refreshProgress(int progress);

        void install(String path);
    }

    interface Presenter extends BasePresenter<View> {
        void checkAppUpgrade();

        void downloadApp(CheckVersionResult result);

        void downloadAppFull(String downloadUrl);
    }

}
