package com.viomi.modulesetting.contract;

import com.viomi.modulesetting.presenter.BasePresenter;
import com.viomi.ovensocommon.db.UserInfoDb;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.contract
 * @ClassName: SettingContract
 * @Description: 设置
 * @Author: randysu
 * @CreateDate: 2020/3/26 3:43 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 3:43 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface SettingContract {

    interface View {
        void showUserInfo(UserInfoDb userInfoDb);
    }

    interface Presenter extends BasePresenter<View> {
        void loadUserInfo();
    }

}
