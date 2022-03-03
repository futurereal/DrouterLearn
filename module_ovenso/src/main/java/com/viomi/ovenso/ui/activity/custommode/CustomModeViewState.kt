package com.viomi.ovenso.ui.activity.custommode

import com.viomi.ovenso.bean.ModeTypeEntity

/**
 *@description:
 *@data:2021/12/22
 */
data class CustomModeViewState(
    var modeTypeEntity: ModeTypeEntity? = null
)

sealed class MainViewAction {
    // 行为类
    object FabClicked : MainViewAction()
    object OnSwipeRefresh : MainViewAction()
    object FetchNews : MainViewAction()
}