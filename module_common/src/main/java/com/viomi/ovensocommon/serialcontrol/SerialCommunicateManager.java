package com.viomi.ovensocommon.serialcontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.iotdevice.IotSerialConfig;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.callback.IotSerialCallback;
import com.viomi.iotdevice.common.callback.ProgressCallback;
import com.viomi.iotdevice.common.exception.SerialException;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.common.util.RxSchedulerUtil;
import com.viomi.iotdevice.iottomcu.bean.ViotActionRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotActionResponseBody;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropResponseBody;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropResponseBody;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.ModelUtil;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.miot.MiotServiceFactory;
import com.viomi.ovensocommon.componentservice.modulesetting.ModuleSettingServiceFactory;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.componentservice.waterpurifier.WaterServiceFactory;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.spec.OvenPropEnum;
import com.viomi.vlog.Vlog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2020/8/24.
 * Description:????????????
 */
public class SerialCommunicateManager {
    private static final String TAG = "SerialCommunicateManage";
    public static final int READ_PERIOD = 200;
    private static volatile SerialCommunicateManager instance;
    // ?????????????????????
    public static final int DEFAUL_BAUD_RATE = 115200;
    public static final String DEFAULT_BAUD_PATH = "/dev/ttyS0";
    public static final IotSerialConfig.IotType DEFAULT_IOT_TYPE = IotSerialConfig.IotType.PROTOCOL_VIOT;

    private Context mContext;
    private CompositeDisposable mCompositeDisposable;
    private SerialIotSerialCallback mIotSerialCallback;

    public static SerialCommunicateManager getInstance() {
        if (instance == null) {
            synchronized (SerialCommunicateManager.class) {
                if (instance == null) {
                    instance = new SerialCommunicateManager();
                }
            }
        }
        return instance;
    }

