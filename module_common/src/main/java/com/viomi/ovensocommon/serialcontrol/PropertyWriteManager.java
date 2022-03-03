package com.viomi.ovensocommon.serialcontrol;

import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.viomi.common.R;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropResponseBody;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ljh on 2020/8/24.
 * Description:
 */
public class PropertyWriteManager implements CommonCallback<ViotSetPropResponseBody> {
    private static final String TAG = "PropertyWriteManager";
    private static volatile PropertyWriteManager instance;
    private List<PropertyEntity> propertyEntityList;

    public SerialCommunicateManager getSerialManager() {
        return serialManager;
    }

    private volatile SerialCommunicateManager serialManager;

    public static PropertyWriteManager getInstance() {
        if (instance == null) {
            synchronized (PropertyWriteManager.class) {
                if (instance == null) {
                    instance = new PropertyWriteManager();
                }
            }
        }
        return instance;
    }

    private PropertyWriteManager() {
        serialManager = SerialCommunicateManager.getInstance();
    }

    /**
     * 根据属性名称，sid，pid 设置属性值
     * 设置单个属性
     */
    public void setProperty(PropertyEntity propertyEntity) {
        Log.i(TAG, "setProperty: setProperty");
        ArrayList<PropertyEntity> propertyEntityArrayList = new ArrayList<>(1);
        propertyEntityArrayList.add(propertyEntity);
        setPropertyList(propertyEntityArrayList);

    }

    /**
     * 设置多个属性
     *
     * @param propertyEntityList 属性集合
     */
    public void setPropertyList(List<PropertyEntity> propertyEntityList) {
        Log.i(TAG, "setPropertyList");
        ArrayList<ViotSetPropRequestBody.Prop> viotPropList = new ArrayList<>();
        for (PropertyEntity propertyEntity : propertyEntityList) {
            ViotSetPropRequestBody.Prop viotprop = new ViotSetPropRequestBody.Prop();
            viotprop.sid = propertyEntity.getSid();
            viotprop.pid = propertyEntity.getPid();
            viotprop.value = propertyEntity.getContent();
            viotPropList.add(viotprop);
            Log.i(TAG, "setPropertyList: " + viotprop.sid + "  " + viotprop.pid + "  " + viotprop.value);
        }
        ViotSetPropRequestBody body = new ViotSetPropRequestBody();
        body.propList.addAll(viotPropList);
        this.propertyEntityList = propertyEntityList;
        serialManager.setProperties(body, this);
    }

    /**
     * 指令的回调, 设置的结果依赖属性变化
     *
     * @param resultCode 错误码
     * @param result     结果
     * @param desc       描述
     * @throws Exception
     */
    @Override
    public void onReceiveResult(int resultCode, @NonNull ViotSetPropResponseBody result, String desc) {
        Log.i(TAG, "onReceiveResult: resultCode: " + resultCode);
        if (resultCode != CommonConstant.SERIAL_RESULTCODE_SUCCESS) {
            // toast 提示 return
            String serialDisconnect = Utils.getApp().getString(R.string.muc_set_fail);
            Log.i(TAG, "onReceiveResult: serialDisconnect: return  " + serialDisconnect);
            ViomiToastUtil.showToastCenter(serialDisconnect);
            return;
        }
        Log.i(TAG, "onReceiveResult: size: " + result.propList.size());
        Log.i(TAG, "onReceiveResult: MSG_DOWNWRITE_SUCCESS    ");
        ViomiRxBus.getInstance().post(CommonConstant.MSG_DOWNWRITE_SUCCESS);
        for (PropertyEntity propertyEntity : propertyEntityList) {
            ViomiRxBus.getInstance().post(CommonConstant.MSG_PROPERTY_CHANGE);
            MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntity);
            ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntity);
        }

    }


}

