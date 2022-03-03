package com.viomi.ovenso.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.viomi.ovenso.OvenApplication;
import com.viomi.ovenso.OvenConstants;
import com.viomi.ovenso.bean.OvenWorkStatusEnum;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovenso.microwave.databinding.FragmentAppointBinding;
import com.viomi.ovenso.serial.OvenSerialManager;
import com.viomi.ovenso.util.OvenTestUtil;
import com.viomi.ovenso.util.TimeUtil;
import com.viomi.ovenso.view.PickerView;
import com.viomi.ovensocommon.BaseDialogFragment;
import com.viomi.ovensocommon.CommonConstant;
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity;
import com.viomi.ovensocommon.rxbus.ViomiRxBus;
import com.viomi.ovensocommon.toast.ViomiToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.disposables.Disposable;
//预约时间为工作结束时间，最长不超过24小时；（时钟时间+此模式下的烹饪时间+5分钟为分界点，
// 大于等于此时间则认为是当天结束，
// 小于此时间则认为是第二天结束；
// 例如：现在要预约一个热风烤，当前北京时间是15:03，热风烤时间为25 min，预约时间以15:03+25+5=15:33为分界点，
// 如果我设定预约时间为15:40，则认为是当,15:40烹饪完成；如果设定时间为15:20，则认为是第二天的15:20烹饪完成；

/**
 * 预约烹饪弹窗
 * 1 、普通模式的预约 和 屏端自定义模式的预约   先下发属性(包含modeId)，再下发 action 传递 dishId 为0( 为了配合插件的逻辑处理) 和 prepareTime 传递参数，
 * 2 、菜谱的预约  由于属性是不变的，固件 和屏端都是写死的属性，不用下发属性  直接下发 action , dishId 和 prepareTime 作为参数传递 来启动
 */
