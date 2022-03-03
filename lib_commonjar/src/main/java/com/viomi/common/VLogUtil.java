package com.viomi.common;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.viomi.vlog.Vlog;

import java.io.File;

public class VLogUtil {
    private static final String TAG = "VLogUtil";
    private static final String GLOBAL_TAG = "ScreenViomi";
    private static final boolean showVlog = true;

    public static void init(Context context) {
        Vlog.init(context, GLOBAL_TAG);
        Vlog.setShowLog(showVlog);
        Vlog.setLevel(Vlog.V);
    }

    /**
     * 串口连接成功，根据model 设置pid
     *
     * @param pid
     */
    public static void setPid(int pid) {
        Vlog.setPid(String.valueOf(pid));
    }

    /**
     * 云米登录成功之后 设置用户id
     *
     * @param userId
     */
    public static void setUserId(String userId) {
        Vlog.setUnionId(userId);
    }

    public static void setDeviceId() {
        String deviceId = ViomiProvideUtil.getDeviceId();
        Vlog.setDeviceId(deviceId);
    }
    
    public static void cleanLogFile() {
        String dirPath = Utils.getApp().getExternalCacheDir().getParent() + "/files";
        File deleteDir = new File(dirPath);
        Log.i(TAG, "cleanLogFile: dirPath: " + dirPath);
        boolean deleteResult = FileUtils.delete(deleteDir);
        Log.i(TAG, "cleanLogFile: deleteResult: " + deleteResult);
    }
}
