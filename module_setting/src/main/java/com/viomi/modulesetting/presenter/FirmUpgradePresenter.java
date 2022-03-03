package com.viomi.modulesetting.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viomi.common.ApplicationUtils;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.contract.FirmUpGradeContract;
import com.viomi.modulesetting.entity.FirmUpdateResult;
import com.viomi.modulesetting.http.RetrofitServiceManager;
import com.viomi.modulesetting.http.download.DownloadFeedbackCallback;
import com.viomi.modulesetting.http.download.SettingDownloadManager;
import com.viomi.modulesetting.http.service.viomi.SettingApiService;
import com.viomi.modulesetting.utils.ViomiHeaderSign;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.rxbus.RxSchedulerUtil;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 固件升级
 */
public class FirmUpgradePresenter implements FirmUpGradeContract.Presenter {
    private static final String TAG = "FirmUpgradePresenter";
    private static final String HEADER_KEY_DID = "did";
    private static final String HEADER_KEY_PID = "pid";
    private static final String HEADER_VALUE_LEFT = "[";
    private static final String HEADER_VALUE_RIGHT = "]";
    private CompositeDisposable mCompositeDisposable;
    private final Context mContext;
    private final SettingDownloadManager downloadManager;

    @Nullable
    private FirmUpGradeContract.View mView;

    public FirmUpgradePresenter() {
        mContext = ApplicationUtils.getContext();
        downloadManager = SettingDownloadManager.getInstance();
        downloadManager.setCallback(new FirmDownloadFeedbackCallback());
    }

    @Override
    public void subscribe(FirmUpGradeContract.View view) {
        Log.i(TAG, "subscribe: ");
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void checkFirmVersion() {
        Log.i(TAG, "checkFirmVersion: ");
        long deviceId = Long.parseLong(ViomiProvideUtil.getDeviceId());
        long pid = ModelUtil.getViomiPid();
        HashMap<String, String> headerMap = new HashMap<>(2);
        headerMap.put(HEADER_KEY_DID, HEADER_VALUE_LEFT + deviceId + HEADER_VALUE_RIGHT);
        headerMap.put(HEADER_KEY_PID, HEADER_VALUE_LEFT + pid + HEADER_VALUE_RIGHT);
        SettingApiService settingApiService = RetrofitServiceManager.getInstance().create(SettingApiService.class);
        Disposable checkDisposable = settingApiService.checkFirmUpdate(ViomiHeaderSign.getHeaders(headerMap, mContext), deviceId, pid)
                .compose(RxSchedulerUtil.SchedulersTransformer1())
                .onTerminateDetach()
                .subscribe(listMobBaseRes -> {
                    String result = ViomiHeaderSign.decrypt(listMobBaseRes.getResult(), mContext);
                    Log.e(TAG, result);
                    Gson gson = new Gson();
                    List<FirmUpdateResult> results = gson.fromJson(result, new TypeToken<List<FirmUpdateResult>>() {
                    }.getType());
                    Log.i(TAG, "accept: results size: " + results.size());
                    if (mView != null) {
                        mView.updateVersionTip(results);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    Log.e(TAG, throwable.getMessage());
                });
        mCompositeDisposable.add(checkDisposable);
    }

    public void writeFileToMcu(String path) {
        Log.i(TAG, "writeFileToMcu: " + path);
        SerialControl.writeFileToMcu(path, (isProcessing, progress, desc) -> new Handler(Looper.getMainLooper()).post(() -> {
            Log.i(TAG, "writeFileToMcu :" + "isProcessing" + isProcessing + "progress : " + progress + " desc : " + desc);
            if (mView == null) {
                Log.i(TAG, "writeFileToMcu: mView is null return ");
                return;
            }
            int currentProgress = progress >= 0 ? (int) (50 + progress * 1f / 2) : 0;
            Log.i(TAG, "writeFileToMcu: currentProgress: " + currentProgress);
            //升级失败
            if (progress <= 0) {
                ViomiToastUtil.showToastCenter("升级失败");
                mView.updateFail();
                Log.i(TAG, "writeFileToMcu: updateFail return ");
                return;
            }
            // 升级成功
            if (!isProcessing && progress == 100) {
                Log.i(TAG, "writeFileToMcu: update Success  return ");
                mView.updateSucess();
                return;
            }
            // 更新进度
            mView.refreshProgress(currentProgress);
        }));
    }

    @Override
    public void downloadFirmFile(String url) {
        Log.i(TAG, "downloadFirmFile: url : " + url);
        File externalCatchDir = mContext.getExternalCacheDir();
        File dirFirmFile = new File(externalCatchDir, ModuleSettingConstants.PATH);
        Log.i(TAG, "downloadFirmFile: dirFile path :" + dirFirmFile.getAbsolutePath());
        if (!dirFirmFile.exists()) {
            boolean makeResult = dirFirmFile.mkdirs();
            Log.i(TAG, "downloadFirmFile: makeResult: " + makeResult);
        }
        File[] files = dirFirmFile.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        String fileName = getFileName(url);
        Log.i(TAG, "downloadFirmFile: fileName: " + fileName);
        File saveFile = new File(dirFirmFile, fileName);
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            Log.i(TAG, "downloadFirmFile: Exception : " + e.getMessage());
        }
        downloadManager.startToDownloadFile(url, saveFile.getAbsolutePath(), saveFile);
    }

    /**
     * 根据链接获取 Apk 名称
     */
    private String getFileName(String path) {
        // http://viot-fileupload-1257947852.file.myqcloud.com/images/iot/10000/y0UgOAr26sYsXMapLyL.hex?sign=4153690082-bf41d697f480459db4e7c4048c679238-0-90400713bb53c89a6afd07203ef6c9b6
        Log.i(TAG, "getFileName: path : " + path);
        if (TextUtils.isEmpty(path)) {
            return "test";
        }
        int separatorIndex = path.lastIndexOf("/");
        int endIndex = path.lastIndexOf("?");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, endIndex);
    }

    @Override
    public void unSubscribe() {
        mView = null;
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    class FirmDownloadFeedbackCallback implements DownloadFeedbackCallback {
        @Override
        public void onStart(int downloadId, long totalBytes) {
            Log.i(TAG, "onStart");
        }

        @Override
        public void onRetry(int downloadId) {
            Log.i(TAG, "onRetry  downloadId: " + downloadId);
        }

        @Override
        public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
            Log.i(TAG, "bytesWritten: " + bytesWritten + "   totalBytes: " + totalBytes);
            if (mView != null) {
                mView.refreshProgress((int) (bytesWritten * 50.0f / totalBytes));
            }
        }

        @Override
        public void onSuccess(int downloadId, String filePath) {
            if (mView == null) {
                return;
            }
            if (TextUtils.isEmpty(filePath)) {
                mView.updateFail();
            } else {
                writeFileToMcu(filePath);
            }
        }

        @Override
        public void onFailure(int downloadId, int statusCode, String errMsg) {
            Log.i(TAG, "onFailure: errMsg ： " + errMsg);
            if (mView != null) {
                mView.updateFail();
            }
        }
    }
}
