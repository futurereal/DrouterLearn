package com.viomi.iotdevice.main.fridge_demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.viomi.iotdevice.IotSerialConfig;
import com.viomi.iotdevice.R;
import com.viomi.iotdevice.ViomiIotManager;
import com.viomi.iotdevice.common.callback.CommonCallback;
import com.viomi.iotdevice.common.callback.IotSerialCallback;
import com.viomi.iotdevice.common.callback.ProgressCallback;
import com.viomi.iotdevice.common.protocol.EventPack;
import com.viomi.iotdevice.common.util.RxSchedulerUtil;
import com.viomi.iotdevice.iottomcu.bean.ViotGetPropResponseBody;
import com.viomi.iotdevice.iottomcu.bean.ViotProperty;
import com.viomi.iotdevice.iottomcu.bean.ViotSetPropRequestBody;
import com.viomi.iotdevice.main.iot_device_lib.util.ToolUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author lzw_9
 */
public class Fridge15_6DemoActivity extends AppCompatActivity {
    private static final String TAG = "Fridge15_6DemoActivity";
    private static final int READ_WRITE_STORAGE = 100;

    @BindView(R.id.tvPropTitle)
    TextView tvPropTitle;
    @BindView(R.id.tvTitleQuickCool)
    TextView tvTitleQuickCool;
    @BindView(R.id.tvSetQuickCoolTmp)
    TextView tvSetQuickCoolTmp;
    @BindView(R.id.barQuickCool)
    SeekBar barQuickCool;
    @BindView(R.id.llBarQuickCool)
    LinearLayout llBarQuickCool;
    @BindView(R.id.tvQuickCoolSwitch)
    TextView tvQuickCoolSwitch;
    @BindView(R.id.tvQuickCoolTemp)
    TextView tvQuickCoolTemp;
    @BindView(R.id.tvQuickCool)
    TextView tvQuickCool;
    @BindView(R.id.llQuickCool)
    LinearLayout llQuickCool;
    @BindView(R.id.tvTitleQuickFreeze)
    TextView tvTitleQuickFreeze;
    @BindView(R.id.tvSetQuickFreezeTmp)
    TextView tvSetQuickFreezeTmp;
    @BindView(R.id.barQuickFreeze)
    SeekBar barQuickFreeze;
    @BindView(R.id.llBarQuickFreeze)
    LinearLayout llBarQuickFreeze;
    @BindView(R.id.tvQuickFreezeSwitch)
    TextView tvQuickFreezeSwitch;
    @BindView(R.id.tvQuickFreezeTemp)
    TextView tvQuickFreezeTemp;
    @BindView(R.id.tvQuickFreeze)
    TextView tvQuickFreeze;
    @BindView(R.id.llQuicFreeze)
    LinearLayout llQuicFreeze;
    @BindView(R.id.tvSmart)
    TextView tvSmart;
    @BindView(R.id.tvErrNum)
    TextView tvErrNum;
    @BindView(R.id.tvRxErrNum)
    TextView tvRxErrNum;
    @BindView(R.id.tvBusyNum)
    TextView tvBusyNum;
    @BindView(R.id.tvDataErrNum)
    TextView tvDataErrNum;
    @BindView(R.id.tvTimeOutNum)
    TextView tvTimeOutNum;
    @BindView(R.id.tvHoliday)
    TextView tvHoliday;
    @BindView(R.id.tvManual)
    TextView tvManual;
    @BindView(R.id.tvBarFreezeNum)
    TextView tvBarFreezeNum;
    @BindView(R.id.tvBarCoolNum)
    TextView tvBarCoolNum;
    @BindView(R.id.tvPropInfo)
    TextView tvPropInfo;
    @BindView(R.id.tvEventInfo)
    TextView tvEventInfo;
    @BindView(R.id.btnOta)
    Button btnOta;
    @BindView(R.id.btnGetProp)
    Button btnGetProp;
    @BindView(R.id.otaProgress)
    ProgressBar otaProgress;

