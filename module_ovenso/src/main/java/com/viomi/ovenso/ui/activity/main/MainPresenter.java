package com.viomi.ovenso.ui.activity.main;

import android.os.Bundle;
import android.util.Log;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.OvenBusEventConstants;
import com.viomi.ovenso.bean.OvenDeviceFaultEnum;
import com.viomi.ovenso.helper.ReportUtils;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.util.MessageUtils;
import com.viomi.ovenso.util.OvenUtil;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.componentservice.ovenso.OvensoServiceFactory;
import com.viomi.ovensocommon.db.MessageEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.serialcontrol.SerialControl;
import com.viomi.ovensocommon.spec.OvenPropEnum;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Ljh on 2020/11/16.
 * Description:
 */
public class MainPresenter extends MainContract.Presenter<MainContract.View> {
    private static final String TAG = "MainPresent";
    private static final int SETTING_SUCCESS = 0;
    private CompositeDisposable mCompositeDisposable;

    public MainPresenter() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        initDisposable();
    }

    /**
     * 监听属性变化
     * 监听WIFI 固件等变变化
     */
    public void initDisposable() {
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(viomiRxBusEvent -> {
            // 属性变化 475
            Log.i(TAG, "eventId: " + viomiRxBusEvent.getMsgId());
            if (mView == null) {
                Log.i(TAG, "subscrebeData: mView is null return ");
                return;
            }
            mView.updatePropertyView();
            Object msgObject = viomiRxBusEvent.getMsgObject();
            switch (viomiRxBusEvent.getMsgId()) {
                case OvenBusEventConstants.MSG_UPDATE_LIGHT:
                    boolean lightStatus = (boolean) msgObject;
                    Log.i(TAG, "initDisposable: lightStatus: " + lightStatus);
                    mView.refreshLight(lightStatus);
                    break;
                case OvenBusEventConstants.MSG_UPDATE_PANEL:
                    boolean panelStatus = (boolean) msgObject;
                    Log.i(TAG, "initDisposable: panelStatus: " + panelStatus);
                    mView.refreshPannel(panelStatus);
                    break;
                case CommonConstant.MSG_WIFI_STATUS_CHANGE:
                    // WIFI是否打开
                    boolean isNetConnect = (boolean) msgObject;
                    mView.refreshNetState(isNetConnect);
                    break;
                case OvenBusEventConstants.MSG_FIRMUPDATE_SUCCESS:
                    //固件升级完成
                    SerialControl.getMucPropertiesAndReport();
                    break;
                case OvenBusEventConstants.MSG_COMMUNICATE_DISCONNECT:
                    int titleId = OvenDeviceFaultEnum.FAULT_DISCONNECT.titleId;
                    int contentId = OvenDeviceFaultEnum.FAULT_DISCONNECT.msgId;
                    OvenUtil.showDeviceFaultDialog(titleId, contentId);

                    // 加入消息提醒
                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setMessageType(MessageEntity.TYPE_ERROR);
                    messageEntity.setMessageTime(System.currentTimeMillis());
                    String messageTitle = ApplicationUtils.getContext().getString(titleId);
                    String messageContent = ApplicationUtils.getContext().getString(contentId);
                    messageEntity.setMessageTitle(messageTitle);
                    messageEntity.setMessageContent(messageContent);
                    MessageUtils.addMessage(messageEntity);

                    int faultValue = ((int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, 0)) | OvenDeviceFaultEnum.FAULT_DISCONNECT.value;
                    // 云米插件故障上报 小米固件是从sp里面读取的
                    ReportUtils.reportDoubleIot(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, faultValue);
                    break;
                case OvenBusEventConstants.MSG_COMMUNICATE_CONNECTED:
                    int faultValue1 = ((int) PropertyPreferenceManager.getInstance().getProperty(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, 0)) & (~OvenDeviceFaultEnum.FAULT_DISCONNECT.value);
                    ReportUtils.reportDoubleIot(OvenPropEnum.DEVICE_FAULT.siid, OvenPropEnum.DEVICE_FAULT.piid, faultValue1);
                    OvenUtil.showDeviceFaultDialog(OvenDeviceFaultEnum.FAULT_DISCONNECT.titleId, OvenDeviceFaultEnum.FAULT_DISCONNECT.msgId);
                    break;
                case OvenBusEventConstants.MSG_TO_MCU_UPGRADE:
                    OvenUtil.dismissDeviceFalutDialog();
                    break;
                case OvenBusEventConstants.MSG_THEME_CHANGED:
                    mView.refreshTheme();
                case CommonConstant.MSG_PROPERTY_CHANGE:
                    // 属性设置成功更新界面
                    PropertyEntity propertyEntity = (PropertyEntity) msgObject;
                    Log.i(TAG, "initDisposable:MSG_PROPERTY_CHANGE  propertyEntity: " + propertyEntity);
                    OvensoServiceFactory.getInstance().getOvenService().dealEventChangeFromFirm(propertyEntity);
                    break;
            }
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void cmdLight(boolean on) {
        Log.i(TAG, "cmdLight: " + on);
        OvenSerialManager.getInstance().setLightProperty(on);
    }

    @Override
    public void cmdPannel(boolean on) {
        Log.i(TAG, "cmdPannel: " + on);
        OvenSerialManager.getInstance().setPannelProperty(on);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

}


