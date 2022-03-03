package com.viomi.viot.utils.encrypt

import android.util.Base64
import com.viomi.viot.config.VIotDeviceConfig
import com.viomi.viot.https.VIotClient
import com.viomi.viot.utils.LogUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 请求 Header 加密工具类
 * Created by William on 2020/5/13.
 */
object EncryptUtil {
    private val TAG = EncryptUtil::class.java.simpleName

    fun headerEncode(request: Request, builder: Request.Builder, config: VIotDeviceConfig) {
        val viomiAccessKey = if (!config.hasTriad) VIotClient.instance.pAppKey else config.deviceId
        val viomiTimeStamp = System.currentTimeMillis().toString()
        val viomiNoise = UUID.randomUUID().toString()

        val signatureMap: MutableMap<String, Any?> = HashMap()
        signatureMap["VIOMI-Access-Key"] = viomiAccessKey
        signatureMap["VIOMI-Timestamp"] = viomiTimeStamp
        signatureMap["VIOMI-Noise"] = viomiNoise
        if (!config.token.isNullOrEmpty()) {
            val authorizationV1 = config.token
            signatureMap["Authorization_v1"] = authorizationV1
        }

        val queryParameterNames = request.url.queryParameterNames
        for (queryParameterName in queryParameterNames) {
            signatureMap[queryParameterName] = request.url.queryParameterValues(queryParameterName)
        }

        val sortMap: MutableMap<String, Any?> = TreeMap { obj: String, anotherString: String? -> obj.compareTo(anotherString!!) }
        sortMap.putAll(signatureMap)

        val stringBuilder = StringBuilder()
        stringBuilder.append(request.method.uppercase(Locale.getDefault())).append(encodeRFC3986("?"))
        for ((key, value) in sortMap) {
            stringBuilder.append(encodeRFC3986(key))
                    .append("=")
                    .append(encodeRFC3986(value.toString()))
                    .append("&")
        }

        val viomiSignature = digitalSignature(stringBuilder.substring(0, stringBuilder.length - 1), if (!config.hasTriad) VIotClient.instance.pAppSecretKey else config.deviceAccessKey)
        LogUtil.d(TAG, "VIOMI-Signature: $viomiSignature.")

        val requestBody = request.body
        val buffer = Buffer()
        val charset = StandardCharsets.UTF_8
        try {
            requestBody?.writeTo(buffer)
            val bodyStr = buffer.readString(charset)
            LogUtil.d(TAG, "AES encode Body String: $bodyStr.")
            if (bodyStr.isNotEmpty()) {
                val encodeData = AESUtil.aesEncode(bodyStr, if (!config.hasTriad) VIotClient.instance.pAppSecretKey else config.deviceAccessKey)
                LogUtil.d(TAG, "AES encode result: $encodeData.")
                builder.method(request.method, encodeData.toRequestBody("application/json; charset=UTF-8".toMediaType()))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        viomiAccessKey?.let { builder.addHeader("VIOMI-Access-Key", it) }
        builder.addHeader("VIOMI-Timestamp", viomiTimeStamp)
        builder.addHeader("VIOMI-Noise", viomiNoise)
        builder.addHeader("VIOMI-Signature", viomiSignature)
        config.token?.let { builder.addHeader("Authorization_v1", it) }
    }

    private fun encodeRFC3986(value: String?): String {
        return try {
            if (value != null) URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~") else ""
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }

    private fun digitalSignature(data: String, appSecretKey: String?): String {
        var sign: ByteArray? = null
        try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(appSecretKey?.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
            sign = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            LogUtil.e(TAG, e.message ?: "")
        } catch (e: InvalidKeyException) {
            LogUtil.e(TAG, e.message ?: "")
        }
        return Base64.encodeToString(sign, Base64.NO_WRAP)
    }
}