package com.viomi.ovenso.ui.activity.custommode

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import com.viomi.common.ApplicationUtils
import com.viomi.ovenso.OvenBusEventConstants
import com.viomi.ovenso.OvenConstants
import com.viomi.ovenso.bean.ModeTypeEntity
import com.viomi.ovenso.bean.OvenWorkStatusEnum
import com.viomi.ovenso.common.BaseTitleActivity
import com.viomi.ovenso.custommode.CustomeModeUtils
import com.viomi.ovenso.helper.ModesHelper
import com.viomi.ovenso.microwave.R
import com.viomi.ovenso.microwave.databinding.ActivityCustomModeBinding
import com.viomi.ovenso.serial.CustomModeWrite
import com.viomi.ovenso.serial.OvenSerialManager
import com.viomi.ovenso.ui.activity.running.CookRunningActivity
import com.viomi.ovenso.util.ItemDecorationUtil
import com.viomi.ovenso.util.OvenTestUtil
import com.viomi.ovenso.util.OvenUtil
import com.viomi.ovensocommon.CommonConstant
import com.viomi.ovensocommon.ViomiRouterConstant.*
import com.viomi.ovensocommon.componentservice.entity.PropertyEntity
import com.viomi.ovensocommon.db.CookParamEntity
import com.viomi.ovensocommon.rxbus.ViomiRxBus
import com.viomi.ovensocommon.rxbus.ViomiRxBusEvent
import com.viomi.ovensocommon.serialcontrol.PropertyPreferenceManager
import com.viomi.ovensocommon.spec.OvenActionEnum
import com.viomi.ovensocommon.spec.OvenPropEnum
import com.viomi.ovensocommon.toast.ViomiToastUtil
import com.viomi.router.annotation.Route
import com.viomi.router.core.ViomiRouter
import java.util.*

/**
 *@description: 屏端自定义模式, 和Fragment 的交互使用MVI的模式
 *@data:2021/12/22
 */
