package com.viomi.waterpurifier.edison.widget;

import static com.viomi.waterpurifier.edison.WaterConstant.FILTER_ERROR_MARGIN;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.viomi.ovensocommon.CommonPreference;
import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager;
import com.viomi.ovensocommon.spec.WaterPropEnum;
import com.viomi.router.core.ViomiRouter;
import com.viomi.waterpurifier.edison.R;
import com.viomi.waterpurifier.edison.databinding.MessageTipBinding;
import com.viomi.waterpurifier.edison.entity.WaterErrorEntity;
import com.viomi.waterpurifier.edison.manager.MessageDialogManager;
import com.viomi.waterpurifier.edison.ui.WaterMainActivity;
import com.viomi.waterpurifier.edison.util.DeviceFaultErrorParser;

import java.util.List;

/**
 * 消息提示的布局
 */
public class MessageTipLayout extends ConstraintLayout implements View.OnClickListener {
    private static final String TAG = "MessageTipLayout";
    // 串口通信故障
    public final static int TYPE_SERIAL = 101;
    // ro寿命问题
    public final static int TYPE_FILTER_CARBON = 102;
    // 4IN1滤芯寿命问题
    public final static int TYPE_FILTER_INONE = 103;
    // 水箱缺水
    public final static int TYPE_WATER_LACK = 104;
    // 网络错误
    public final static int TYPE_NET_ERROR = 105;

    public final static String TIP_SERIAL = "主控板连接异常";
    public final static String TIP_SERIAL_CONTENT = "请联系客服400-100-2632";

    public final static String TIP_WATER_LACK = "水箱缺水";
    public final static String TIP_WATER_LACK_CONTENT = "请及时补充并装好";
    private final Context mContext;
    MessageTipBinding messageTipBinding;
    private MessageDialogManager messageDialogManager;

    public MessageTipLayout(Context context) {
        this(context, null);
    }

    public MessageTipLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageTipLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mContext = context;
        // 把布局加载父布局里面
        messageTipBinding = DataBindingUtil.inflate(layoutInflater, R.layout.message_tip, this, true);
        // 设置点击事件的监听
        setOnClickListener(this);
    }

    public void showCommonTip(int messageType) {
        Log.i(TAG, "setCommonError: messageType: " + messageType);
        int imgTipResureId = 0;
        String messageTitle = "";
        String messageContent = "";
        if (messageType == TYPE_SERIAL) {
            imgTipResureId = R.drawable.toast_alerm;
            messageTitle = "主控板连接异常";
        } else if (messageType == TYPE_FILTER_INONE) {
            imgTipResureId = R.drawable.toast_alerm;
            messageTitle = "4in1滤芯寿命耗尽";
            int filterLifeOne = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.siid, WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.piid, 0);
            messageContent = filterLifeOne + "%";
        } else if (messageType == TYPE_FILTER_CARBON) {
            imgTipResureId = R.drawable.toast_alerm;
            messageTitle = "碳棒滤芯寿命耗尽";
            int filterLife = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.siid, WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.piid, 0);
            messageContent = filterLife + "%";
        } else if (messageType == TYPE_NET_ERROR) {
            imgTipResureId = R.drawable.toast_alerm;
            messageTitle = "网络未连接";
            messageContent = "去设置";
            messageTipBinding.messagetipContent.setOnClickListener(v -> {
                ViomiRouter.getInstance().build(ViomiRouterConstant.SETTING_CONTAINER)
                        .withString(ViomiRouterConstant.SETTING_KEY_FRAGMENTROUTER, ViomiRouterConstant.SETTING_FRAGMENT_WLAN)
                        .navigation();
            });
        } else if (messageType == TYPE_WATER_LACK) {
            imgTipResureId = R.drawable.toast_alerm;
            messageTitle = TIP_WATER_LACK;
            messageContent = TIP_WATER_LACK_CONTENT;
        }
        messageTipBinding.messagetipImgtip.setImageResource(imgTipResureId);
        messageTipBinding.messagetipTitletip.setText(messageTitle);
        messageTipBinding.messagetipContent.setText(messageContent);
        if (TextUtils.isEmpty(messageContent)) {
            messageTipBinding.messagetipContent.setVisibility(GONE);
            messageTipBinding.messagetipLine.setVisibility(GONE);
        }
        // 显示故障
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
    }

    public void showOtherMsg(String messageContent) {
        messageTipBinding.messagetipImgtip.setImageResource(R.drawable.toast_alerm);
        messageTipBinding.messagetipTitletip.setText(messageContent);
        messageTipBinding.messagetipContent.setText("请处理");
        messageTipBinding.messagetipContent.setTextColor(Color.GREEN);
    }


    @Override
    public void onClick(View v) {
        // 先判断串口是否有故障
        boolean isSerialDisconnect = CommonPreference.getInstance().getSerialDisconnect();
        Log.i(TAG, "onClick: isSerialDisconnect : " + isSerialDisconnect + " messageDialogManager: " + messageDialogManager);
        if (messageDialogManager == null) {
            FragmentActivity fragmentActivity = (WaterMainActivity) mContext;
            messageDialogManager = MessageDialogManager.getInstance();
        }
        if (isSerialDisconnect) {
            messageDialogManager.showSerialDisConnectDialog();
            return;
        }
        // 判断设备故障的逻辑
        // 设备故障
        int equipmentFault = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.EQUIPMENT_FAULT.siid, WaterPropEnum.EQUIPMENT_FAULT.piid, 0);
        Log.i(TAG, "onClick: equipmentFault : " + equipmentFault);
        if (equipmentFault != 0) {
            List<WaterErrorEntity> errorPurList = DeviceFaultErrorParser.getErrorList(equipmentFault);
            if (errorPurList.size() > 1) {
                // 显示单个故障
                messageDialogManager.showMutilFaultsDialog(errorPurList);
            } else {
                // 显示多个故障
                messageDialogManager.showDeviceFaultDialog(errorPurList.get(0));
            }
            return;
        }
        // 滤芯故障
        int inOneLife = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.siid, WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.piid, 0);
        if (inOneLife < FILTER_ERROR_MARGIN) {
            PropertyEntity propertyEntity = new PropertyEntity(WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.siid, WaterPropEnum.FILTER_4IN1_LIFE_LEVEL.piid, inOneLife);
            messageDialogManager.showFilterErrorDialog(propertyEntity);
            return;
        }
        int roLife = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.siid, WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.piid, 0);
        if (roLife < FILTER_ERROR_MARGIN) {
            PropertyEntity propertyEntity = new PropertyEntity(WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.siid, WaterPropEnum.FILTER_CARBON_LIFE_LEVEL.piid, roLife);
            messageDialogManager.showFilterErrorDialog(propertyEntity);
            return;
        }
        // 缺水
        int waterLackStatus = (int) PropertyPreferenceManager.getInstance().getProperty(WaterPropEnum.CISTERN.siid, WaterPropEnum.CISTERN.piid, -1);
        if (waterLackStatus == 1) {
            messageDialogManager.showLackWaterDialog();
        }

    }
}