public class AppointFragment extends BaseDialogFragment<FragmentAppointBinding> {
    private static final String TAG = "AppointFragment";
    public static final String KEY_COOK_TIME = "keyCookTime";
    public static final String KEY_DISH_ID = "keyDishId";
    private static final int START_HOUR = 0;
    private static final int START_MINUTE = 0;
    private static final int END_HOUR = 23;
    private static final int END_MINUTE = 59;
    // 最小预约时间
    private static final int MIN_APPOINT_TIME = 5;
    public static final int MINUTE_UNIT = 60;
    private PickerView mDayPickerView;
    private PickerView mHourPickerView;
    private PickerView mMinutePickerView;
    private Calendar appointCalendar;
    private float cookTotalTime = 0;
    private List<PropertyEntity> propertyEntityList;
    private int dishId;
    private int diffMinute = MIN_APPOINT_TIME;
    private Calendar currentTimeCalendar;
    private int diffDay;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_appoint;
    }

    @Override
    protected void initView() {
        Log.i(TAG, "initView: ");
        Context context = viewDataBinding.appointTitle.getContext();
        ConstraintLayout contentLayout = new ConstraintLayout(context);
        contentLayout.setId(R.id.date_content);
        ConstraintLayout.LayoutParams contentParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );
        ConstraintLayout.LayoutParams dayParams = new ConstraintLayout.LayoutParams(
                (282),
                (375)
        );
        ConstraintLayout.LayoutParams hourParams = new ConstraintLayout.LayoutParams(
                (282),
                (375)
        );
        ConstraintLayout.LayoutParams minuteParams = new ConstraintLayout.LayoutParams(
                (282),
                (375)
        );
        mDayPickerView = new PickerView(context);
        mDayPickerView.setId(R.id.date_day);
        mHourPickerView = new PickerView(context, "时");
        mHourPickerView.setId(R.id.date_hour);
        mMinutePickerView = new PickerView(context, "分");
        mMinutePickerView.setId(R.id.date_minute);
        contentParams.topToBottom = R.id.appoint_boundary_layout;
        contentParams.bottomToBottom = R.id.dialog;
        dayParams.leftToLeft = R.id.date_content;
        dayParams.rightToLeft = R.id.date_hour;
        dayParams.topToBottom = R.id.appoint_boundary_layout;
        dayParams.bottomToBottom = R.id.date_content;
        hourParams.leftToRight = R.id.date_day;
        hourParams.rightToLeft = R.id.date_minute;
        hourParams.topToBottom = R.id.appoint_boundary_layout;
        hourParams.bottomToBottom = R.id.date_content;
        minuteParams.leftToRight = R.id.date_hour;
        minuteParams.rightToRight = R.id.date_content;
        minuteParams.topToBottom = R.id.appoint_boundary_layout;
        minuteParams.bottomToBottom = R.id.date_content;
        mDayPickerView.setLayoutParams(dayParams);
        mHourPickerView.setLayoutParams(hourParams);
        mMinutePickerView.setLayoutParams(minuteParams);
        contentLayout.addView(mDayPickerView);
        contentLayout.addView(mHourPickerView);
        contentLayout.addView(mMinutePickerView);
        contentLayout.setLayoutParams(contentParams);
        viewDataBinding.orderGroup.addView(contentLayout);
        // 初始化数据
        initData();
    }

    private void initData() {
        Log.i(TAG, "initData: ");
        appointCalendar = Calendar.getInstance();
        currentTimeCalendar = Calendar.getInstance();
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        cookTotalTime = bundle.getInt(KEY_COOK_TIME);
        dishId = bundle.getInt(KEY_DISH_ID);
        Log.i(TAG, "initData: dishId:" + dishId + "  cookTotalTime: " + cookTotalTime);
        long currentCalendarTime = (long) (appointCalendar.getTimeInMillis() + (cookTotalTime + MIN_APPOINT_TIME) * 60 * 1000);
        appointCalendar.setTimeInMillis(currentCalendarTime);
        initPickerView(appointCalendar);
    }

    /**
     * @param propertyEntityList 由于 propertyEntity 不能序列化，暂时用设置的方式来赋值
     */
    public void setPropertyEntityList(List<PropertyEntity> propertyEntityList) {
        this.propertyEntityList = propertyEntityList;
    }

    private void initPickerView(Calendar appointCalendar) {
        Log.i(TAG, "initPickerView: ");
        // 天数
        ArrayList<String> mDayUnits = new ArrayList<>();
        mDayUnits.add(TimeUtil.getDayTip(appointCalendar));
        mDayPickerView.setDataList(mDayUnits);
        mDayPickerView.setCanScroll(false);
        DecimalFormat mDecimalFormat = new DecimalFormat("00");
        List<String> mHourUnits = new ArrayList<>();
        // 小时
        for (int i = START_HOUR; i <= END_HOUR; i++) {
            mHourUnits.add(mDecimalFormat.format(i));
        }
        mHourPickerView.setDataList(mHourUnits);
        int hour = appointCalendar.get(Calendar.HOUR_OF_DAY);
        Log.i(TAG, "initPickerView: hour: " + hour);
        mHourPickerView.setSelected(hour);
        mHourPickerView.setCanScroll(mHourUnits.size() > 1);
        List<String> mMinuteUnits = new ArrayList<>();
        // 分钟
        for (int i = START_MINUTE; i <= END_MINUTE; i++) {
            mMinuteUnits.add(mDecimalFormat.format(i));
        }
        mMinutePickerView.setDataList(mMinuteUnits);
        int minutes = appointCalendar.get(Calendar.MINUTE);
        Log.i(TAG, "initPickerView: minutes: " + minutes);
        mMinutePickerView.setSelected(minutes);
        mMinutePickerView.setCanScroll(mMinuteUnits.size() > 1);

        String mOrderTime = mDayPickerView.getSelectItem() + TimeUtil.getTimeHHMM(appointCalendar.getTime());
        Log.i(TAG, "initPickerView: mOrderTIme: " + mOrderTime);
        // 更新标题
        String totalFormat = mMinutePickerView.getContext().getResources().getString(R.string.oven_order_date_title);
        viewDataBinding.appointTitle.setText(String.format(totalFormat, mOrderTime));
    }

    @Override
    protected void initListener() {
        Log.i(TAG, "initListener: ");
        mHourPickerView.setOnSelectListener((view, selectedHour) -> {
            Log.i(TAG, "onSelect:" + selectedHour);
            int hour = Integer.parseInt(selectedHour);
            refreshSelectDayInfo(hour, appointCalendar.get(Calendar.MINUTE));
        });
        mMinutePickerView.setOnSelectListener((view, selected) -> {
            Log.i(TAG, "onSelect: " + selected);
            int minute = Integer.parseInt(selected);
            refreshSelectDayInfo(appointCalendar.get(Calendar.HOUR_OF_DAY), minute);
        });
        viewDataBinding.appointSure.setOnClickListener(v -> {
            int currentTimeMinute = currentTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 + currentTimeCalendar.get(Calendar.MINUTE);
            int appointTimeMinute = appointCalendar.get(Calendar.HOUR_OF_DAY) * 60 + appointCalendar.get(Calendar.MINUTE);
            Log.i(TAG, "initListener: currentTimeMinute: " + currentTimeMinute + "  appointTimeMinute: " + appointTimeMinute);
            int diffMinute = (int) (appointTimeMinute - currentTimeMinute - MIN_APPOINT_TIME - cookTotalTime);
            Log.i(TAG, "initListener: diffMinute: " + diffMinute);
            if (diffMinute < 0 && diffDay == 0) {
                ViomiToastUtil.showToastCenter(getString(R.string.appontfragment_timetip));
                return;
            }
            // 判断预约时间 和 当前时间的时间差
            ViomiToastUtil.showToastNormal(getString(R.string.oven_cookparam_isbooking), Toast.LENGTH_SHORT);
            if (dishId == OvenConstants.DISHID_NO_RECIPE) {
                // 模式预约
                // 模式 设置属性 后再 下发 action
                OvenSerialManager.getInstance().writePropertyList(propertyEntityList);
                OvenTestUtil.testPropertySuccess();
            } else {
                // 菜谱预约
                startAppointAction();
            }
        });
        viewDataBinding.appointCancel.setOnClickListener(view -> dismissAllowingStateLoss());
        // 监听属性下发 成功 和  action 下发成功
        Disposable disposable = ViomiRxBus.getInstance().subscribeUi(ovenRxBusEvent -> {
            int msgId = ovenRxBusEvent.getMsgId();
            Log.i(TAG, "initListener: msgId: " + msgId);
            if (msgId == CommonConstant.MSG_DOWNWRITE_SUCCESS) {
                startAppointAction();
                return;
            }
            if (msgId == CommonConstant.MSG_ACTION_SUCCESS) {
                OvenTestUtil.testCookingUI(OvenWorkStatusEnum.BOOKED);
                dismissAllowingStateLoss();
            }
        });
        addDispose(disposable);
    }

    private void startAppointAction() {
        int leftTime = (int) (diffMinute + cookTotalTime);
        if (diffMinute < MIN_APPOINT_TIME) {
            leftTime = 24 * 60 + leftTime;
        }
        Log.i(TAG, "startAppointAction:  diffTIme: " + diffMinute + "   leftTime: " + leftTime);
        OvenSerialManager.getInstance().doAppointAction(dishId, leftTime);
        OvenTestUtil.testActionSuccess();
    }

    private void refreshSelectDayInfo(int hour, int minute) {
        Log.i(TAG, "refreshSelectDayInfo: hour: " + hour + "  minute: " + minute);
        int currentTimeMinute = currentTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 + currentTimeCalendar.get(Calendar.MINUTE);
        Log.i(TAG, "refreshSelectDayInfo: currentTimeMinute: " + currentTimeMinute);
        int currentHour = currentTimeCalendar.get(Calendar.HOUR_OF_DAY);
        Log.i(TAG, "refreshSelectDayInfo: currentHour: " + currentHour);
        int currentMinute = currentTimeCalendar.get(Calendar.MINUTE);
        Log.i(TAG, "refreshSelectDayInfo: currentMinute: " + currentMinute);
        diffMinute = (hour * MINUTE_UNIT + minute) - (currentTimeMinute + (int) cookTotalTime);
        Log.i(TAG, "refreshSelectDayInfo: diffMinute: " + diffMinute);
        diffDay = 0;
        if (diffMinute < MIN_APPOINT_TIME) {
            diffDay = 1;
        }
        appointCalendar.set(Calendar.DAY_OF_YEAR, currentTimeCalendar.get(Calendar.DAY_OF_YEAR) + diffDay);
        appointCalendar.set(Calendar.HOUR_OF_DAY, hour);
        appointCalendar.set(Calendar.MINUTE, minute);
        String day = TimeUtil.getDayTip(appointCalendar);
        mDayPickerView.setDataList(new ArrayList<String>() {{
            add(day);
        }});
        mDayPickerView.setSelected(0);
        String mOrderTime = day + TimeUtil.getTimeHHMM(appointCalendar.getTime());
        Log.i(TAG, "refreshSelectDayInfo: mOrderTime: " + mOrderTime);
        viewDataBinding.appointTitle.setText(String.format(OvenApplication.getContext().getResources().getString(R.string.oven_order_date_title), mOrderTime));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        assert dialog != null;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = (846);
        wlp.height = (458);
        window.setAttributes(wlp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }


}
