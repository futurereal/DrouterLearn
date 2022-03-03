package com.viomi.ovensocommon.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.viomi.common.ApplicationUtils

/**
 * 获取DataStore实例
 *
 * @author vipyinzhiwei
 * @since  2021/5/12
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = ApplicationUtils.getContext().packageName + "_preferences",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, ApplicationUtils.getContext().packageName + "_preferences"))
    })