package com.viomi.viot.https

import com.viomi.viot.utils.LogUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2020/5/26
 *     desc   : Http 请求参数封装
 *     version: 1.0
 * </pre>
 */
class RequestHandler {
    private val mMap: MutableMap<String, Any?>?

    val paramsJSONObject: JSONObject
        get() {
            val jsonObject = JSONObject()
            mMap?.forEach { kotlin.runCatching { jsonObject.put(it.key, it.value) }.onFailure { LogUtil.d(TAG, it.message.toString()) } }
            LogUtil.d(TAG, "params: ${mMap?.toString()}")
            return jsonObject
        }

    fun addParams(key: String, `object`: Any?) = mMap?.let { it[key] = `object` }

    val jsonRequestBody: RequestBody
        get() = paramsJSONObject.toString().toRequestBody("application/json".toMediaType())

    fun getTextRequestBody(text: String): RequestBody {
        return text.toRequestBody("application/text".toMediaType())
    }

    init {
        mMap = HashMap()
    }

    companion object {
        private val TAG = RequestHandler::class.java.simpleName
    }
}
