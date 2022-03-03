package com.viomi.modulesetting.contract;

import android.graphics.Bitmap;

import com.viomi.ovensocommon.BasePresenter;
import com.viomi.ovensocommon.BaseView;
import com.viomi.ovensocommon.db.UserInfoDb;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: ovenSo
 * @Package: com.viomi.settingpagelib.contract
 * @ClassName: LoginQRCodeContract
 * @Description: 扫码登录
 * @Author: randysu
 * @CreateDate: 2020/4/2 10:04 AM
 * @UpdateUser:
 * @UpdateDate: 2020/4/2 10:04 AM
 * @UpdateRemark:
 * @Version: 1.0
 */
public interface LoginContract {

    interface View extends BaseView {
        void showLoadingQRCode();

        void showViomiQRCode(String qrCodeUrl);

        void showMiQRCode(Bitmap bitmap);

        void showRetry();

        void showLoading();

        void loginViomiFail(String message);

        void showRebindMiotFragment(UserInfoDb userInfoDb);

        void dismissSelf();
    }

    abstract class Presenter<T extends BaseView> extends BasePresenter<View> {
        public abstract void loadQrCodeBitmap(boolean isMiotLogin);
    }

}
