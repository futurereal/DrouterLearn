package com.viomi.waterpurifier.edison.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.viomi.ovensocommon.ViomiRouterConstant
import com.viomi.router.core.ViomiRouter
import com.viomi.waterpurifier.edison.manager.MessageDialogManager

class WaterTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startWaterMain();
        startOtherView()
    }

    private fun startOtherView() {
//        var childLockFragment = ChildLockFragment();
//        childLockFragment.show(getSupportFragmentManager(), ChildLockFragment.class. getName ());
        MessageDialogManager.getInstance().showFilterWashFragment()
    }

    private fun startWaterMain() {
        ViomiRouter.getInstance().build(ViomiRouterConstant.WATER_MAIN).navigation()
    }

}