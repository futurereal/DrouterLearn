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
 * Description:VIOT 通道管理类
 */
public class ViotDeviceManager {
    public static final String TAG = "DeviceManagerViot";
    private static final ViotDeviceManager deviceManager = new ViotDeviceManager();
    private static final boolean NEED_VERIFIY = false;
    //帐号关系绑定 和解绑， 接收推送服务
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
        // 如果需要检验的话，才需要传 小米的设备id
        if (NEED_VERIFIY) {
            miotDeviceId = ViomiProvideUtil.getMiotDeviceId();
        }
        loginDeviceBindListener = new LoginDeviceBindListener();
    }

    public static ViotDeviceManager getInstance() {
        return deviceManager;
    }

    /**
     * 开始绑定设备
     */
    public void bindViotServcie(UserInfoDb userInfoDb) {
        Log.i(TAG, "bindViotServcie...");
        String token = null;
        String userId = null;
        if (userInfoDb != null) {
            token = userInfoDb.getToken();
            userId = userInfoDb.getUserId();
        }
        //是否停止根据情况来
//        VIotHostManager.Companion.getInstance().stopDevice(mContext);
        try {
            VIotDeviceConfig deviceConfig = new VIotDeviceConfig(viotPid, token, userId, viotDeviceId,
                    deviceAccessKey, cloudPublickey, MANUFACTURE_NAME, viotModelName, miotDeviceId, NEED_VERIFIY);
            Log.i(TAG, "bindViotServcie VIotDeviceConfig :" + deviceConfig);
            //MQTT 日志
            VIotHostManager.Companion.getInstance().enableLog(CommonConstant.VIOT_LOG_OPEN);
            VIotHostManager.Companion.getInstance().setDebugEnvironment(ModuleSettingConstants.IS_DEBUG_ENV);
            Log.i(TAG, "bindViotServcie startBindDevice :");
            loginDeviceBindListener.setUserinfo(userInfoDb);
            VIotHostManager.Companion.getInstance().startDevice(mContext, deviceConfig, loginDeviceBindListener);
        } catch (Exception exception) {
            Log.i(TAG, "bindViotServcie: Exception: " + exception.getMessage());
        }
    }

    //设备端主动发起重置设备
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
         * 云米服务绑定成功
         *
         * @param vIotDeviceConfig 返回的参数
         */
        @Override
        public void onSucceed(@NotNull VIotDeviceConfig vIotDeviceConfig) {
            Log.i(TAG, "LoginDeviceBindListener startDevice onSucceed:" + vIotDeviceConfig);
            // 服务器推动的绑定解绑信息
            registerRefreshAccount(userInfo);
        }

        /**
         * 云米服务绑定失败
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
         * 设备绑定云米账号成功
         */
        @Override
        public void onBind() {
            String viotUserId = userInfo.getUserId();
            String miotUserId = userInfo.getMiUserId();
            Log.i(TAG, "onBind: viotUserId: " + viotUserId + " miotUserId: " + miotUserId);
            // 绑定成功，
            if (TextUtils.isEmpty(viotUserId) || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_UNBIND) || TextUtils.equals(viotUserId, ModuleSettingConstants.VIOT_NO_BIND)) {
                Log.i(TAG, "onBind: viotUserId  is null or -1  return ");
                return;
            }
            // 区分第一次绑定 和 从数据库读取userInfoDb 的绑定
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
            // 存在Viot 绑定成功，但是miot 被解绑的情况。
            if (userInfo.isBindMiot() || !TextUtils.equals(miotUserId, ModuleSettingConstants.VIOT_UNBIND)) {
                // 登录云米必须重置米家的绑定
                MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
            }
            //Logan日志记录
            Vlog.setUnionId(userInfo.getUserId());
            //注册日志拉取，跟是否登录成功没有关系
            registerLogPullListener();
            //注册插件下发属性  和  上报一次屏端的属性的属性给插件
            registerPropertyChangeListener();
            SerialControl.getMucPropertiesAndReport();
            // 回调绑定状态
