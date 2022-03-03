package com.viomi.ovensocommon.serialcontrol;

import android.util.Log;

import androidx.annotation.NonNull;

import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.model.IotResultFormat;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropResponseBody;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/8/24.
 * Description:读属性
 */
public class PropertyGetManager implements CommonCallback<ViotGetPropResponseBody> {
    private static final String TAG = "ReadPropertyManager";
    private static volatile PropertyGetManager instance;
    private final SerialCommunicateManager serialManager;
    private String currentPropertyName = "";
    private int currentSid;
    private int currentPid;
    private OnGetPropertyCallback listener;

    static PropertyGetManager getInstance() {
        if (instance == null) {
            synchronized (PropertyGetManager.class) {
                if (instance == null) {
                    instance = new PropertyGetManager();
                }
            }
        }
        return instance;
    }

    private PropertyGetManager() {
        serialManager = SerialCommunicateManager.getInstance();
    }


    /**
     * 根据属性名称，sid，pid从单片机读属性值
     *
     * @param propertyName 属性名
     * @param sid          服务id
     * @param pid          属性id
     */
    void getPropertyByName(String propertyName, int sid, int pid, OnGetPropertyCallback callback) {
        currentPropertyName = propertyName;
        currentSid = sid;
        currentPid = pid;

        ViotGetPropRequestBody body = new ViotGetPropRequestBody();
        ViotGetPropRequestBody.Prop prop = new ViotGetPropRequestBody.Prop();
        prop.sid = currentSid;
        prop.pid = currentPid;
        body.propList.add(prop);

        listener = callback;
        serialManager.getProperties(body, this);
    }

    void getProperty(List<ViotGetPropRequestBody.Prop> propList, OnGetPropertyCallback callback) {
        Log.i(TAG, "getProperty: propListSize: " + propList.size());
        currentPropertyName = "";
        currentSid = -1;
        currentPid = -1;
        ViotGetPropRequestBody body = new ViotGetPropRequestBody();
        body.propList.addAll(propList);
        listener = callback;
        serialManager.getProperties(body, this);
    }

    void getPropertyList(List<PropertyEntity> propertyEntityList, OnGetPropertyCallback callback) {
        Log.i(TAG, "getPropertyList: " + propertyEntityList.size());
        currentPropertyName = "";
        currentSid = -1;
        currentPid = -1;
        ViotGetPropRequestBody body = new ViotGetPropRequestBody();
        List<ViotGetPropRequestBody.Prop> propList = new ArrayList<>();
        for (PropertyEntity propertyEntity : propertyEntityList) {
            ViotGetPropRequestBody.Prop prop = new ViotGetPropRequestBody.Prop(propertyEntity.getSid(), propertyEntity.getPid());
            propList.add(prop);
        }
        body.propList.addAll(propList);
        listener = callback;
        serialManager.getProperties(body, this);
    }

    @Override
    public void onReceiveResult(int resultCode, @NonNull ViotGetPropResponseBody result, String desc) {
        if (resultCode == IotResultFormat.RESULT_OK) {
            Log.i(TAG, "resultCode: " + resultCode + "   result: " + result.propList.size() + "  desc: " + desc);
            List<PropertyEntity> changedProperties = new ArrayList<>();
            List<PropertyEntity> allProperties = new ArrayList<>();
            for (ViotGetPropResponseBody.Prop prop : result.propList) {
                if (prop.code != 0) {
                    Log.e(TAG, "onReceiveResult err siid:" + prop.sid + " piid:" + prop.pid + " code:" + prop.code);
                    continue;
                }
                PropertyEntity propertyEntity = new PropertyEntity(prop.sid, prop.pid, prop.value);
                allProperties.add(propertyEntity);
                // 判断是否有属性变化
                boolean isPropertyChange = PropertyPreferenceManager.getInstance().judgePropertyChange(prop.sid, prop.pid, prop.value);
                Log.i(TAG, "onReceiveResult: isPropertyChange : " + isPropertyChange + "    siid:" + prop.sid + " piid:" + prop.pid + " value: " + prop.value);
//                if (isPropertyChange) {
                changedProperties.add(propertyEntity);
                PropertyPreferenceManager.getInstance().setProperty(prop.sid, prop.pid, prop.value);
//                }
            }
            //属性必须要上报
            ModuleSettingServiceFactory.getInstance().getViotService().reportData(changedProperties);
            MiotServiceFactory.getInstance().getMiotService().reportData(changedProperties);

            for (ViotGetPropResponseBody.Prop prop : result.propList) {
                int sid = prop.sid;
                int pid = prop.pid;
                if (sid == currentSid && pid == currentPid) {
                    if (listener != null) {
                        Object value = prop.value;
                        Log.e(TAG, "sid: " + sid + "   pid: " + pid + "  value: " + value.toString());
                        listener.getPropertyResult(currentPropertyName, value);
                    }
                    break;
                }
            }
        } else {
            Log.e(TAG, "getProperty Error  resultCode: " + resultCode + "  desc: " + desc);
            if (listener != null) {
                listener.getPropertyResult(currentPropertyName, resultCode);
            }
        }
        currentPropertyName = "";
        currentSid = -1;
        currentPid = -1;
        listener = null;
    }

    /**
     * 获取属性返回值
     */
    interface OnGetPropertyCallback {
        void getPropertyResult(String propertyName, Object result);
    }
}

