package com.viomi.modulesetting.contract;

import com.viomi.ovensocommon.BasePresenter;
import com.viomi.ovensocommon.BaseView;
import com.viomi.ovensocommon.db.UserInfoDb;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.contract
 * @ClassName: AccountContract
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/26 11:52 AM
 * @UpdateUser:
 * @UpdateDate: 2020/3/26 11:52 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface AccountContract {

    interface View extends BaseView {
        void refreshUserInfo(UserInfoDb qrCodeBase);

        void refreshMiBindStatus(boolean isBind);
    }

    abstract class Presenter<V extends BaseView> extends BasePresenter<View> {
        public abstract void loadUserInfoAndUpdateView(UserInfoDb userInfoDb);
    }

}