//            ModuleSettingServiceFactory.getInstance().getViotService().getSettingListener().onBindStatus(true);
        }

        /**
         * 手机端 和 设备端均解绑
         */
        @Override
        public void onUnBind() {
            Log.i(TAG, " LoginDeviceBindListener  onUnBind...");
            isSubScribe = true;
            unbindMiAndClearInfo();
//            ModuleSettingServiceFactory.getInstance().getViotService().getSettingListener().onBindStatus(true);
        }


        /**
         * 管理平台 拉取日志的监听
         */
        private void registerLogPullListener() {
            VIotHostManager.Companion.getInstance().registerRemoteDebugCallback(new OnRemoteDebugListener() {
                @Override
                public void remoteMessage(String messageContent) {
                    //区分这个log是哪个应用的，屏端设备是pid即可，手机设备，安卓云米商城1，iOS云米商城2
                    Log.i(TAG, "remoteMessage: " + messageContent);
                    VLogUtil.setPid(PID_ANDROID);
                    //设备id，屏端设备是did，应该有通用方法获取的，手机设备会自动获取设备号的
                    Vlog.setDeviceId(ViomiProvideUtil.getDeviceId());
                    //此命令会自动上传系统日志，如果系统日志想自己处理上传，可以调用
                    //Vlog.commandHandle(message, false)，然后自己处理上传
                    Vlog.commandHandle(messageContent);
                }
            });
        }

        /**
         * 插件下发的属性 和指令的 处理
         */
        private void registerPropertyChangeListener() {
            Log.i(TAG, "registerPropertyChangeListener: ");
            //监听单个属性下发的逻辑
            VIotHostManager.Companion.getInstance().registerSetPropertiesCallback(new OnSetPropertiesListener() {
                @Override
                public void onSetProperty(@androidx.annotation.Nullable VIotDeviceProperty property) {
                    // 判断串口是否连接
                    // 判断是否正在MCU升级
                    // 新建菜谱
                    // 启动自定义菜谱  1 、设置属性   2  、设置Action
                    Log.i(TAG, "registerPropertyChangeListener ： onSetProperty: " + property);
                    PropertyEntity propertyEntity = new PropertyEntity(property.getSiid(), property.getPiid(), property.getValue());
                    // 烤箱viot 插件下发指令
                    OvensoServiceFactory.getInstance().getOvenService().dealPropertyFromPlug(propertyEntity);
                    // 净水器ERO 插件下发指令
                    WaterServiceFactory.getInstance().getWaterService().dealPropertyFromPlug(propertyEntity);
                }

                @Override
                public void onFailed(int code, @androidx.annotation.Nullable String message) {
                    Log.i(TAG, "registerPropertyChangeListener  onFailed:  code: " + code + " message: " + message);
                }
            });
            //一次性监听多个属性下发
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
            // 插件拉取属性的逻辑 viot 这个没有用到，viot的逻辑是依赖上报
            VIotHostManager.Companion.getInstance().registerGetPropertiesCallback(new OnGetPropertiesListener() {
                @androidx.annotation.Nullable
                @Override
                public List<VIotDeviceProperty> onGetProperty(@androidx.annotation.Nullable List<VIotDeviceProperty> list) {
                    Log.i(TAG, "onGetProperty: " + list.size());
                    // 由于获取属性是同步的逻辑，获取之后需要立马上报，所以需要从sp 里面读取属性，
                    // 由于没有返回默认值，所有的属性 以数字的形式测试 能获取属性就行。
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
            //监听云米下发的，直接向串口下发指令， 下发指令的结果 通过属性变化来体现。
            VIotHostManager.Companion.getInstance().registerActionCallback(new OnActionListener() {
                @Override
                public void onAction(VIotDeviceAction vIotDeviceAction) {
                    int siid = vIotDeviceAction.getSiid();
                    int aiid = vIotDeviceAction.getAiid();
                    Log.i(TAG, "onAction: " + siid + " " + aiid);
                    // 检测固件是否在升级
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
         * @description: 从sp 获取属性，由于不知道属性的具体类型，所以尝试 数字 boolean  字符串等。
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
         * 1 在云米商城绑定 小米账号和解绑小米账号 的回调。ACCOUNT_RELATION_UNBIND_MIOT
         * 2 米家app 扫码登录设备，小米账号绑定了云米，也会推送 ACCOUNT_RELATION_BIND_VIOT
         * 3 云米商城  绑定小米账号，小米账号绑定的有设备 推送 ACCOUNT_RELATION_BIND_VIOT
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
                // 云米商城解绑小米账号
                if (handleType == ACCOUNT_RELATION_UNBIND_MIOT) {
                    Log.w(TAG, "onAccountRefresh  unbindMiot");
                    String savedMiId = userinfoDb.getMiUserId();
                    Log.i(TAG, "onAccountRefresh: savedMiId: " + savedMiId);
                    userinfoDb.setMiUserId(ModuleSettingConstants.VIOT_UNBIND);
                    userinfoDb.setBindMiot(false);
                    ModuleSettingApplicaiton.setUserInfoDb(userinfoDb);
                    ViomiRoomDatabase.getDatabase().userInfoDao().insert(userinfoDb);
                    // 解绑小米，通过回调更新界面
                    MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
                    return;
                }
                //帐号关系绑定
                if (handleType == ACCOUNT_RELATION_BIND_IOT) {
                    // 云米账号从未绑定到已经绑定
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
                    // 小米账号 从 未绑定  到 已绑定
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
         * 从服务器获取 用户信息，并且缓存到本地
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
                        // 缓存用户信息到本地
                        LoginRepository.getInstance().saveUserInfoToDb(userInfoDb);
                        ModuleSettingApplicaiton.setUserInfoDb(userInfoDb);
                        Log.d(TAG, "fetchUserInfoAndSave post MSG_VIOMI_LOGIN_SUCCESS:");
                        //刷新帐号信息进行显示
                        ViomiRxBus.getInstance().post(ModuleSetingEventConstant.MSG_VIOMI_LOGIN, userInfoDb);
                        loginDeviceBindListener.setIsFetchUserInfo(false);
                    }, throwable -> Log.e(TAG, "exception" + throwable.getMessage()));
        }

        boolean isSubScribe = true;

        public void unbindMiAndClearInfo() {
            Log.i(TAG, "unbindMiAndClearInfo: ");
            // 删除本地云米相关账号信息
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
                            // 重新绑定云米 userid 传1
                            bindViotServcie(userInfoDb);
                            Log.i(TAG, "unbindMiAndClearInfo  beforeMiotBind:  " + beforeMiotBind);
                            if (beforeMiotBind) {
                                //解绑米家
                                MiotServiceFactory.getInstance().getMiotService().resetAndRebindMiot();
                            }
                        }
                    });

        }
    }
}
