package com.viomi.modulesetting.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.viomi.common.ApplicationUtils;
import com.viomi.common.VLogUtil;
import com.viomi.common.ViomiProvideUtil;
import com.viomi.modulesetting.ModuleSetingEventConstant;
import com.viomi.modulesetting.ModuleSettingApplicaiton;
import com.viomi.modulesetting.ModuleSettingConstants;
import com.viomi.modulesetting.entity.VMAccountEntity;
import com.viomi.modulesetting.http.RetrofitServiceManager;
import com.viomi.modulesetting.http.service.viomi.SettingApiService;
import com.viomi.modulesetting.repository.LoginRepository;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.db.UserInfoDb;
import com.viomi.ovensocommon.db.ViomiRoomDatabase;
import com.viomi.ovensocommon.rxbus.RxSchedulerUtil;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.viot.VIotHostManager;
import com.viomi.viot.account.AccountMessage;
import com.viomi.viot.action.VIotDeviceAction;
import com.viomi.viot.config.VIotDeviceConfig;
import com.viomi.viot.listener.OnAccountRefreshListener;
import com.viomi.viot.listener.OnActionListener;
import com.viomi.viot.listener.OnDeviceBindListener;
import com.viomi.viot.listener.OnGetPropertiesListener;
import com.viomi.viot.listener.OnRemoteDebugListener;
import com.viomi.viot.listener.OnSetPropertiesListener;
import com.viomi.viot.listener.OnSetPropertyListListener;
import com.viomi.viot.property.VIotDeviceProperty;
import com.viomi.viot.utils.encrypt.AESUtil;
import com.viomi.vlog.Vlog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Created by Ljh on 2021/3/12.
 * Description:VIOT ???????????????
 */
public class ViotDeviceManager {
    public static final String TAG = "DeviceManagerViot";
    private static final ViotDeviceManager deviceManager = new ViotDeviceManager();
    private static final boolean NEED_VERIFIY = false;
    //?????????????????? ???????????? ??????????????????
    public static final int ACCOUNT_RELATION_BIND_IOT = 1;
    public static final int ACCOUNT_RELATION_UNBIND_MIOT = 2;

    public static final String MANUFACTURE_NAME = "viomi";
    private static final int PID_ANDROID = 1;
    private final int viotPid;
    private String miotDeviceId;
    private final String viotDeviceId;
    private final String deviceAccessKey;
    private final String cloudPublickey;
    private final String viotModelName;
    private final LoginDeviceBindListener loginDeviceBindListener;
    private final Context mContext;

    private ViotDeviceManager() {
        mContext = Utils.getApp();
        viotPid = ModelUtil.getViomiPid();
        viotDeviceId = ViomiProvideUtil.getDeviceId();
        viotModelName = ModelUtil.getModelName();
        deviceAccessKey = ViomiProvideUtil.getAccessKey();
        cloudPublickey = ViomiProvideUtil.getCloudPublicKey();
        // ??????????????????????????????????????? ???????????????id
        if (NEED_VERIFIY) {
            miotDeviceId = ViomiProvideUtil.getMiotDeviceId();
        }
        loginDeviceBindListener = new LoginDeviceBindListener();
    }

    public static ViotDeviceManager getInstance() {
        return deviceManager;
    }

    /**
     * ??????????????????
     */
    public void bindViotServcie(UserInfoDb userInfoDb) {
        Log.i(TAG, "bindViotServcie...");
        String token = null;
        String userId = null;
        if (userInfoDb != null) {
            token = userInfoDb.getToken();
            userId = userInfoDb.getUserId();
        }
        //???????????????????????????
//        VIotHostManager.Companion.getInstance().stopDevice(mContext);
        try {
            VIotDeviceConfig deviceConfig = new VIotDeviceConfig(viotPid, token, userId, viotDeviceId,
                    deviceAccessKey, cloudPublickey, MANUFACTURE_NAME, viotModelName, miotDeviceId, NEED_VERIFIY);
            Log.i(TAG, "bindViotServcie VIotDeviceConfig :" + deviceConfig);
            //MQTT ??????
            VIotHostManager.Companion.getInstance().enableLog(CommonConstant.VIOT_LOG_OPEN);
            VIotHostManager.Companion.getInstance().setDebugEnvironment(ModuleSettingConstants.IS_DEBUG_ENV);
            Log.i(TAG, "bindViotServcie startBindDevice :");
            loginDeviceBindListener.setUserinfo(userInfoDb);
            VIotHostManager.Companion.getInstance().startDevice(mContext, deviceConfig, loginDeviceBindListener);
        } catch (Exception exception) {
            Log.i(TAG, "bindViotServcie: Exception: " + exception.getMessage());
        }
    }

