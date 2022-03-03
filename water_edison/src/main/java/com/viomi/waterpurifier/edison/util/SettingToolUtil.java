package com.viomi.waterpurifier.edison.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.WIFI_SERVICE;

/**
 * 公共工具类
 * Created by William on 2018/1/20.
 */
public class SettingToolUtil {
    private static final String TAG = SettingToolUtil.class.getSimpleName();

    /**
     * dp 转 px
     */
    public static int dpToPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 根据 Key 获取系统底层属性
     *
     * @return 如果不存在该 key 则返回空字符串
     */
    private static String get(Context context, String key) throws IllegalArgumentException {
        String ret;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi") @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            // 参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);
            // 参数
            Object[] params = new Object[1];
            params[0] = key;
            ret = (String) get.invoke(SystemProperties, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    /**
     * 获取 mac 地址
     */
    public static String getMac() {
        String mac = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return mac;
    }

    /**
     * 是否是闰年
     *
     * @param year year
     * @return 是否是闰年
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }

    /**
     * 判断 Activity 是否正在运行
     *
     * @param name: Activity 名称（如: com.viomi.fridge.vertical.album.activity.AlbumActivity）
     */
    public static boolean isActivityRunning(Context context, String name) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);// 获取栈最大数量
            ActivityManager.RunningTaskInfo task = tasks.get(0);// 获取栈顶
            if (task != null) {
                return TextUtils.equals(task.topActivity.getPackageName(), "com.viomi.fridge.vertical")
                        && TextUtils.equals(task.topActivity.getClassName(), name);
            }
        }
        return false;
    }

    /**
     * 判断某个服务是否正在运行
     *
     * @param serviceName 是包名+ 服务的类名
     * @return true 代表正在运行，false 代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (myAM == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
        if (myList == null || myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 获取版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * int ip 地址格式化
     */
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    public static int getWifiSignalStrength(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();// 当前 WLAN 连接信息
        if (wifiInfo != null && wifiInfo.getIpAddress() != 0) {
            return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
        } else {
            return 0;
        }
    }

    /**
     * 网络是否已连接
     */
    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isConnected() && networkinfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取version code
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 保存一个对象数据
     *
     * @param fileName: 文件名
     * @param object:   数据对象
     */
    public static void saveObject(Context context, String fileName, Object object) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);

            // 将 fos 的数据保存到内核缓冲区
            // 不能确保数据保存到物理存储设备上，如突然断电可能导致文件未保存
            fos.flush();

            // 将数据同步到达物理存储设备
            FileDescriptor fd = fos.getFD();
            fd.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从本地（手机内部存储）读取保存的对象
     *
     * @param filename 文件名称
     */
    public static Object getFileObject(Context context, String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(filename);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            Log.e(TAG, "getObject error,msg=" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 判断是否存在第三方 app
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据包名启动第三方 app
     */
    public static void startOtherApp(Context context, String packageName, boolean isActivity) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (!isActivity && intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 获取 Ip 地址
     */
    public static String getIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
            DhcpInfo di = wifiManager.getDhcpInfo();
            return intToIp(di.ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getSSid(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return color
     */
    public static int getColor(float radio, int[] colorArr, float[] positionArr, int alpha) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < positionArr.length; i++) {
            if (radio <= positionArr[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio, positionArr[i - 1], positionArr[i]);
                return getColorFrom(startColor, endColor, areaRadio, alpha);
            }
        }
        return -1;
    }

    /**
     * 获取某个百分比位置的颜色
     *
     * @param radio 取值[0,1]
     * @return color
     */
    public static int getColor(float radio, int[] colorArr, float[] positionArr) {
        int startColor;
        int endColor;
        if (radio >= 1) {
            return colorArr[colorArr.length - 1];
        }
        for (int i = 0; i < positionArr.length; i++) {
            if (radio <= positionArr[i]) {
                if (i == 0) {
                    return colorArr[0];
                }
                startColor = colorArr[i - 1];
                endColor = colorArr[i];
                float areaRadio = getAreaRadio(radio, positionArr[i - 1], positionArr[i]);
                return getColorFrom(startColor, endColor, areaRadio);
            }
        }
        return -1;
    }

    private static float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor s
     * @param endColor   e
     * @param radio      r
     * @return color
     */
    private static int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     */
    private static int getColorFrom(int startColor, int endColor, float radio, int alpha) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(alpha, red, greed, blue);
    }

    /**
     * 获取图片宽高
     */
    public static int[] getImageSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /*
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的 bitmap 为 null
        /*
         *options.outHeight 为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * 返回 app 运行状态
     * 1: 程序在前台运行
     * 2: 程序在后台运行
     * 3: 程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppStatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
        // 判断程序是否在栈顶
        if (list.get(0).topActivity.getPackageName().equals(pageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(pageName)) {
                    return 2;
                }
            }
            return 3;// 栈里找不到，返回 3
        }
    }

    public static boolean pingNet() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                throw new Exception("pingNet should not use in MainThread!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String result = null;
        String[] ipArray = new String[]{
                "www.meizu.com",
                "www.baidu.com",
                "www.qq.com"
        };
        for (int i = 0; i < 3; i++) {
            try {
                Process p = Runtime.getRuntime().exec("ping -c 3 -w 5 " + ipArray[i]);// ping 网址 3 次
                // ping的状态
                int status = p.waitFor();
                if (status == 0) {
                    result = "success";
                    return true;
                } else {
                    result = "failed";
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                result = "IOException";
            } catch (InterruptedException e) {
                result = "InterruptedException";
            } finally {
                Log.e(TAG, "pingNet: " + ipArray[i] + ":result = " + result);
            }
        }
        return false;
    }

    public static String formatTimeString(int time) {
        int minute = time / 60;
        int second = time % 60;
        return minute + ":" + (second < 10 ? "0" + second : second);
    }

    /**
     * 获取外网的 IP（要访问 Url，要放到后台线程里处理）
     */
    public static String getNetIp() {
        URL infoUrl;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(stringBuilder.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipLine;
    }

    /**
     * 判断 Release 和 Debug 版本
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 隐藏和显示输入法
     */
    public static void showOrHideKeyboard(Context context, View view, boolean isShow) {
        if (context == null || view == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) {
            return;
        }
        if (isShow) {
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 判断字符串是否包含中文
     */
    public static boolean isChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

//    /**
//     * 获取小米标识
//     */
//    public static SettingMiIdentification getMiIdentification() {
//        SettingMiIdentification miIdentification = new SettingMiIdentification();
//        miIdentification.setMac("7C:49:EB:0F:42:82");
//        miIdentification.setDeviceId("85396802");
//        miIdentification.setMiToken("vGP581G2tSvzelnY");
//
//
//        String sn = get(SettingManager.getInstance().getContext(), "gsm.serial");
//        if (sn == null) {
//            Log.e(TAG, "sn code is null");
//            return miIdentification;
//        } else {
//            Log.d(TAG, "sn = " + sn + ",length = " + sn.length());
//        }
//        String[] list;
//        if (sn.length() >= 24 && (!sn.contains("|"))) {
//            list = new String[2];
//            list[0] = sn.substring(0, 8);
//            list[1] = sn.substring(8, 24);
//        } else {
//            list = sn.split("\\|");
//            if (list.length < 2 || list[1].length() < 16) {
//                Log.e("getMiIdentification", "error,sn=" + sn);
//                return miIdentification;
//            }
//        }
//        miIdentification.setMac(getMac());
//        miIdentification.setDeviceId(list[0]);
//        miIdentification.setMiToken(list[1].substring(0, 16));
//        return miIdentification;
//    }

}
