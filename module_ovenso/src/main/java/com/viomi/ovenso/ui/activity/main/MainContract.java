package com.viomi.ovenso.ui.activity.main;


import com.viomi.ovensocommon.BasePresenter;
import com.viomi.ovensocommon.BaseView;

/**
 * Created by Ljh on 2020/11/16.
 * Description:
 */
public interface MainContract {
    interface View extends BaseView {//UI接口

        void refreshLight(boolean on);

        void refreshPannel(boolean on);

        void refreshNetState(boolean isWifiConnect);

        void refreshTheme();

        void updatePropertyView();
    }

    abstract class Presenter<V extends BaseView> extends BasePresenter<View> {//方法接口

        public abstract void cmdLight(boolean on);

        public abstract void cmdPannel(boolean on);
    }
}