    //?????????????????????????????????
    public void resetDevice() {
        Log.d(TAG, "resetDevice");
        VIotHostManager.Companion.getInstance().resetDevice();
    }

    class LoginDeviceBindListener implements OnDeviceBindListener {
        private final Context context;
        private UserInfoDb userInfo;
        private boolean isFechUserInfo;
        private boolean beforeMiotBind;

        public LoginDeviceBindListener() {
            this.context = ApplicationUtils.getContext();
        }

        public void setIsFetchUserInfo(boolean isFetchUserInfo) {
            this.isFechUserInfo = isFetchUserInfo;
        }


        public void setUserinfo(UserInfoDb userInfo) {
            Log.i(TAG, "setUserinfoDb: " + userInfo);
            this.userInfo = userInfo;
        }

        /**
         * ????????????????????????
         *
         * @param vIotDeviceConfig ???????????????
         */
        @Override
        public void onSucceed(@NotNull VIotDeviceConfig vIotDeviceConfig) {
            Log.i(TAG, "LoginDeviceBindListener startDevice onSucceed:" + vIotDeviceConfig);
            // ????????????????????????????????????
            registerRefreshAccount(userInfo);
        }

        /**
         * ????????????????????????
         *
         * @param code
         * @param msg
         */
        @Override
        public void onFailed(int code, String msg) {
            Log.i(TAG, "LoginDeviceBindListener bindViotService onFailed:" + code + "   " + msg);
            VIotHostManager.Companion.getInstance().stopDevice(context);
            ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_BIND_VIOMI_DEVICE_FAIL);
        }

        /**
         * ??????????????????????????????
         */
        @Override
        public void onBind() {
            String viotUserId = userInfo.getUserId();
            String miotUserId = userInfo.getMiUserId();
            Log.i(TAG, "onBind: viotUserId: " + viotUserId + " miotUserId: " + miotUserId);
            // ???????????????
            if (TextUtils.isEmpty(viotUserId) || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_UNBIND) || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_NO_BIND)) {
                Log.i(TAG, "onBind: viotUserId  is null or -1  return ");
                return;
            }
            // ????????????????????? ??? ??????????????????userInfoDb ?????????
            Log.i(TAG, "onBind: " + userInfo.isFromQrcode());
            if (userInfo.isFromQrcode()) {
                LoginRepository.getInstance().saveUserInfoToDb(userInfo);
                ModuleSettingApplicaiton.setUserInfoDb(userInfo);
            }
            Log.i(TAG, "onBind: isFechUserInfo: " + isFechUserInfo);
            if (isFechUserInfo) {
                fetchUserInfoAndSave(viotUserId, miotUserId);
            } else {
                ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_VIOMI_LOGIN, userInfo);
                OvensoServiceFactory.getInstance().getOvenService().ViotLoginStatusChange(true);
                WaterServiceFactory.getInstance().getWaterService().ViotLoginStatusChange(true);
            }
            // ??????Viot ?????????????????????miot ?????????????????????
            if (userInfo.isBindMiot() || !TextUtils.equals(miotUserId, ModuleSettingConstants.VIOT_UNBIND)) {
                // ???????????????????????????????????????
                MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
            }
            //Logan????????????
            Vlog.setUnionId(userInfo.getUserId());
            //??????????????????????????????????????????????????????
            registerLogPullListener();
            //????????????????????????  ???  ?????????????????????????????????????????????
            registerPropertyChangeListener();
            SerialControl.getMucPropertiesAndReport();
            // ??????????????????
