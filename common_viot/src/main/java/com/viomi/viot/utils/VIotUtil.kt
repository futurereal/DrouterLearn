package com.viomi.viot.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.TextUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * 公共工具类
 * Created by William on 2020/5/14.
 */
object VIotUtil {

    fun getMacAddress(): String {
        val interfaces: List<NetworkInterface?>?
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                if (networkInterface != null && !TextUtils.isEmpty(networkInterface.name)) {
                    if ("wlan0".equals(networkInterface.name, ignoreCase = true)) {
                        val macBytes: ByteArray = networkInterface.hardwareAddress
                        if (macBytes.isNotEmpty()) {
                            val str = StringBuilder()
                            for (b in macBytes) {
                                str.append(String.format("%02X:", b))
                            }
                            if (str.isNotEmpty()) {
                                str.deleteCharAt(str.length - 1)
                            }
                            return str.toString()
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "unknown"
    }

    fun isNetworkEnable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = manager?.activeNetworkInfo
        return networkInfo?.isConnected == true && networkInfo.isAvailable
    }

    fun getIpAddress(context: Context): String? {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val dhcpInfo = wifiManager?.dhcpInfo
            intToIp(dhcpInfo?.ipAddress ?: 0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getMaskAddress(interfaceName: String): String {
        try {
            val networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces() // 获取本机所有的网络接口
            while (networkInterfaceEnumeration.hasMoreElements()) { // 判断 Enumeration 对象中是否还有数据
                val networkInterface =
                    networkInterfaceEnumeration.nextElement() // 获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp && interfaceName != networkInterface.displayName) { // 判断网口是否在使用，判断是否时我们获取的网口
                    continue
                }
                for (interfaceAddress in networkInterface.interfaceAddresses) {
                    if (interfaceAddress.address is Inet4Address) { // 仅仅处理 ipv4
                        return calcMaskByPrefixLength(interfaceAddress.networkPrefixLength.toInt()) // 获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "error"
    }

    private fun calcMaskByPrefixLength(length: Int): String {
        val mask = -0x1 shl 32 - length
        val partsNum = 4
        val bitsOfPart = 8
        val maskParts = IntArray(partsNum)
        val selector = 0x000000ff
        for (i in maskParts.indices) {
            val pos = maskParts.size - 1 - i
            maskParts[pos] = mask shr i * bitsOfPart and selector
        }
        var result = ""
        result += maskParts[0]
        for (i in 1 until maskParts.size) {
            result = result + "." + maskParts[i]
        }
        return result
    }

    fun getGateway(): String {
        val arr: Array<String>
        try {
            val process = Runtime.getRuntime().exec("ip route list table 0")
            BufferedReader(InputStreamReader(process.errorStream))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            val string: String = `in`.readLine()
            arr = string.split("\\s+".toRegex()).toTypedArray()
            return arr[2]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "error"
    }

    fun get(context: Context, key: String): String {
        var ret: String
        try {
            val classLoader = context.classLoader
            val systemProperties = classLoader.loadClass("android.os.SystemProperties")
            val paramTypes: Array<Class<*>?> = arrayOfNulls(1)
            paramTypes[0] = String::class.java
            val get = systemProperties.getMethod("get", *paramTypes)
            val params = arrayOfNulls<Any>(1)
            params[0] = key
            ret = get.invoke(systemProperties, *params) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: java.lang.Exception) {
            ret = ""
        }
        return ret
    }

    private fun intToIp(paramInt: Int): String {
        return "${paramInt and 0xFF}.${0xFF and (paramInt shr 8)}.${0xFF and (paramInt shr 16)}.${0xFF and (paramInt shr 24)}"
    }

    internal fun isServiceWork(context: Context, serviceName: String): Boolean {
        var isWork = false
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return false
        val list = activityManager.getRunningServices(Int.MAX_VALUE)
        if (list?.isNullOrEmpty() == true) return false
        for (i in list.indices) {
            val name = list[i].service.className
            if (name == serviceName) {
                isWork = true
                break
            }
        }
        return isWork
    }

    fun isSubDeviceMsg(topic: String?): Boolean {
        val str = topic?.split("/")
        return if ((str?.size ?: 0) < 5) false else str?.get(1) != str?.get(3)
    }
}
