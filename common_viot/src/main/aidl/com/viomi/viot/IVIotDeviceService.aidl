package com.viomi.viot;

import java.util.List;
import com.viomi.viot.IOnVIotResultListener;
import com.viomi.viot.IOnSetPropertiesListener;
import com.viomi.viot.IOnActionListener;
import com.viomi.viot.IOnGetPropertiesListener;
import com.viomi.viot.IOnDeviceBindListener;
import com.viomi.viot.config.VIotDeviceConfig;
import com.viomi.viot.property.VIotDeviceProperty;
import com.viomi.viot.event.VIotDeviceEvent;
import com.viomi.viot.action.VIotDeviceAction;
import com.viomi.viot.push.IMessageArrivedListener;
import com.viomi.viot.IRemoteDebugListener;
import com.viomi.viot.IDataRefreshListener;
import com.viomi.viot.account.IAccountMessageListener;
import com.viomi.viot.IOnSetPropertyListListener;
import com.viomi.viot.IDeviceExtraMqttListener;
import com.viomi.viot.IViotConnectedListener;
import com.viomi.viot.IModelConfigListener;

interface IVIotDeviceService {

    void bindDevice(in VIotDeviceConfig config, IOnDeviceBindListener iOnDeviceBindListener);

    void resetDevice();

    void checkDeviceConnect(IOnVIotResultListener iOnVIotResultListener);

    void syncProperties(in List<VIotDeviceProperty> list, IOnVIotResultListener iOnVIotResultListener);

    void sendEvent(in List<VIotDeviceEvent> list, IOnVIotResultListener iOnVIotResultListener);

    void subscribeSetProperties(IOnSetPropertiesListener iOnSetPropertiesListener);

    void subscribeSetPropertyList(IOnSetPropertyListListener iOnSetPropertyListListener);

    void subscribeAction(IOnActionListener iOnActionListener);

    void subscribeGetProperties(IOnGetPropertiesListener iOnGetPropertiesListener);

    void subscrbiePushMessage(IMessageArrivedListener iMessageArrivedListener);

    void subscribeRemoteDebug(IRemoteDebugListener iRemoteDebugListener);

    void subscribeDataRefresh(IDataRefreshListener iDataRefreshListener);

    void subscribeAccountRefresh(IAccountMessageListener iAccountMessageListener);

    void subscribeExtraMqttMessage(IDeviceExtraMqttListener iDeviceExtraMqttListener);

    void sendMqttMessage(in String payload, in String subDid, in boolean isReply);

    void enableLog(in boolean enable);

    void setDebugEnvironment(in boolean enable);

    void notifyActionOutAdd(in VIotDeviceAction action);

    void notifyPropertiesValue(in List<VIotDeviceProperty> list, in String id);

    void updateMacAddress(in String mac);

    void uploadExtraDevInfo(in Map<String, String> map);

    void registerViotConnectedListener(IViotConnectedListener iViotConnectedListener);

    void getModelConfig(IModelConfigListener iModelConfigListener);
}
