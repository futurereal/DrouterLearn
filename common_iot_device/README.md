#  云米VIOT SDK

## 初始化接口和通用配置
1.Iot初始化方法和配置统一由 ViomiIotManager 提供，初始化可以在任意地方进行调用，方法如下：
```
IotSerialConfig.Builder builder = new IotSerialConfig.Builder();
IotSerialConfig config = new IotSerialConfig(builder);
ViomiIotManager.getInstance().init(this, config);
```
2.打印调试日志，需要在初始化前调用如下方法：
```
ViomiIotManager.getInstance().enableLog(true);
```
3.相关配置说明如下：
```
// 设置Iot SDK使用的协议，目前支持 PROTOCOL_MIOT，PROTOCOL_MIOT_SPEC，PROTOCOL_VIOT 三种。
// 默认是 PROTOCOL_MIOT
builder.setIotType(IotSerialConfig.IotType.PROTOCOL_VIOT);
// 设置Iot SDK内部逻辑Http请求调用环境，false 为正式环境，true 为测试环境
// 默认是 false
builder.setDebugEnv(true);
// 设置设备的 did
builder.setDid("");
// 设置设备的 mac
builder.setMac("");
// 设置设备和MCU串口通信的波特率，不设置此项不会初始化和MCU的串口通信
builder.setMcuSerialBaudRate(115200);
// 设置设备和MCU串口通信的串口路径，不设置此项不会初始化和MCU的串口通信
builder.setMcuSerialPath("");
// 设置设备和WiFi模块通信的波特率，不设置此项不会初始化和WiFi模块的串口通信
builder.setWifiSerialBaudRate(115200);
// 设置设备和WiFi模块通信的串口路径，不设置此项不会初始化和WiFi模块的串口通信
builder.setWifiSerialPath("");
```
4.可以在合适地方通过调用如下方法关闭Iot所有的串口通信：
```
ViomiIotManager.getInstance().close();
```
## MCU 到 Android 屏端串口通信
待补充
## Android 屏端到 WiFi 模组串口通信
在SDK初始化时设置了builder.setWifiSerialBaudRate()和builder.setWifiSerialPath()后，就会自动打开屏端和WiFI模组的串口通信。
#### 1.向 WiFi 模组发送串口数据
如果需要给 WiFi 模组发送串口数据，可以调用如下方法，参数为 String 类型，格式需要遵循 Viot 协议：
```
IotToWiFiSerialManager.getInstance().setCommand();
```
## Mesh 组网
Mesh组网分为手机App扫描WiFi模组给Android屏端配网和手机App扫描屏端二维码给WiFi模组配网两种。使用Mesh组网需要打开屏端和WiFI模组的串口通信。<br>1.组网的相关监听回调设置如下：
```
ViomiIotManager.getInstance().setMeshCallback(new IotMeshCallback() {

            @Override
            public void onWiFiInfoCallback(String ssid, String password) {
                // 手机给 WiFi 模组配网时 WiFi 相关信息回调
            }

            @Override
            public void onMeshQRRefresh(boolean isSuccess, String url) {
                // 屏端配网二维码生成回调
            }

            @Override
            public void onDeviceToWiFiMeshResult(boolean isSuccess, DeviceToWiFiMeshEntity entity) {
                // 手机App扫描屏端二维码给WiFi模组配网结果回调
            }

            @Override
            public void onWiFiToDeviceMeshResult(boolean isSuccess, WiFiToDeviceMeshEntity entity) {
                // 手机App扫描WiFi模组给Android屏端配网结果回调
            }
        });
 ```
2.可以在合适时机调用如下方法移除组网监听：
 ```
ViomiIotManager.getInstance().removeMesh();
 ```
3.生成屏端配网二维码方法如下：
```
ViomiIotManager.getInstance().getDeviceToWiFiMeshQR();
```

    