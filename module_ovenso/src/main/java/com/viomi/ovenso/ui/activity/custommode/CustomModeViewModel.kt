package com.viomi.ovenso.ui.activity.custommode

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *@description:
 *@data:2021/12/22
 */
class CustomModeViewModel : ViewModel() {

    // 包含状态 和 数据变化
    val _viewStates: MutableLiveData<CustomModeViewState> =
        MutableLiveData(CustomModeViewState())

    // 废弃了obserable 方法，  asLiveData 和LIveData 的区别
    val viewStates = _viewStates.asLiveData()

    /**
     * 通过Action 处理数据
     */
    fun dispatch(viewAction: MainViewAction) {
        Log.i(TAG, "dispatch:  viewAction$viewAction")
        when (viewAction) {
            MainViewAction.FabClicked -> fabClicked()
        }
    }

    private fun fabClicked() {

    }

    companion object {
        private const val TAG = "CustomModeViewModel"
    }
}