    private Disposable disposableGetProp;
    private int coolNum = 2, freezeNum = -15;
    boolean barQuickCooling = false, barQuickFreezing = false;
    boolean barQuickCoolUser = false, barQuickFreezeUser = false;
    long errTotalCount = 0, dataCount = 0, busyCount = 0, timeOutCount = 0, rxErrCount = 0, dataErrCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge15_6_demo);
        ButterKnife.bind(this);
        initView();
        initData();
        checkPermission();
    }

    private void initView() {
        //冰箱冷藏调温范围：2~8℃   冰箱冷冻调温范围：-15~-23℃   智能模式下：冷藏4℃，冷冻-18℃  假日模式下：冷藏8℃，冷冻-15℃；速冷是冷藏显示2℃，速冻是冷冻显示-23℃
        barQuickCool.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//范围2~8
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tempNum = (int) (2 + (8.0 - 2) / 100 * progress);
                barQuickCoolUser = fromUser;
                Log.w(TAG, "barQuickCool progress=" + progress + " tempNum = " + tempNum + " fromUser = " + fromUser);
                tvBarCoolNum.setText("滑动条对应温度:" + tempNum + "℃");
                if (tempNum != coolNum) {
                    coolNum = tempNum;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "barQuickCool onStartTrackingTouch");
                barQuickCooling = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "barQuickCool onStopTrackingTouch coolNum=" + coolNum);
                barQuickCooling = false;
                if (barQuickCoolUser) {
                    setprop(initSetReqBody(3, 2, coolNum));
                }
            }
        });
        barQuickFreeze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//-15~-23℃
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int tempNum = -15 - (int) ((23.0 - 15) / 100 * progress);
                barQuickFreezeUser = fromUser;
                Log.w(TAG, "barQuickFreeze progress=" + progress + " tempNum = " + tempNum + " fromUser = " + fromUser);
                tvBarFreezeNum.setText("滑动条对应温度:" + tempNum + "℃");
                if (tempNum != freezeNum) {
                    freezeNum = tempNum;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "barQuickFreeze onStartTrackingTouch");
                barQuickFreezing = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "barQuickFreeze onStopTrackingTouch freezeNum = " + freezeNum);
                barQuickFreezing = false;
                if (barQuickFreezeUser) {
                    setprop(initSetReqBody(4, 2, freezeNum));
                }
            }
        });
    }

    private void initData() {
        ViomiIotManager.Companion.getInstance().setHandler(new IotSerialCallback() {
            @Override
            public void props(Map prop, String serialData) {
                //屬性上报
                for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) prop.entrySet()) {
                    Log.d(TAG, "prop，name=" + entry.getKey() + ",value=" + entry.getValue());
                    DevProp.savePropValue(entry.getKey(), entry.getValue());
                    refreshUI(false);//
                }
            }

            @Override
            public void event(EventPack pack, String serialData) {
                //事件上报
                Log.d(TAG, "event，name=" + pack.getName());
                runOnUiThread(() -> tvEventInfo.setText("事件上报：" + serialData));
            }

            @Override
            public void json_send(String json) {
                //json格式數據上报，仅适用于miot
                Log.d(TAG, "json send=" + json);
            }

            @Override
            public void model(String model) {
                Log.d(TAG, "model=" + model);
            }

            @Override
            public void mcu_version(String version) {
                Log.d(TAG, "mcu_version=" + version);
            }

            @Override
            public void enterTestMode() {

            }
        });
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mac = info.getMacAddress();
        } catch (Exception e) {
            Log.e("getLocalMacAddress", "fail,msg=" + e.getMessage());
        }
        IotSerialConfig.Builder builder = new IotSerialConfig.Builder();
        builder.setIotType(IotSerialConfig.IotType.PROTOCOL_VIOT);
        builder.setMcuSerialBaudRate(115200);
        builder.setMcuSerialPath("cndlcd".equals(ToolUtil.getFridgeFactory()) ? "/dev/ttyS0" : "/dev/ttyMT2");//CND
        builder.setDebugEnv(true);
        builder.setMac(mac);
        IotSerialConfig config = new IotSerialConfig(builder);
        ViomiIotManager.Companion.getInstance().enableLog(true);
        ViomiIotManager.Companion.getInstance().init(this, config);
    }

    private void startGetProp() {
        //dataCount = errTotalCount = busyCount = timeOutCount = rxErrCount = dataErrCount = 0;
        showErrInfo();
        disposableGetProp = Observable.interval(3000, TimeUnit.MILLISECONDS)
                .compose(RxSchedulerUtil.INSTANCE.schedulerTransformer5())
                .onTerminateDetach()
                .subscribe(aLong -> {
                    getprop();
                    btnGetProp.setText("getprop 每3秒一次");
                });
    }

    private void endGetProp() {
        //dataCount = errTotalCount = busyCount = timeOutCount = rxErrCount = dataErrCount = 0;
        showErrInfo();
        if (disposableGetProp != null && !disposableGetProp.isDisposed()) {
            disposableGetProp.dispose();
            disposableGetProp = null;
            btnGetProp.setText("getprop 已暂停");
        }
    }

    /***
     * 获取设备属性
     */
    public void getprop() {
        if (ViomiIotManager.Companion.getInstance().isSerialBusy()) {
            Log.e(TAG, " IotToMcuSerialManager getprop busy");
            return;
        }
        ViomiIotManager.Companion.getInstance().getProperties(DevProp.getPropBody(), new CommonCallback() {

            @Override
            public void onReceiveResult(@Nullable Integer resultCode, @Nullable Object result, @Nullable String desc) {
                Log.e(TAG, "IotToMcuSerialManager getprop resultCode = " + resultCode);
                dataCount++;
                if (resultCode == 0) {
                    for (ViotProperty prop : ((ViotGetPropResponseBody) result).getPropList()) {
                        if (prop.getCode() == 0) {
                            DevProp.savePropValue(prop.getSid() + "." + prop.getPid(), prop.getValue());
                        }
                    }
                    refreshUI(true);
                } else {
                    runOnUiThread(() -> {
                        errTotalCount++;
                        tvPropTitle.setText("getProp属性展示");
                        if (resultCode == -97) {
                            busyCount++;
                            tvPropTitle.setText("getProp属性展示---异常:指令繁忙");
                        } else if (resultCode == -98) {
                            tvPropTitle.setText("getProp属性展示---异常:串口异常");
                        } else if (resultCode == -99) {
                            dataErrCount++;
                            tvPropTitle.setText("getProp属性展示---异常:数据返回格式异常");
                        } else if (resultCode == -100) {
                            timeOutCount++;
                            tvPropTitle.setText("getProp属性展示---异常:超时");
                        } else if (resultCode == -101) {
                            tvPropTitle.setText("getProp属性展示---异常:固件升级");
                        } else if (resultCode == -102) {
                            rxErrCount++;
                            tvPropTitle.setText("getProp属性展示---异常:Rx断开");
                        }
                        tvPropTitle.setTextColor(Color.parseColor("#FF0000"));
                        showErrInfo();
                    });
                }
            }
        });
    }

    public void refreshUI(final boolean isGetProp) {//
        runOnUiThread(() -> {
            //冷藏室
            if (isGetProp) {
                tvPropTitle.setText("getProp属性展示");
            } else {
                tvPropTitle.setText("属性主动上报展示");
            }
            tvPropTitle.setTextColor(Color.parseColor("#ffffff"));
            tvTitleQuickCool.setText("冷藏室:" + DevProp.fridgetemperature_3_1 + "℃");
            tvSetQuickCoolTmp.setText("设置温度:" + DevProp.targettemperature_3_2 + "℃");
            tvQuickCoolSwitch.setSelected(DevProp.on_3_3);
            tvQuickCool.setSelected(DevProp.mode_3_4);
            if (!barQuickCooling && coolNum != DevProp.targettemperature_3_2) {
                coolNum = DevProp.targettemperature_3_2;
                tvBarCoolNum.setText("滑动条对应温度:" + coolNum + "℃");
                barQuickCool.setProgress((int) ((coolNum - 2) * 100 / (8.0 - 2)));
                Log.w(TAG,
                        "IotToMcuSerialManager 更新barQuickCool num=" + coolNum + " progress=" + (int) ((coolNum - 2) * 100 / (8.0 - 2)));
            }
            //冷冻室
            tvTitleQuickFreeze.setText("冷冻室:" + DevProp.temperatureanother_4_1 + "℃");
            tvSetQuickFreezeTmp.setText("设置温度:" + DevProp.targettemperatureanother_4_2 + "℃");
            //tvQuickFreezeSwitch.setSelected(DevProp.onanother_4_3);
            tvQuickFreeze.setSelected(DevProp.mode_4_4);
            if (!barQuickFreezing && freezeNum != DevProp.targettemperatureanother_4_2) {
                freezeNum = DevProp.targettemperatureanother_4_2;
                tvBarFreezeNum.setText("滑动条对应温度:" + freezeNum + "℃");
                barQuickFreeze.setProgress((int) ((-15 - freezeNum) * 100 / (23.0 - 15)));
                Log.w(TAG,
                        "IotToMcuSerialManager 更新barQuickFreeze num=" + freezeNum + " progress=" + (int) ((-15 - freezeNum) * 100 / (23.0 - 15)));
            }
            //模式
            tvManual.setSelected(DevProp.mode_2_4 == 0);
            tvSmart.setSelected(DevProp.mode_2_4 == 1);
            tvHoliday.setSelected(DevProp.mode_2_4 == 2);
            //属性全部展示
            String text = "故障= " + DevProp.fault_5_1;
            text += "\nModel= " + DevProp.model_1_2;
            text += "\nMCU固件版本= " + DevProp.firmwarerevision_1_4;
            text += "\n模式= " + DevProp.mode_2_4;
            text += "\n环境温度= " + DevProp.indoor_temperature_2_7;
            //
            text += "\n冷藏室温度= " + DevProp.fridgetemperature_3_1;
            text += "\n冷藏室设定温度= " + DevProp.targettemperature_3_2;
            text += "\n冷藏室开关= " + (DevProp.on_3_3 ? "开" : "关");
            //
            text += "\n冷冻室温度= " + DevProp.temperatureanother_4_1;
            text += "\n冷冻室设定温度= " + DevProp.targettemperatureanother_4_2;
            //
            text += "\n速冷= " + (DevProp.mode_3_4 ? "开" : "关");
            text += "\n速冻= " + (DevProp.mode_4_4 ? "开" : "关");
            //
            text += "\n化霜传感器温度= " + DevProp.frosttemp_6_1;
            text += "\n强制化霜= " + (DevProp.forcedfrost_6_2 ? "开" : "关");
            text += "\n强制制冷/强制不停机= " + (DevProp.forcenonstop_6_3 ? "开" : "关");
            //text += "\n翻转梁加热丝= " + (DevProp.heatingwire_6_4 ? "开" : "关");
            text += "\n缩时= " + (DevProp.timecut_6_5 ? "开" : "关");
            text += "\n风机转速= " + (DevProp.fan_speed_6_7);
            text += "\n风机状态= " + (DevProp.fan_status_6_8 ? "开" : "关");
            text += "\n自检= " + (DevProp.self_check_6_9 ? "开" : "关");
            text += "\n商检= " + (DevProp.commodity_check_6_10 ? "开" : "关");
            tvPropInfo.setText(text);
            //
            showErrInfo();
        });
    }

    public void showErrInfo() {
        if (dataCount > 24 * 60 * 20) {
            dataCount = errTotalCount = busyCount = timeOutCount = rxErrCount = dataErrCount = 0;
        }
        tvErrNum.setVisibility(dataCount == 0 ? View.GONE : View.VISIBLE);
        tvErrNum.setText("失败:" + errTotalCount + "/" + dataCount);
        tvBusyNum.setText(busyCount == 0 ? "" : "繁忙：" + busyCount);
        tvDataErrNum.setText(dataErrCount == 0 ? "" : "错数据：" + dataErrCount);
        tvTimeOutNum.setText(timeOutCount == 0 ? "" : "超时：" + timeOutCount);
        tvRxErrNum.setText(rxErrCount == 0 ? "" : "断连：" + rxErrCount);
    }

    public ViotSetPropRequestBody initSetReqBody(int sid, int pid, Object value) {
        ViotSetPropRequestBody body = new ViotSetPropRequestBody();
        ViotProperty propMode = new ViotProperty();
        propMode.setSid(sid);
        propMode.setPid(pid);
        propMode.setValue(value);
        body.getPropList().add(propMode);
        return body;
    }

    /***
     * 设置设备属性
     */
    public void setprop(ViotSetPropRequestBody requestBody) {
        ViomiIotManager.Companion.getInstance().setProperties(requestBody,
                (resultCode, result, desc) -> {
                    Log.e(TAG, "IotToMcuSerialManager setprop resultCode = " + resultCode);
                    runOnUiThread(() -> {
                        dataCount++;
                        if (resultCode != 0) {
                            errTotalCount++;
                            Toast.makeText(Fridge15_6DemoActivity.this, "设置失败", Toast.LENGTH_SHORT);
                            if (resultCode == -97) {
                                busyCount++;
                            } else if (resultCode == -99) {
                                dataErrCount++;
                            } else if (resultCode == -100) {
                                timeOutCount++;
                            } else if (resultCode == -102) {
                                rxErrCount++;
                            }
                        } else {
                            Toast.makeText(Fridge15_6DemoActivity.this, "设置成功", Toast.LENGTH_SHORT);
                        }
                        showErrInfo();
                    });
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endGetProp();
        ViomiIotManager.Companion.getInstance().close();
    }

    @OnClick({R.id.tvQuickCoolSwitch, R.id.tvQuickCool, R.id.tvQuickFreezeSwitch, R.id.tvQuickFreeze, R.id.tvSmart,
            R.id.tvHoliday, R.id.tvManual, R.id.factory_test_self_checking, R.id.factory_test_frost_force,
            R.id.factory_test_fan_status, R.id.btnOta,
            R.id.factory_test_rcf_force, R.id.factory_test_heater, R.id.factory_time_cut,
            R.id.factory_test_shang_check, R.id.btnGetProp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvQuickCoolSwitch://开关冷藏室
                setprop(initSetReqBody(3, 3, view.isSelected() ? 0 : 1));
                break;
            case R.id.tvQuickCool://速冷模式
                setprop(initSetReqBody(3, 4, view.isSelected() ? 0 : 1));
                break;
            case R.id.tvQuickFreezeSwitch://开关速冻室
                setprop(initSetReqBody(4, 3, view.isSelected() ? 0 : 1));
                break;
            case R.id.tvQuickFreeze://速冻模式
                setprop(initSetReqBody(4, 4, view.isSelected() ? 0 : 1));
                break;
            case R.id.tvSmart://智能
                setprop(initSetReqBody(2, 4, 1));
                break;
            case R.id.tvHoliday://假日
                setprop(initSetReqBody(2, 4, 2));
                break;
            case R.id.tvManual://手动
                setprop(initSetReqBody(2, 4, 0));
                break;
            case R.id.factory_test_self_checking:
                setprop(initSetReqBody(6, 9, DevProp.self_check_6_9 ? 0 : 1));
                break;
            case R.id.factory_test_frost_force:
                setprop(initSetReqBody(6, 2, DevProp.forcedfrost_6_2 ? 0 : 1));
                break;
            case R.id.factory_test_fan_status:
                setprop(initSetReqBody(6, 8, DevProp.fan_status_6_8 ? 0 : 1));
                break;
            case R.id.factory_test_rcf_force:
                setprop(initSetReqBody(6, 3, DevProp.forcenonstop_6_3 ? 0 : 1));
                break;
            case R.id.factory_test_heater:
                //setprop(initSetReqBody(6, 4, DevProp.heatingwire_6_4 ? 0 : 1));
                break;
            case R.id.factory_time_cut:
                setprop(initSetReqBody(6, 5, DevProp.timecut_6_5 ? 0 : 1));
                break;
            case R.id.factory_test_shang_check:
                setprop(initSetReqBody(6, 10, DevProp.commodity_check_6_10 ? 0 : 1));
                break;
            case R.id.btnGetProp:
                if (disposableGetProp == null) {
                    startGetProp();
                } else {
                    endGetProp();
                }
                break;
            case R.id.btnOta:// adb push ota.bin sdcard/mcu/ota.bin
                endGetProp();
                try {
                    ViomiIotManager.Companion.getInstance().otaStart("/sdcard/mcu/ota.bin", new ProgressCallback() {
                        @Override
                        public void onResult(boolean isProcessing, int progress, String desc) {
                            Log.i(TAG, "mcuOta，isProcessing  =" + isProcessing + ",progress=" + progress);
                            otaProgress.setVisibility(isProcessing ? View.VISIBLE : View.INVISIBLE);
                            otaProgress.setProgress(progress >= 0 ? progress : 0);
                            if (!isProcessing && progress == 100) {
                                Log.i(TAG, "mcuOta success!");
                            } else if (progress <= 0) {
                                Log.i(TAG, "mcuOta fail,msg=" + desc);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.i(TAG, "ota fail，msg =" + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(Fridge15_6DemoActivity.this, "ota 异常", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(READ_WRITE_STORAGE)
    public void checkPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.w(TAG, "已获得权限");
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要获取读写本地数据的权限",
                    READ_WRITE_STORAGE, perms);
        }
    }
}
