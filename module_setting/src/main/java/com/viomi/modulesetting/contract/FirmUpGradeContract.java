package com.viomi.modulesetting.contract;

import com.viomi.modulesetting.entity.FirmUpdateResult;
import com.viomi.modulesetting.presenter.BasePresenter;

import java.util.List;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 固件生几个
 */
public interface FirmUpGradeContract {

    interface View {
        void updateVersionTip(List<FirmUpdateResult> result);

        void refreshProgress(int progress);

        void updateSucess();

        void updateFail();

    }

    interface Presenter extends BasePresenter<View> {
        void checkFirmVersion();

        void downloadFirmFile(String url);
    }

}
