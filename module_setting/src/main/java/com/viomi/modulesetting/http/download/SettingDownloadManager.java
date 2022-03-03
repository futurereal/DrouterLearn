package com.viomi.modulesetting.http.download;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @ProjectName: CommonSettingPage
 * @Package: com.viomi.settingpagelib.http.download
 * @ClassName: SettingDownloadManager
 * @Description:
 * @Author: randysu
 * @CreateDate: 2020/3/18 5:53 PM
 * @UpdateUser:
 * @UpdateDate: 2020/3/18 5:53 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class SettingDownloadManager extends AbsFileDownloadManager {

    private static SettingDownloadManager instance;

    public static SettingDownloadManager getInstance() {
        if (instance == null) {
            synchronized (SettingDownloadManager.class) {
                if (instance == null) {
                    instance = new SettingDownloadManager();
                }
            }
        }

        return instance;
    }

    @Override
    public void setCallback(DownloadFeedbackCallback callback) {
        superCallback = callback;
    }

}