@Route(path = OVENSO_MODE_CUSTOM)
class CustomModeActivity : BaseTitleActivity<ActivityCustomModeBinding>(),
    AdapterView.OnItemClickListener {
    private var isStartCook: Boolean = false
    private lateinit var customModeName: String
    private var totalTime: Float = 0f
    private var currentPosition: Int = 0
    private var isAddMode: Boolean = false
    private lateinit var addModeTypeEntity: ModeTypeEntity
    private lateinit var customModeAdapter: CustomModeAdapter
    private lateinit var modeTypeEntityList: MutableList<ModeTypeEntity>

    // 需要 高版本的AndroidX 1.3.1
    private val customModeViewModel: CustomModeViewModel by viewModels()
    override fun getChildContentViewId(): Int = R.layout.activity_custom_mode

    override fun getTitleName(): String {
        customModeName = getString(R.string.custommode_title)
        return customModeName
    }

    override fun initChildUi() {
        // ArrayExtra 和 ArrayListExtra 有区别的
        val modeTypeEntity: ModeTypeEntity =
            intent.getParcelableExtra(KEY_MODE_ENTITY)!!
        val cookParamEntityList = modeTypeEntity.cookParamEntityList
        Log.i(TAG, "initChildUi: ${cookParamEntityList?.size}")
        customModeAdapter = CustomModeAdapter()
        // 模式数据
        modeTypeEntityList = initModeTypeEntity(cookParamEntityList)
        Log.i(TAG, "initChildUi: ${modeTypeEntityList.size}")
        customModeAdapter.setModeList(modeTypeEntityList)
        childViewBinding.recyclerviewCustommode.adapter = customModeAdapter
        childViewBinding.recyclerviewCustommode.addItemDecoration(
            ItemDecorationUtil.linearHorDecor(
                ApplicationUtils.getContext(),
                ITEM_MARGIN
            )
        )
        customModeAdapter.notifyDataSetChanged()
    }

    override fun initListener() {
        super.initListener()
        Log.i(TAG, "initListener: ")
        initObservable()
        customModeAdapter.setOnItemClickListener(this)
        customModeAdapter.setItemButtonListener(object : CustomModeAdapter.ItemButtonListener {
            override fun delete(modeTypeEntity: ModeTypeEntity) {
                Log.i(TAG, "delete: ")
                deleteModeTypeEntity(modeTypeEntity)
            }
        })
        childViewBinding.includeStart.root.setOnClickListener {
            if (modeTypeEntityList.size == 1) {
                ViomiToastUtil.showToastCenter(getString(R.string.custommode_tip))
                return@setOnClickListener
            }
            val isWaterTankClose = PropertyPreferenceManager.getInstance().getProperty(
                OvenPropEnum.WATERTANK_ISCLOSE.siid,
                OvenPropEnum.WATERTANK_ISCLOSE.piid,
                false
            ) as Boolean
            if (!isWaterTankClose) {
                var needTip = false
                val iterator = modeTypeEntityList.iterator()
                for (item in iterator) {
                    if (MODE_TYPE_STREAM == item.modeType) {
                        needTip = true
                    }
                }
                Log.i(TAG, "initListener: $needTip")
                if (needTip) {
                    ViomiToastUtil.showToastCenter(getString(R.string.error_water_tank_close_content))
                    return@setOnClickListener
                }
            }

            ViomiToastUtil.showToastNormal(
                getString(R.string.oven_cookparam_launching),
                Toast.LENGTH_SHORT
            )
            isStartCook = true
            val propertyList: List<PropertyEntity> = getPropertyList()
            OvenSerialManager.getInstance().writePropertyList(propertyList)
            OvenTestUtil.testCookingUI(OvenWorkStatusEnum.WORKING)
        }

        childViewBinding.includeAppoint.root.setOnClickListener {
            if (modeTypeEntityList.size == 1) {
                ViomiToastUtil.showToastCenter(getString(R.string.custommode_tip))
                return@setOnClickListener
            }
            val propertyEntityList: List<PropertyEntity> = getPropertyList()
            OvenUtil.showAppointFragment(
                totalTime,
                OvenConstants.DISHID_NO_RECIPE,
                propertyEntityList
            )
        }
    }

    override fun initData() {
    }

    private fun getPropertyList(): List<PropertyEntity> {
        val propertyEntityList = mutableListOf<PropertyEntity>()
        //模式
        val modePropertyEntity = PropertyEntity()
        modePropertyEntity.sid = OvenPropEnum.MODE.siid
        modePropertyEntity.pid = OvenPropEnum.MODE.piid
        modePropertyEntity.content = OvenConstants.MODEID_SCREEN_CUSTOMMODE
        propertyEntityList.add(modePropertyEntity)
        // 字符串
        val propertyEntityMcu = PropertyEntity()
        val stringBuilder = StringBuilder()
        totalTime = 0f
        modeTypeEntityList.forEach constituting@{
            if (it.modeId == OvenConstants.MODE_ID_ADD) return@constituting
            stringBuilder.append(it.modeId).append(CustomeModeUtils.PLUG_SPLITER)
            var defineTime = it.cookParamEntityList[0].defineTime
            if (it.modeId == OvenConstants.MODE_ID_MICRWAVE) {
                defineTime *= 60
            }
            stringBuilder.append(OvenUtil.getFloatString(defineTime))
                .append(CustomeModeUtils.PLUG_SPLITER)
            stringBuilder.append(it.cookParamEntityList[0].defineFirepower)
                .append(CustomeModeUtils.PLUG_SPLITER)
            totalTime += it.cookParamEntityList[0].defineTime
        }
        var mucStr = stringBuilder.toString()
        mucStr = mucStr.substring(0, mucStr.lastIndexOf(CustomeModeUtils.PLUG_SPLITER))
        Log.i(TAG, "getPropertyList: $totalTime")
        propertyEntityMcu.sid = CustomModeWrite.CUTOMMODE_MUC_SIID
        propertyEntityMcu.pid = CustomModeWrite.CUTOMMODE_MUC_PID
        propertyEntityMcu.content = mucStr
        propertyEntityList.add(propertyEntityMcu)
        Log.i(TAG, "getPropertyList: propertyMcu : $propertyEntityMcu")
        Log.i(TAG, "getPropertyList: ${propertyEntityList.size}")
        return propertyEntityList
    }

    private fun initObservable() {
        Log.i(TAG, "initObservable: ")
        customModeViewModel.viewStates.run {
            observeState(this@CustomModeActivity, CustomModeViewState::modeTypeEntity) {
                Log.i(TAG, "initViewModel: newsList size : $it")
                // 初始状态 newList size 为 0 。  如果屏幕旋转，重建，这个list 就不为0， 可以加载出来之前的值
                addOrEditMode(it)
            }
        }
        // 监听启动
        val customModeDisposable =
            ViomiRxBus.getInstance().subscribeUi { busEvent: ViomiRxBusEvent ->
                when (busEvent.msgId) {
                    CommonConstant.MSG_DOWNWRITE_SUCCESS -> {
                        if (!isStartCook) {
                            Log.i(TAG, "initListener: isBook return")
                            return@subscribeUi
                        }
                        //延迟100s 解决 属性变化回的比较慢，导致无法启动烤箱
                        Thread.sleep(100)
                        OvenSerialManager.getInstance()
                            .doStandardAction(OvenActionEnum.ACTION_START)
                        isStartCook = false
                    }
                    OvenBusEventConstants.MSG_COOK_STATUSCHANGE -> {
                        Log.i(TAG, "initObservable: $isActivityResumed")
                        if (!isActivityResumed) {
                            Log.i(TAG, "initListener: isActivityResume false")
                            return@subscribeUi
                        }
                        val statusEnumValue = busEvent.msgObject as Int
                        if (statusEnumValue != OvenWorkStatusEnum.WORKING.value && statusEnumValue != OvenWorkStatusEnum.BOOKED.value) {
                            Log.i(TAG, "initListener: not start")
                            return@subscribeUi
                        }
                        val sendModeTypeList = mutableListOf<ModeTypeEntity>()
                        Log.i(TAG, "initObservable: ${modeTypeEntityList.size}")
                        for (modeTypeEntity in modeTypeEntityList) {
                            if (modeTypeEntity.modeId != OvenConstants.MODE_ID_ADD) {
                                Log.i(TAG, "initObservable: modeTypeEntity: $modeTypeEntity")
                                sendModeTypeList.add(modeTypeEntity)
                            }
                        }
                        ViomiRouter.getInstance().build(OVENSO_COOK_RUNNING).withString(
                            CookRunningActivity.KEY_RECIPENAME,
                            customModeName
                        ).withInt(CookRunningActivity.KEY_STAUSENUMS_VALUE, statusEnumValue)
                            .withParcelableArrayList(
                                CookRunningActivity.KEY_MODETYPE_LIST,
                                ArrayList(sendModeTypeList)
                            ).navigation()
                    }
                }
            }
        addDisposable(customModeDisposable)
    }

    private fun addOrEditMode(modeTypeEntity: ModeTypeEntity?) {
        Log.i(TAG, "addModeTypeEntity: $modeTypeEntity ")
        if (modeTypeEntity == null) {
            return
        }
        Log.i(TAG, "addOrEditMode: isAddMode  $isAddMode")
        // 编辑模式
        if (!isAddMode) {
            modeTypeEntityList.removeAt(currentPosition)
            modeTypeEntityList.add(currentPosition, modeTypeEntity)
            customModeAdapter.notifyDataSetChanged()
            return
        }
        val modeEntitySize = modeTypeEntityList.size
        Log.i(TAG, "addOrEditMode: modeEntitySize  $modeEntitySize")
        // 添加模式
        // 如果是三个移除最后一个
        if (modeEntitySize == MODE_MAX) {
            addModeTypeEntity = modeTypeEntityList.removeLast()
            modeTypeEntityList.add(modeTypeEntity)
            customModeAdapter.notifyDataSetChanged()
            return
        }
        // 如果不是三个
        val lastIndexed = modeEntitySize - 1
        modeTypeEntityList.add(lastIndexed, modeTypeEntity)
        customModeAdapter.notifyDataSetChanged()
    }

    private fun initModeTypeEntity(cookParamEntityList: List<CookParamEntity>): MutableList<ModeTypeEntity> {
        val modeTypeList = mutableListOf<ModeTypeEntity>()
        for (cookParamEntity in cookParamEntityList) {
            val currentModeTypeEntity = ModesHelper.getModeEntityById(cookParamEntity.modeId)
            modeTypeList.add(ModeTypeEntity.deepCopy(currentModeTypeEntity))
        }
        Log.i(TAG, "initModeTypeEntity: ${modeTypeList.size}")
        return modeTypeList
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.i(TAG, "onItemClick: $position")
        currentPosition = position
        val modeTypeEntity = modeTypeEntityList[position]
        // 判断是如果是+号 添加模式，否则是编辑模式  isAddMode 数据回调的时候使用
        isAddMode = modeTypeEntity.modeId == OvenConstants.MODE_ID_ADD
        Log.i(TAG, "onItemClick: isAdd: $isAddMode")
        OvenUtil.showModeSelectFragment(
            modeTypeEntityList as ArrayList<ModeTypeEntity>?,
            position
        )
    }

    fun deleteModeTypeEntity(modeTypeEntity: ModeTypeEntity) {
        Log.i(TAG, "deleteModeTypeEntity: $modeTypeEntity")
        val length = modeTypeEntityList.size
        val lastIndex = modeTypeEntityList.lastIndex
        val lastEntity = modeTypeEntityList[lastIndex]
        modeTypeEntityList.remove(modeTypeEntity)
        if (length == MODE_MAX && lastEntity.modeId != OvenConstants.MODE_ID_ADD) {
            modeTypeEntityList.add(addModeTypeEntity)
        }
        customModeAdapter.notifyDataSetChanged()
    }

    companion object {
        const val KEY_MODE_ENTITY: String = "keyModeEntity"
        const val MODE_TYPE_STREAM: String = "蒸"
        const val MODE_MAX: Int = 3
        const val ITEM_MARGIN: Int = 25
        private const val TAG = "CustomModeActivity"
    }
}

