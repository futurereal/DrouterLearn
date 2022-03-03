package com.viomi.upgrade_lib.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.viomi.upgrade_lib.R
import com.viomi.upgrade_lib.UpdateConst
import com.viomi.upgrade_lib.api.VUpgradeApi
import com.viomi.upgrade_lib.entity.CheckVersionResult
import com.viomi.upgrade_lib.entity.DeviceInfo
import com.viomi.upgrade_lib.utils.VLog
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by mai on 2020/12/16.
 */
class MainActivity : AppCompatActivity() {
    var checkVersionResult: CheckVersionResult? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions =
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (checkSelfPermission(str) !== PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    requestPermissions(permissions, REQUEST_CODE_CONTACT)
                }
            }
        }

        UpdateConst.HTTP_DEBUG = true;  //开启测试环境
        UpdateConst.CHECK_PERIOD_DEBUG = true;   //开启测试轮询时间  10分钟

        VUpgradeApi.instance.init(
            this, DeviceInfo(
                did = "1000539281",
                channel = 0,
                mac = "04:cf:8c:fc:6f:ac",
                model = "viomi.fridge.x4",
                firmwareName = "FridgeLauncher",
                firmwareType = 3
            ), getUserId = {
                "11977689"
            }, isCheckVersion = {
                true
            }, versionCallBack = {
                VLog.d("test", "是否检测到版本-->$it")
            })

        tv_check.setOnClickListener {
            VUpgradeApi.instance.checkVersion(this, versionListener = {
                when (it.code) {
                    UpdateConst.CODE_SUCCESS -> { //存在新版本

                    }
                    UpdateConst.CODE_NO_NEED_UPDATE -> { //没有新版本

                    }
                    UpdateConst.CODE_PARSE_FAIL -> { //服务器出错

                    }
                }
                checkVersionResult = it;
            })
        }

        tv_update.setOnClickListener {
            VUpgradeApi.instance.downloadApk(
                this,
                checkVersionResult!!.url!!,
                checkVersionResult!!.upgradeType,
                checkVersionResult!!.recordId,
                downloadListener = {
                    when (it.code) {
                        UpdateConst.CODE_SUCCESS -> {  //下载完成

                        }
                        UpdateConst.CODE_DOWNLOADING -> { //正在下载

                        }
                        UpdateConst.CODE_PARSE_FAIL -> { //下载出错

                        }
                    }
                })
        }
    }
}
