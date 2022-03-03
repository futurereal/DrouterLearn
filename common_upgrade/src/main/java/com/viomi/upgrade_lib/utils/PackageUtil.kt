package com.viomi.upgrade_lib.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by mai on 2020/12/14.
 */
class PackageUtil {

    companion object {
        fun getVersionCode(context: Context): Int {
            val packageManager = context.packageManager
            val versionCode: Int
            versionCode = try {
                val packageInfo =
                    packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionCode
            } catch (var5: PackageManager.NameNotFoundException) {
                var5.printStackTrace()
                0
            }
            return versionCode
        }

        fun getVersionName(context: Context): String? {
            val packageManager = context.packageManager
            val versionName: String
            versionName = try {
                val packageInfo =
                    packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName
            } catch (var5: PackageManager.NameNotFoundException) {
                var5.printStackTrace()
                ""
            }
            return versionName
        }
    }

}