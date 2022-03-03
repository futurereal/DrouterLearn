package com.viomi.modulesetting.repository;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.entity.QRCodeBase;
import com.viomi.modulesetting.entity.QRCodeLoginCodeBase;
import com.viomi.modulesetting.entity.QRCodeLoginResult;
import com.viomi.modulesetting.entity.QRCodeResult;
import com.viomi.modulesetting.entity.UserInfo;
import com.viomi.modulesetting.http.RetrofitServiceManager;
import com.viomi.modulesetting.http.service.viomi.SettingApiService;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Copyright (C), 2014-2020, 佛山云米科技有限公司
 *
 * @Description: 请求服务器 1 、 获取 二维码url 的地址  2 、检查登录状态
 */
public class LoginRepository {
    private static final String TAG = "LoginRepository";
    // iot 平台的 类型
    public static final String TYPE_VIOT = "1";
    private static volatile LoginRepository mInstance;
    private final SettingApiService settingApiService;

    public static LoginRepository getInstance() {
        if (mInstance == null) {
            synchronized (LoginRepository.class) {
                if (mInstance == null) {
                    mInstance = new LoginRepository();
                }
            }
        }
        return mInstance;
    }

    private LoginRepository() {
        settingApiService = RetrofitServiceManager.getInstance().create(SettingApiService.class);
    }

    /**
     * 生成云米二维码
     */
    public Observable<QRCodeLoginCodeBase> createQRCode(String clientId) {
        return settingApiService.createLoginQRCode(TYPE_VIOT, clientId);
    }

    /**
     * 检查登录状态
     */
    public Flowable<QRCodeBase> getLoginStatus(String clientId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientID", clientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "login check status req params:" + jsonObject);
        return settingApiService.getLoginUserInfo(RetrofitServiceManager.getInstance().getRequestBody(jsonObject));
    }

    public UserInfoDb convertQrcodeToUserInfoDb(QRCodeBase qrCodeBase) {
        UserInfoDb userInfoDb = new UserInfoDb();
        QRCodeResult loginResult = qrCodeBase.getLoginQRCode().getResult();
        userInfoDb.setToken(loginResult.getToken());
        //userID 暂时没有用
        QRCodeLoginResult qrCodeLoginResult = loginResult.getLoginResult();
        userInfoDb.setUserId(qrCodeLoginResult.getUserId());
        UserInfo userInfo = loginResult.getUserInfo();
        userInfoDb.setNickname(userInfo.getNickname());
        userInfoDb.setHeadImgUrl(userInfo.getHeadImg());
        userInfoDb.setAccount(userInfo.getAccount());
        userInfoDb.setMobile(userInfo.getMobile());
        userInfoDb.setMiUserId(userInfo.getMiUserInfo().getUserId());
        userInfoDb.setMiId(userInfo.getMiUserInfo().getMiId());
        // 之前米家的绑定状态
        userInfoDb.setBindMiot(ModuleSettingApplicaiton.getUserInfoDb().isBindMiot());
        return userInfoDb;
    }

    /**
     * 保存用户登录信息
     *
     * @param userInfoDb
     * @return
     */
    public void saveUserInfoToDb(UserInfoDb userInfoDb) {
        // 把账号信息保存到文件
        Log.i(TAG, "saveUserInfo: userInfoDb: " + userInfoDb.toString());
        long insertResult = ViomiRoomDatabase.getDatabase().userInfoDao().insert(userInfoDb);
        Log.i(TAG, "saveUserInfoToDb: insertResult : " + insertResult);
    }

    /**
     * 生成米家绑定二维码
     *
     * @param content：二维码信息
     * @param width：二维码宽度
     * @param height：二维码高度
     */
    public Bitmap createMiQRCodeBimap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix encode = null;
        try {
            encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (encode == null) {
            return null;
        }
        int[] pixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (encode.get(j, i)) {
                    pixels[i * width + j] = 0x00000000;
                } else {
                    pixels[i * width + j] = 0xffffffff;
                }
            }
        }
        return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
    }
}
