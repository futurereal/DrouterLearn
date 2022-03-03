package com.viomi.ovensocommon.serialcontrol;

import android.util.Log;

import androidx.annotation.NonNull;

import com.viomi.common.ViomiProvideUtil;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.model.IotResultFormat;
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotActionResponseBody;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/8/24.
 * Description:
 */
public class ActionSendManager implements CommonCallback<ViotActionResponseBody> {

    private static final String TAG = ActionSendManager.class.getName();

    private static ActionSendManager instance;

    public SerialCommunicateManager getSerialManager() {
        return serialManager;
    }

    private final SerialCommunicateManager serialManager;
    private String currentActionName = "";
    private int currentSid;
    private int currentAid;
    private List<ViotActionRequestBody.Prop> currentParamsProp;
    private OnSetActionCallback listener;

    static ActionSendManager getInstance() {
        if (instance == null) {
            synchronized (ActionSendManager.class) {
                if (instance == null) {
                    instance = new ActionSendManager();
                }
            }
        }
        return instance;
    }

    private ActionSendManager() {
        serialManager = SerialCommunicateManager.getInstance();
    }

    /**
     * 发送Action
     *
     * @param actionName
     * @param sid
     * @param aid
     */
    void sendActionByName(String actionName, int sid, int aid, List<PropertyEntity> propList, OnSetActionCallback callback) {
        Log.i(TAG, "sendActionByName:  sid: " + sid + "  aid: " + aid);
        listener = callback;
        currentActionName = actionName;
        currentSid = sid;
        currentAid = aid;
        ViotActionRequestBody body = new ViotActionRequestBody();
        body.sid = currentSid;
        body.aid = currentAid;
        if (propList != null && propList.size() > 0) {
            for (PropertyEntity propertyEntity : propList) {
                ViotActionRequestBody.Prop viotprop = new ViotActionRequestBody.Prop();
                viotprop.pid = propertyEntity.getPid();
                viotprop.value = propertyEntity.getContent();
                Log.i(TAG, "sendActionByName:  pid: " + propertyEntity.getPid() + "  value: " + propertyEntity.getContent());
                body.propList.add(viotprop);
            }
        }
        serialManager.doAction(body, this);
    }

    /**
     * 执行指令的回调
     *
     * @param resultCode 返回码
     * @param result     结果
     * @param desc       描述
     * @throws Exception
     */
    @Override
    public void onReceiveResult(int resultCode, @NonNull ViotActionResponseBody result, String desc) {
        Log.i(TAG, "onReceiveResult: resultCode:  " + resultCode);
        if (resultCode == IotResultFormat.RESULT_OK && currentParamsProp != null && currentParamsProp.size() > 0) {
            Log.i(TAG, "IotResultFormat: ok:  " + resultCode);
            List<PropertyEntity> propertyEntities = new ArrayList<>();
            String did = ViomiProvideUtil.getDeviceId();
            for (ViotActionRequestBody.Prop prop : currentParamsProp) {
                propertyEntities.add(new PropertyEntity(currentSid, prop.pid, prop.value));
            }
            ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntities);
            MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntities);
        }
        Log.i(TAG, "onReceiveResult: listener : " + listener);
        currentActionName = "";
        currentSid = -1;
        currentAid = -1;
        if (listener != null) {
            listener.setActionResult(currentActionName, resultCode);
            listener = null;
        }
    }

    /**
     * 设置Action返回值
     */
    interface OnSetActionCallback {
        void setActionResult(String propertyName, int code);
    }

}