    private SerialCommunicateManager() {
        mContext = ApplicationUtils.getContext();
        if (mContext == null) {
            mContext = ApplicationUtils.getContext();
        }
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * ???????????????????????????
     */
    public void openSerialPort() {
        openSerialPort(DEFAULT_IOT_TYPE, DEFAUL_BAUD_RATE, DEFAULT_BAUD_PATH);
    }

    /**
     * ???????????????????????????
     * MissingPermission  ??????info.getMacAddress ?????????
     */
    @SuppressLint("MissingPermission")
    void openSerialPort(IotSerialConfig.IotType iotType, int baudRate, String path) {
        Log.i(TAG, "openSerialPort  start");
        if (CommonConstant.TEST_UI) {
            Log.i(TAG, "openSerialPort: testUI not open return");
            return;
        }
        //?????? device_lib ??????????????????  ?????????????????????????????????
        if (mIotSerialCallback == null) {
            mIotSerialCallback = new SerialIotSerialCallback();
        }
        ViomiIotManager.getInstance().setHandler(mIotSerialCallback);
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = info.getMacAddress();
        // ????????????????????????????????????
        IotSerialConfig.Builder builder = new IotSerialConfig.Builder();
        IotSerialConfig iotSerialConfig = builder.setIotType(iotType)
                .setMcuSerialBaudRate(baudRate)
                .setMcuSerialPath(path)
                .setDebugEnv(true)
                .setMac(mac)
                .setReadPeriod(READ_PERIOD)
                .build();
        // ????????????
        ViomiIotManager.getInstance().enableLog(CommonConstant.SERIAL_LOG_OPEN);
        Log.i(TAG, "openSerialPort  strart");
        // ????????????
        ViomiIotManager.getInstance().init(mContext, iotSerialConfig);
        Log.i(TAG, "openSerialPort  end and save status");
        // ????????????????????????????????????
        CommonPreference.getInstance().setSerialDisconnect(false);
    }

    /**
     * ????????????
     */
    public void closeSerialPort() {
        ViomiIotManager.getInstance().close();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
        mIotSerialCallback = null;
        Log.i(TAG, "closeSerialPort");
    }

    /**
     * ??????????????????
     *
     * @param requestBody
     * @param commonCallback
     */
    void getProperties(ViotGetPropRequestBody requestBody, CommonCallback<ViotGetPropResponseBody> commonCallback) {
        getProperties(requestBody, false, commonCallback);
    }

    /**
     * ??????????????????
     *
     * @param requestBody
     * @param isPolling
     * @param commonCallback
     */
    void getProperties(ViotGetPropRequestBody requestBody, boolean isPolling, CommonCallback<ViotGetPropResponseBody> commonCallback) {
        if (ViomiIotManager.getInstance().isSerialBusy()) {
            Log.e(TAG, "IotToMcuSerialManager getprop busy");
            return;
        }
        ViomiIotManager.getInstance().getProperties(requestBody, commonCallback);
        if (isPolling) {
            Disposable disposableGetProp = Observable.interval(3000, TimeUnit.MILLISECONDS)
                    .compose(RxSchedulerUtil.SchedulersTransformer5())
                    .onTerminateDetach()
                    .subscribe(aLong -> {
                        getProperties(requestBody, commonCallback);
                    });
            mCompositeDisposable.add(disposableGetProp);
        }
    }

    /**
     * ???????????????????????????
     * 1???commonCallBack ???????????????????????????
     * 2??? SerialIotSerialCallback ????????? ?????? event ?????????????????????????????????????????????????????????MainPrseneter ??????
     *
     * @param requestBody
     */
    void setProperties(ViotSetPropRequestBody requestBody, CommonCallback<ViotSetPropResponseBody> commonCallback) {
        ViomiIotManager.getInstance().setProperties(requestBody, commonCallback);
    }

    /**
     * action??????
     *
     * @param actionBody
     * @param commonCallback
     */
    void doAction(ViotActionRequestBody actionBody, CommonCallback<ViotActionResponseBody> commonCallback) {
        Log.i(TAG, "actionBody: " + actionBody.aid);
        ViomiIotManager.getInstance().action(actionBody, commonCallback);
    }

    /**
     * OTA??????mcu??????
     *
     * @param binFilePath
     * @param callback
     */
    void mcuOta(String binFilePath, ProgressCallback callback) {
        try {
            Log.i(TAG, "OTA Start");
            ViomiIotManager.getInstance().otaStart(binFilePath, callback);
        } catch (SerialException e) {
            Log.i(TAG, "OTA Fail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????
     */
    class SerialIotSerialCallback implements IotSerialCallback {
        /**
         * ???????????????????????????????????????????????????
         *
         * @param prop       ???????????????
         * @param serialData ??????????????????
         * @ ??????
         */
        @Override
        public void props(Map prop, String serialData) {
            Log.d(TAG, "props: serialData:" + serialData);
            List<PropertyEntity> propertyEntityList = new ArrayList<PropertyEntity>();
            Iterator<Map.Entry<String, Object>> it = prop.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                String[] sidAndPid = key.split("\\.");
                if (sidAndPid.length != 2) {
                    continue;
                }
                int sid = Integer.parseInt(sidAndPid[0]);
                int pid = Integer.parseInt(sidAndPid[1]);
                Object value = entry.getValue();
                // ???????????????sp ??????
                PropertyPreferenceManager.getInstance().setProperty(sid, pid, value);
                PropertyEntity propertyEntity = new PropertyEntity(sid, pid, value);
                // ??????????????????????????????MainPresenter
                OvensoServiceFactory.getInstance().getOvenService().dealPropertyChangeFromFirm(propertyEntity);
                WaterServiceFactory.getInstance().getWaterService().dealPropertyChangeFromFirm(propertyEntity);
                if (sid == OvenPropEnum.TEMPER.siid && pid == OvenPropEnum.TEMPER.piid) {
                    int workStatusValue = (int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.WORK_STATUS.siid, OvenPropEnum.WORK_STATUS.piid, 0);
                    Log.i(TAG, "props:workStatusValue " + workStatusValue);
                    if (workStatusValue == 0) {
                        continue;
                    }
                }
                propertyEntityList.add(propertyEntity);
            }
            Log.i(TAG, "props: propertyEntityList size:" + propertyEntityList.size());
            // ?????????????????? ??? ????????? 0
            if (propertyEntityList.size() == 0) {
                Log.i(TAG, "props: property  is  ignor");
                return;
            }
            // miot????????????
            MiotServiceFactory.getInstance().getMiotService().reportData(propertyEntityList);
            // viot ????????????
            ModuleSettingServiceFactory.getInstance().getViotService().reportData(propertyEntityList);
        }

        /**
         * ????????????????????? ????????? ??? ?????? ??????????????????  app ??????????????????????????????????????????
         * <p>
         * ??????????????? ?????? ?????? ?????? ??????
         * 1 ???????????????????????????  ??????????????????event 0 0
         * 2 ????????????????????????event 0 1
         * 3 ??????????????????event 0 2
         *
         * @param pack
         * @param serialData
         */
        @Override
        public void event(EventPack pack, String serialData) {
            if (pack == null || pack.name == null || TextUtils.isEmpty(pack.name)) {
                Log.i(TAG, "event: pack is null  or packName is null");
                return;
            }
            String[] sAndEId = pack.name.split("\\.");
            if (sAndEId == null || sAndEId.length != 2) {
                Log.i(TAG, "event: sAndEId is null or length !=2");
                return;
            }
            int siid = Integer.parseInt(sAndEId[0]);
            int eid = Integer.parseInt(sAndEId[1]);
            if (siid == 0) {
                Log.i(TAG, "event: error  siid ==0 eiid == " + eid);
                if (eid == 0) {
                    CommonPreference.getInstance().setSerialDisconnect(true);
                    ViomiRxBus.getInstance().post(CommonConstant.MSG_COMMUNICATE_DISCONNECT);
                } else if (eid == 1) {
                    CommonPreference.getInstance().setSerialDisconnect(false);
                    ViomiRxBus.getInstance().post(CommonConstant.MSG_COMMUNICATE_CONNECTED);
                } else if (eid == 2) {
                    ViomiRxBus.getInstance().post(CommonConstant.MSG_COMMUNICATE_DATA_ERR);
                }
                return;
            }
            //???????????????????????????
            String packName = pack.name;
            Log.i(TAG, "event: packName: " + packName + " serialData: " + serialData);
            //??????????????????
            MiotServiceFactory.getInstance().getMiotService().reportAction(packName);
            //??????????????????
            ModuleSettingServiceFactory.getInstance().getViotService().reportEvent(siid, eid);
            PropertyEntity propertyEntity = new PropertyEntity(siid, eid);
            // ????????????????????????????????? ????????????
            OvensoServiceFactory.getInstance().getOvenService().dealEventChangeFromFirm(propertyEntity);
            WaterServiceFactory.getInstance().getWaterService().dealEventChangeFromFirm(propertyEntity);
            ViomiRxBus.getInstance().post(CommonConstant.MSG_EVENT_CHANGE, propertyEntity);
        }

        @Override
        public void json_send(String json) {
            Log.d(TAG, "json send=" + json);
        }

        /**
         * ??????????????????????????????????????????
         *
         * @param model
         */
        @Override
        public void model(String model) {
            Log.i(TAG, "model=" + model);
            // ???????????????????????????????????????????????????????????????????????????????????????
            model = model.replace("\"", "");
            Log.i(TAG, "model: final Mode: " + model);
            CommonPreference.getInstance().setMcuUpgrade(false);
            PropertyPreferenceManager.getInstance().setProperty(CommonConstant.SERIAL_MODE_SIID, CommonConstant.SERIAL_MODE_PIID, model);
            int modelId = ModelUtil.getViomiPid();
            Log.i(TAG, "model: modelId: " + modelId);
            // ??????????????????id ????????????
            Vlog.setPid(String.valueOf(modelId));
        }

        @Override
        public void mcu_version(String mcuVersion) {
            Log.d(TAG, "mcu_version=" + mcuVersion);
            PropertyPreferenceManager.getInstance().setProperty(CommonConstant.SERIAL_VERSION_SIID, CommonConstant.SERIAL_VERSION_PIID, mcuVersion);
            WaterServiceFactory.getInstance().getWaterService().isPowerOffLauncher();
        }

        @Override
        public void enterTestMode() {
            Log.i(TAG, "enterTestMode: ");
        }
    }
}


