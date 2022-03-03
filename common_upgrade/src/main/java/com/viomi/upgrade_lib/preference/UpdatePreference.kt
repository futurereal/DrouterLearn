package com.viomi.upgrade_lib.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.viomi.upgrade_lib.UpdateConst.PREFERENCE_NAME

/**
 * 更新相关 SP
 *
 * @author William
 * @date 2020/9/5
 */
class UpdatePreference private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun saveFileLength(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getFileLength(key: String): Long {
        return sharedPreferences.getLong(key, 0)
    }

    companion object {
        @Volatile
        var instance: UpdatePreference? = null

        fun getInstance(context: Context): UpdatePreference {
            if (instance == null) {
                synchronized(UpdatePreference::class.java) {
                    if (instance == null) {
                        instance = UpdatePreference(context)
                    }
                }
            }
            return instance!!
        }
    }
}