//            ModuleSettingServiceFactory.getInstance().getViotService().getSettingListener().onBindStatus(true);
        }

        /**
         * ????????? ??? ??????????????????
         */
        @Override
        public void onUnBind() {
            Log.i(TAG, " LoginDeviceBindListener  onUnBind...");
            isSubScribe = true;
            unbindMiAndClearInfo();
//            ModuleSettingServiceFactory.getInstance().getViotService().getSettingListener().onBindStatus(true);
        }


        /**
         * ???????????? ?????????????????????
         */
        private void registerLogPullListener() {
            VIotHostManager.Companion.getInstance().registerRemoteDebugCallback(new OnRemoteDebugListener() {
                @Override
                public void remoteMessage(String messageContent) {
                    //????????????log????????????????????????????????????pid??????????????????????????????????????????1???iOS????????????2
                    Log.i(TAG, "remoteMessage: " + messageContent);
                    VLogUtil.setPid(PID_ANDROID);
                    //??????id??????????????????did???????????????????????????????????????????????????????????????????????????
                    Vlog.setDeviceId(ViomiProvideUtil.getDeviceId());
                    //?????????????????????????????????????????????????????????????????????????????????????????????
                    //Vlog.commandHandle(message, false)???????????????????????????
                    Vlog.commandHandle(messageContent);
                }
            });
        }

        /**
         * ????????????????????? ???????????? ??????
         */
        private void registerPropertyChangeListener() {
            Log.i(TAG, "registerPropertyChangeListener: ");
            //?????????????????????????????????
            VIotHostManager.Companion.getInstance().registerSetPropertiesCallback(new OnSetPropertiesListener() {
                @Override
                public void onSetProperty(@androidx.annotation.Nullable VIotDeviceProperty property) {
                    // ????????????????????????
                    // ??????????????????MCU??????
                    // ????????????
                    // ?????????????????????  1 ???????????????   2  ?????????Action
                    Log.i(TAG, "registerPropertyChangeListener ??? onSetProperty: " + property);
                    PropertyEntity propertyEntity = new PropertyEntity(property.getSiid(), property.getPiid(), property.getValue());
                    // ??????viot ??????????????????
                    OvensoServiceFactory.getInstance().getOvenService().dealPropertyFromPlug(propertyEntity);
                    // ?????????ERO ??????????????????
                    WaterServiceFactory.getInstance().getWaterService().dealPropertyFromPlug(propertyEntity);
                }

                @Override
                public void onFailed(int code, @androidx.annotation.Nullable String message) {
                    Log.i(TAG, "registerPropertyChangeListener  onFailed:  code: " + code + " message: " + message);
                }
            });
            //?????????????????????????????????
            VIotHostManager.Companion.getInstance().registerSetPropertyListCallback(new OnSetPropertyListListener() {
                @Override
                public void onSetProperty(@org.jetbrains.annotations.Nullable List<VIotDeviceProperty> list) {
                    Log.d(TAG, "registerPropertyChangeListener   success" + list.size());
                }

                @Override
                public void onFailed(int i, @org.jetbrains.annotations.Nullable String s) {
                    Log.d(TAG, "registerPropertyChangeListener   fail: " + i);
                }
            });
            // ??????????????????????????? viot ?????????????????????viot????????????????????????
            VIotHostManager.Companion.getInstance().registerGetPropertiesCallback(new OnGetPropertiesListener() {
                @androidx.annotation.Nullable
                @Override
                public List<VIotDeviceProperty> onGetProperty(@androidx.annotation.Nullable List<VIotDeviceProperty> list) {
                    Log.i(TAG, "onGetProperty: " + list.size());
                    // ???????????????????????????????????????????????????????????????????????????????????????sp ?????????????????????
                    // ????????????????????????????????????????????? ???????????????????????? ????????????????????????
                    for (VIotDeviceProperty vIotDeviceProperty : list) {
                        Object objectValue = getObjectValue(vIotDeviceProperty);
                        Log.i(TAG, "onGetProperty: objectValue: " + objectValue);
                        vIotDeviceProperty.setValue(objectValue);
                    }
                    return list;
                }

                @Override
                public void onFailed(int i, @androidx.annotation.Nullable String s) {
                    Log.i(TAG, "onGetProperty: fail :  " + s);
                }
            });
            //?????????????????????????????????????????????????????? ????????????????????? ??????????????????????????????
            VIotHostManager.Companion.getInstance().registerActionCallback(new OnActionListener() {
                @Override
                public void onAction(VIotDeviceAction vIotDeviceAction) {
                    int siid = vIotDeviceAction.getSiid();
                    int aiid = vIotDeviceAction.getAiid();
                    Log.i(TAG, "onAction: " + siid + " " + aiid);
                    // ???????????????????????????
                    if (!SerialControl.checkMcuAviable()) {
                        return;
                    }
                    ArrayList<VIotDeviceProperty> vIotDeviceProperties = vIotDeviceAction.getInList();
                    Log.i(TAG, "onAction: vIotDeviceProperties : " + vIotDeviceProperties);
                    List<PropertyEntity> propertyEntities = new ArrayList<>();
                    if (vIotDeviceProperties != null && vIotDeviceProperties.size() > 0) {
                        for (VIotDeviceProperty vIotDeviceProperty : vIotDeviceProperties) {
                            PropertyEntity propertyEntity = new PropertyEntity(siid, vIotDeviceProperty.getPiid(), vIotDeviceProperty.getValue());
                            propertyEntities.add(propertyEntity);
                        }
                    }
                    OvensoServiceFactory.getInstance().getOvenService().doActionFromPlug(siid, aiid, propertyEntities);
                    WaterServiceFactory.getInstance().getWaterService().doActionFromPlug(siid, aiid, propertyEntities);
                }

                @Override
                public void onFailed(int i, @androidx.annotation.Nullable String s) {
                    Log.i(TAG, "onFailed: ");
                }
            });
        }

        /**
         * @param vIotDeviceProperty
         * @return
         * @description: ???sp ?????????????????????????????????????????????????????????????????? ?????? boolean  ???????????????
         */
        private Object getObjectValue(VIotDeviceProperty vIotDeviceProperty) {
            Object objectValue = 0;
            try {
                objectValue = PropertyPreferenceManager.getInstance().getProperty(vIotDeviceProperty.getSiid(),
                        vIotDeviceProperty.getPiid(), 0);
            } catch (Exception exception) {
                Log.i(TAG, "onGetProperty: exception int");
                try {
                    objectValue = PropertyPreferenceManager.getInstance().getProperty(vIotDeviceProperty.getSiid(),
                            vIotDeviceProperty.getPiid(), "");
                } catch (Exception exception1) {
                    Log.i(TAG, "onGetProperty: exception String");
                    try {
                        objectValue = PropertyPreferenceManager.getInstance().getProperty(vIotDeviceProperty.getSiid(),
                                vIotDeviceProperty.getPiid(), false);
                    } catch (Exception exception2) {
                        Log.i(TAG, "getObjectValue: boolean ");
                    }

                }
            }
            Log.i(TAG, "getObjectValue: objectValue " + objectValue);
            return objectValue;
        }


        private void registerRefreshAccount(UserInfoDb userInfo) {
            Log.d(TAG, "registerRefreshAccount");
            VIotHostManager.Companion.getInstance().registerAccountRefreshCallback(new AccountRefreshListener(userInfo));
        }

        /**
         * 1 ????????????????????? ????????????????????????????????? ????????????ACCOUNT_RELATION_UNBIND_MIOT
         * 2 ??????app ??????????????????????????????????????????????????????????????? ACCOUNT_RELATION_BIND_VIOT
         * 3 ????????????  ??????????????????????????????????????????????????? ?????? ACCOUNT_RELATION_BIND_VIOT
         */
        class AccountRefreshListener implements OnAccountRefreshListener {
            private final UserInfoDb userinfoDb;

            public AccountRefreshListener(UserInfoDb userinfoDb) {
                this.userinfoDb = userinfoDb;
            }

            @SuppressLint("CheckResult")
            @Override
            public void onAccountRefresh(@Nullable AccountMessage accountMessage) {

                //viotAccountId=111246621, miAccountId=882225307, plateform=[1], handleType=1
                // (viotAccountId=111248684, miAccountId=60507270, plateform=[3], handleType=2
                // viotAccountId=111248684, miAccountId=60507270, plateform=[3], handleType=1
                Log.i(TAG, "onAccountRefresh:" + accountMessage.toString());
                int handleType = accountMessage.getHandleType();
                // ??????????????????????????????
                if (handleType == ACCOUNT_RELATION_UNBIND_MIOT) {
                    Log.w(TAG, "onAccountRefresh  unbindMiot");
                    String savedMiId = userinfoDb.getMiUserId();
                    Log.i(TAG, "onAccountRefresh: savedMiId: " + savedMiId);
                    userinfoDb.setMiUserId(ModuleSettingConstants.VIOT_UNBIND);
                    userinfoDb.setBindMiot(false);
                    ModuleSettingApplicaiton.setUserInfoDb(userinfoDb);
                    ViomiRoomDatabase.getDatabase().userInfoDao().insert(userinfoDb);
                    // ???????????????????????????????????????
                    MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
                    return;
                }
                //??????????????????
                if (handleType == ACCOUNT_RELATION_BIND_IOT) {
                    // ???????????????????????????????????????
                    String savedUserId = userinfoDb.getUserId();
                    String currentUserId = accountMessage.getViotAccountId();
                    Log.w(TAG, "onAccountRefresh  savedUserId: " + savedUserId + "  currentUserId : " + currentUserId);
                    if (!TextUtils.equals(savedUserId, currentUserId)) {
                        Log.d(TAG, " onAccountRefresh  bindViotStatus");
                        userinfoDb.setUserId(currentUserId);
                        userinfoDb.setMiUserId(accountMessage.getMiAccountId());
                        bindViotServcie(userinfoDb);
                        loginDeviceBindListener.setIsFetchUserInfo(true);
                        return;
                    }
                    // ???????????? ??? ?????????  ??? ?????????
                    String saveMiUserId = userinfoDb.getMiUserId();
                    String currentMiUserId = accountMessage.getMiAccountId();
                    Log.w(TAG, "onAccountRefresh  saveMiUserId: " + saveMiUserId + "  currentMiUserId : " + currentMiUserId);
                    if (!TextUtils.equals(saveMiUserId, currentMiUserId)) {
                        userinfoDb.setMiUserId(currentMiUserId);
                        ModuleSettingApplicaiton.setUserInfoDb(userinfoDb);
                        MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
                    }
                }
            }
        }

        /**
         * ?????????????????? ????????????????????????????????????
         *
         * @param saveUserId
         */
        public void fetchUserInfoAndSave(String saveUserId, String miotUserId) {
            Log.i(TAG, "fetchUserInfoAndSave: ");
            JSONObject jsonObject = new JSONObject();
            try {
                String data = AESUtil.INSTANCE.aesEncode(saveUserId + ":" + System.currentTimeMillis(),
                        deviceAccessKey);
                Log.i(TAG, "fetchUserInfoAndSave: data: " + data);
                jsonObject.put("encryptData", data);
            } catch (JSONException e) {
                Log.i(TAG, "fetchUserInfoAndSave: jsonException");
                e.printStackTrace();
            }
            RequestBody requestBody = RetrofitServiceManager.getInstance().getRequestBody(jsonObject);
            Log.i(TAG, "fetchUserInfoAndSave: reqeustBody: " + requestBody);
            SettingApiService settingApiService = RetrofitServiceManager.getInstance().create(SettingApiService.class);
            settingApiService.getViomiAccountInfo(viotDeviceId, requestBody)
                    .compose(RxSchedulerUtil.SchedulersTransformer1())
                    .onTerminateDetach()
                    .subscribe(result -> {
                        int resultCode = result.getMobBaseRes().getCode();
                        if (resultCode != 100) {
                            Log.i(TAG, "fetchUserInfoAndSave: error  " + resultCode);
                            return;
                        }
                        VMAccountEntity entity = result.getMobBaseRes().getResult();
                        Log.d(TAG, "fetchUserInfoAndSave entity :" + entity.toString());
                        VMAccountEntity.UserViomiInfoBeanBean viomInfoBean = entity.getUserViomiInfoBean();
                        Log.d(TAG, "fetchUserInfoAndSave viomInfoBean :" + viomInfoBean.toString());
                        UserInfoDb userInfoDb = new UserInfoDb();
                        userInfoDb.setUserId(viomInfoBean.getUserId());
                        userInfoDb.setMiUserId(miotUserId);
                        userInfoDb.setToken(entity.getToken());
                        userInfoDb.setHeadImgUrl(viomInfoBean.getHeadImg());
                        userInfoDb.setAccount(viomInfoBean.getAccount());
                        userInfoDb.setMobile(viomInfoBean.getMobile());
                        userInfoDb.setNickname(viomInfoBean.getNickName());
                        // ???????????????????????????
                        LoginRepository.getInstance().saveUserInfoToDb(userInfoDb);
                        ModuleSettingApplicaiton.setUserInfoDb(userInfoDb);
                        Log.d(TAG, "fetchUserInfoAndSave post MSG_VIOMI_LOGIN_SUCCESS:");
                        //??????????????????????????????
                        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_VIOMI_LOGIN, userInfoDb);
                        loginDeviceBindListener.setIsFetchUserInfo(false);
                    }, throwable -> Log.e(TAG, "exception" + throwable.getMessage()));
        }

        boolean isSubScribe = true;

        public void unbindMiAndClearInfo() {
            Log.i(TAG, "unbindMiAndClearInfo: ");
            // ????????????????????????????????????
            Observable.create((ObservableOnSubscribe<UserInfoDb>) emitter -> {
                UserInfoDb userInfoDb = ViomiRoomDatabase.getDatabase().userInfoDao().getUserInfo();
                int deleteResult = ViomiRoomDatabase.getDatabase().userInfoDao().delete(userInfoDb);
                Log.i(TAG, "subscribe: deleteResult:" + deleteResult);
                userInfoDb.setUserId(ModuleSettingConstants.VIOT_UNBIND);
                userInfoDb.setMiUserId(ModuleSettingConstants.VIOT_UNBIND);
                beforeMiotBind = userInfoDb.isBindMiot();
                userInfoDb.setBindMiot(false);
                userInfoDb.setNickname(ModuleSettingConstants.DEFAULT_NAME);
                userInfoDb.setHeadImgUrl(ModuleSettingConstants.DEFAULT_HEAD);
                Long insertResult = ViomiRoomDatabase.getDatabase().userInfoDao().insert(userInfoDb);
                Log.i(TAG, "subscribe: insertResult: " + insertResult);
                ModuleSettingApplicaiton.setUserInfoDb(userInfoDb);
                emitter.onNext(userInfoDb);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<UserInfoDb>() {
                        @Override
                        public void accept(UserInfoDb userInfoDb) throws Exception {
                            ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_VIOMI_LOGOUT, userInfoDb);
                            OvensoServiceFactory.getInstance().getOvenService().ViotLoginStatusChange(false);
                            WaterServiceFactory.getInstance().getWaterService().ViotLoginStatusChange(false);
                            // ?????????????????? userid ???1
                            bindViotServcie(userInfoDb);
                            Log.i(TAG, "unbindMiAndClearInfo  beforeMiotBind:  " + beforeMiotBind);
                            if (beforeMiotBind) {
                                //????????????
                                MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
                            }
                        }
                    });

        }
    }
}
