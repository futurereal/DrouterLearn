package com.viomi.modulesetting.utils.softap;

import java.util.List;

/**
 * @author bailing
 * date 2020/9/9
 * description：
 */
public interface SoftApContract {

    interface SoftApViewI {
        /**
         * 获取热点开关状态回调
         *
         * @param isOpen 开关
         */
        void initSoftAp(boolean isOpen);

        /**
         * 设置当前热点频段
         *
         * @param hertz
         */
        void setCurrentHertz(int hertz);

        /**
         * 更新连接设备列表
         *
         * @param list 数据源
         */
        void notifyItemChanged(List<HotSpotDevice> list);
    }

    interface HotSpotPresenterI {
        /**
         * 设置热点开关状态
         *
         * @param isOpen 开关
         */
        void setOpen(boolean isOpen);

        /**
         * 销毁
         */
        void onDestroy();

        /**
         * 刷新已连接的设备列表
         */
        void notifyDeviceList();

    }


}
