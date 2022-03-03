package com.viomi.ovensocommon.utils

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.ActivityUtils
import com.viomi.ovensocommon.ViomiRouterConstant.*
import com.viomi.router.core.ViomiRouter
import com.viomi.router.core.utils.RefInvoke

/**
 *@description:
 *@data:2021/12/14
 */
object FragmentUtils {
    private const val TAG = "FragmentUtils"

    @JvmStatic
    fun loadFragment(containerId: Int, targetFragmentRouter: String) {
        Log.i(
            TAG,
            "initView: fragmentRouterName: $targetFragmentRouter"
        )
        val fragmentPair = ViomiRouter.getInstance().build(targetFragmentRouter).providerPage
        Log.i(
            TAG,
            "initView: fragmentRouterName: $fragmentPair"
        )
        val targetFragment = RefInvoke.createObject(fragmentPair.first) as Fragment
        val fragmentActivity = ActivityUtils.getTopActivity() as FragmentActivity
        val transaction: FragmentTransaction =
            fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.add(containerId, targetFragment, targetFragment.javaClass.name)
        transaction.commitAllowingStateLoss()
    }

    @JvmStatic
    fun loadCameraFragment(
        containerId: Int,
        recipeName: String?,
        modeId: String?,
        isCooking: Boolean
    ) {
        Log.i(TAG, "loadCameraFragment: ")
        val postCard = ViomiRouter.getInstance().build(CAMERA_FRAGMENT)
        Log.i(TAG, "loadCameraFragment: $postCard")
        /*     if (!TextUtils.isEmpty(recipeName)) {
                 postCard.withString(CAMERA_KEY_RECIPENAME, recipeName)
             }
             if (!TextUtils.isEmpty(modeId)) {
                 postCard.withString(CAMERA_KEY_MODEID, modeId)
             }
             postCard.withBoolean(CAMERA_KEY_COOKING, isCooking)*/
        val fragmentPair = postCard.providerPage

        Log.i(
            TAG,
            "initView: fragmentRouterName: $fragmentPair"
        )
        val targetFragment = RefInvoke.createObject(fragmentPair.first) as Fragment
        val bundle = Bundle()
        if (!TextUtils.isEmpty(recipeName)) {
            bundle.putString(CAMERA_KEY_RECIPENAME, recipeName)
        }
        if (!TextUtils.isEmpty(modeId)) {
            bundle.putString(CAMERA_KEY_MODEID, modeId)
        }
        bundle.putString(CAMERA_KEY_RECIPENAME, recipeName)
        bundle.putString(CAMERA_KEY_MODEID, modeId)
        bundle.putBoolean(CAMERA_KEY_COOKING, isCooking)
        targetFragment.arguments = bundle
        Log.i(TAG, "loadCameraFragment: " + targetFragment.arguments)
        val fragmentActivity = ActivityUtils.getTopActivity() as FragmentActivity
        val transaction: FragmentTransaction =
            fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.add(containerId, targetFragment, targetFragment.javaClass.name)
        transaction.commitAllowingStateLoss()

    }

}