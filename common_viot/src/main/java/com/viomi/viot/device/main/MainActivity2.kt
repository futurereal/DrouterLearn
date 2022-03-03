package com.viomi.viot.device.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.viomi.viot.R
import com.viomi.viot.databinding.ActivityMain2Binding
import com.viomi.viot.rxjava2.RxScheduler
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * Created by William on 2020/6/15.
 */
class MainActivity2 : AppCompatActivity() {
    private val did = "1000028622"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMain2Binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

        ApiClient.instance.init()
        ApiClient.instance.isDebug = false

        binding.input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ApiClient.instance.token = binding.input.text.toString()
            }
        })
        binding.input.setText("QNNt5czCWRbRbGX7")

        binding.setProperties.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("id", 123)
            jsonObject.put("version", "1.0")
            jsonObject.put("method", "set_properties")
            val jsonArray = JSONArray()
            val param = JSONObject()

            param.put("did", did)
            param.put("piid", 1)
            param.put("siid", 3)
            param.put("value", "false")

            val param2 = JSONObject()
            param2.put("did", did)
            param2.put("piid", 2)
            param2.put("siid", 3)
            param2.put("value", 12345)

            jsonArray.put(param)
            jsonArray.put(param2)
            jsonObject.put("params", jsonArray)

            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
            ApiClient.instance.service?.openApi(requestBody)
                ?.compose(RxScheduler.schedulerTransformer1())
                ?.subscribe({

                }, {

                })
        }

        binding.action.setOnClickListener {
            val jsonObject = JSONObject()
            jsonObject.put("id", 123)
            jsonObject.put("version", "1.0")
            jsonObject.put("method", "action")

            val jsonArray = JSONArray()
            val jsonParam = JSONObject()
            val jsonParam2 = JSONObject()

            val jsonArrayIn = JSONArray()
            val jsonObjectIn = JSONObject()
            jsonObjectIn.put("piid", 10)
            jsonObjectIn.put("value", 0)
            jsonArrayIn.put(jsonObjectIn)

            val jsonArrayIn2 = JSONArray()
            val jsonObjectIn2 = JSONObject()
            jsonObjectIn2.put("piid", 12)
            jsonObjectIn2.put("value", true)
            jsonArrayIn2.put(jsonObjectIn2)

            jsonParam.put("aiid", 1)
            jsonParam.put("siid", 2)
            jsonParam.put("did", did)
            jsonParam.put("in", jsonArrayIn)
            jsonArray.put(jsonParam)

            jsonParam2.put("aiid", 4)
            jsonParam2.put("siid", 3)
            jsonParam2.put("did", did)
            jsonParam2.put("in", jsonArrayIn2)
            jsonArray.put(jsonParam2)

            jsonObject.put("params", jsonArray)

            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
            ApiClient.instance.service?.openApi(requestBody)
                ?.compose(RxScheduler.schedulerTransformer1())
                ?.subscribe({

                }, {

                })
        }

        binding.mesh.setOnClickListener {
            ApiClient.instance.service?.subDevMeshRequest("1111121807")
                ?.compose(RxScheduler.schedulerTransformer1())
                ?.subscribe({

                }, {

                })
        }
    }
}