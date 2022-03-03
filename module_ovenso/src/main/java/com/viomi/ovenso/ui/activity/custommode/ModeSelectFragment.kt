package com.viomi.ovenso.ui.activity.custommode

import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import com.blankj.utilcode.util.ScreenUtils
import com.viomi.common.ApplicationUtils
import com.viomi.ovenso.OvenConstants
import com.viomi.ovenso.bean.ModeDetailTitleEntity
import com.viomi.ovenso.bean.ModeTypeEntity
import com.viomi.ovenso.helper.ModesHelper
import com.viomi.ovenso.microwave.R
import com.viomi.ovenso.microwave.databinding.FragmentModeSelectBinding
import com.viomi.ovenso.util.ItemDecorationUtil
import com.viomi.ovenso.util.OvenUtil
import com.viomi.ovensocommon.BaseDialogFragment
import com.viomi.ovensocommon.toast.ViomiToastUtil

/**
 *@description: 模式选择的弹框
 *@data:2021/12/23
 */
class ModeSelectFragment : BaseDialogFragment<FragmentModeSelectBinding>(),
    AdapterView.OnItemClickListener {
    private var selectPosition: Int = 0
    private var currentPosition: Int = 0
    private var isWrongMode: Boolean = false
    private lateinit var currentModeTypeEntity: ModeTypeEntity
    private var modifyMode: Boolean = false
    private lateinit var viewModel: Lazy<CustomModeViewModel>
    private lateinit var allModeTypeEntityList: List<ModeTypeEntity>
    private lateinit var selectModeTypeEntityList: ArrayList<ModeTypeEntity>
    private lateinit var modeTitleList: List<ModeDetailTitleEntity>
    private lateinit var modeSelectTitleAdapter: ModeSelectTitleAdapter

    override fun initView() {
        viewModel = requireActivity().viewModels()
        // 标题的数据
        modeTitleList = getModeTitleList()
        selectModeTypeEntityList =
            arguments?.getParcelableArrayList(KEY_MODETYPE_ENTITY_LIST)!!
        selectPosition =
            arguments?.getInt(KEY_MODETYPE_POSITION)!!

        // 1 添加模式  只用判断 前一个，如果是前一个是微波，需要判断 之前的 。
        // 2 修改模式 ，如果是 最后一个   和 一 相同。
        // 3 修改模式 如果是 第一个，需要 和 第二个对比
        // 4 修改模式  如果是 中间的， 需要 前面的 和后面的比较

        Log.i(TAG, "initView: $selectPosition  size:   ${selectModeTypeEntityList.size} ")
        val selectModeTypeEntity = selectModeTypeEntityList[selectPosition]
        if (selectModeTypeEntity.modeId == OvenConstants.MODE_ID_ADD) {
            currentPosition = 0
        } else {
            modifyMode = true
            currentPosition = getModeIndex(selectModeTypeEntity)
            Log.i(TAG, "initView: currentIndex =$currentPosition")
            val definePower = selectModeTypeEntity.cookParamEntityList[0]?.defineFirepower
            val defineTime = selectModeTypeEntity.cookParamEntityList[0]?.defineTime

            allModeTypeEntityList[currentPosition].cookParamEntityList[0].defineFirepower =
                definePower!!
            allModeTypeEntityList[currentPosition].cookParamEntityList[0]?.defineTime =
                defineTime!!
        }
        Log.i(TAG, "initView: ${modeTitleList.size}")
        modeSelectTitleAdapter = ModeSelectTitleAdapter(modeTitleList)
        viewDataBinding.recyclerviewModeselectTitle.adapter = modeSelectTitleAdapter
        viewDataBinding.recyclerviewModeselectTitle.addItemDecoration(
            ItemDecorationUtil.linearHorDecor(
                ApplicationUtils.getContext(),
                50
            )
        )
        updateView(currentPosition)
    }

    override fun onStart() {
        landWidth = (ScreenUtils.getScreenWidth() * 0.6).toInt()
        landHeight = ScreenUtils.getScreenHeight()
        Log.i(TAG, "onStart: ")
        super.onStart()
    }

    private fun getModeIndex(modeTypeEntity: ModeTypeEntity?): Int {
        for (entityIndex in allModeTypeEntityList.indices) {
            currentModeTypeEntity = allModeTypeEntityList[entityIndex]
            if (currentModeTypeEntity.modeId == modeTypeEntity?.modeId) {
                return entityIndex
            }
        }
        return POSITION_FIRST
    }


    private fun getModeTitleList(): List<ModeDetailTitleEntity> {
        allModeTypeEntityList = ModesHelper.getModeTypeList(R.array.modeid_combine)
        Log.i(TAG, "getModeTitleList: ${allModeTypeEntityList.size}")
        //var modeTileList = ArrayList<ModeDetailTitleEntity>(10)
        val modeTileList = mutableListOf<ModeDetailTitleEntity>()
        for (index in allModeTypeEntityList.indices) {
            val modeTitleEntity = ModeDetailTitleEntity()
            modeTitleEntity.titleName = allModeTypeEntityList[index].name
            modeTileList.add(modeTitleEntity)
        }
        Log.i(TAG, "getModeTitleList: final ${modeTileList.size}")
        return modeTileList
    }

    override fun initListener() {
        Log.i(TAG, "initListener: ")
        viewDataBinding.modeselectCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        viewDataBinding.modeselectSure.setOnClickListener {
            if (isWrongMode) {
                ViomiToastUtil.showToastCenter(viewDataBinding.modeselectTemperatureTip.text.toString())
                return@setOnClickListener
            }
            // 同时更新 获取的状态  和   获取数据的list
            val modeTypeEntity = allModeTypeEntityList[currentPosition]
            Log.i(TAG, "initListener: $modeTypeEntity ")
            viewModel.value._viewStates.setState {
                Log.i(TAG, "initListener: sendEntityData")
                // 判断是修改模式 还是 增加模式
                val deepModeTypeEntity = ModeTypeEntity.deepCopy(modeTypeEntity)
                copy(modeTypeEntity = deepModeTypeEntity)
            }
            dismissAllowingStateLoss()
        }
        modeSelectTitleAdapter.setOnItemClickListener(this)
        viewDataBinding.modeselectTimerpicker.setOnSelectListener { _, selected ->
            val defineTime = selected.first.toFloat()
            Log.i(
                TAG,
                "initListener: defineTime : $defineTime"
            )
            currentModeTypeEntity = allModeTypeEntityList[currentPosition]
            allModeTypeEntityList[currentPosition].cookParamEntityList[0].defineTime = defineTime
        }

        viewDataBinding.modeselectTemperaturepicker.setOnSelectListener { _, selected ->
            val modeTyp = allModeTypeEntityList[currentPosition].modeType
            val isMicroWave = OvenUtil.isMicroMode(modeTyp)
            val defineFirePower =
                if (isMicroWave) OvenUtil.getMicroPowerNames()
                    .indexOf(selected.first) + 1 else selected.first.toInt()

            Log.i(
                TAG,
                "initListener: defineFirePower: $defineFirePower"
            )
            allModeTypeEntityList[currentPosition].cookParamEntityList[0].defineFirepower =
                defineFirePower
            updateTemperatureTip(allModeTypeEntityList[currentPosition])
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_mode_select
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.i(TAG, "onItemClick: ")
        updateView(position)
    }

    companion object {
        const val KEY_MODETYPE_POSITION: String = "keyModeTypePosition"
        const val KEY_MODETYPE_ENTITY_LIST: String = "keyModeEntityList"
        const val POSITION_FIRST: Int = 0
        private const val TAG = "ModeSelectFragment"
    }

    private fun updateView(position: Int) {
        Log.i(TAG, "updateView: position: $position")
        modeTitleList[currentPosition].isSelect = false
        modeTitleList[position].isSelect = true
        currentPosition = position
        modeSelectTitleAdapter.notifyDataSetChanged()

        viewDataBinding.modeselectTitle.text = modeTitleList[position].titleName
        viewDataBinding.recyclerviewModeselectTitle.scrollToPosition(position)

        val modeTypeEntity = allModeTypeEntityList[position]
        Log.i(TAG, "updatePicker: modeTypeEntity: $modeTypeEntity")
        val cookParamEntity = modeTypeEntity.cookParamEntityList[0]
        val isMicroMode = OvenUtil.isMicroMode(cookParamEntity.modeType)
        if (isMicroMode) {
            val microNames = OvenUtil.getMicroPowerNames()
            viewDataBinding.modeselectTemperaturepicker.setUnitText("")
            viewDataBinding.modeselectTemperaturepicker.setStringData(
                microNames,
                cookParamEntity.defineFirepower - 1
            )
            viewDataBinding.modeselectTimerpicker.setFloatData(
                cookParamEntity.defineTime,
                cookParamEntity.timeRangeMin,
                cookParamEntity.timeRangeMax,
                OvenConstants.MICRO_TIME_STEP
            )
        } else {
            viewDataBinding.modeselectTemperaturepicker.setUnitText(getString(R.string.temperature_single))
            viewDataBinding.modeselectTemperaturepicker.setIntData(
                cookParamEntity.defineFirepower,
                cookParamEntity.firepowerRangeMin, cookParamEntity.firepowerRangeMax
            )
            viewDataBinding.modeselectTimerpicker.setIntData(
                cookParamEntity.defineTime.toInt(),
                cookParamEntity.timeRangeMin.toInt(),
                cookParamEntity.timeRangeMax.toInt(),
            )
        }
        updateTemperatureTip(modeTypeEntity)
    }

    private fun updateTemperatureTip(modeTypeEntity: ModeTypeEntity) {
        val cookParmaEntity = modeTypeEntity.cookParamEntityList[0]
        if (OvenUtil.isMicroMode(cookParmaEntity.modeType)) {
            isWrongMode = false
            viewDataBinding.modeselectTemperatureTip.visibility = View.INVISIBLE
            return
        }
        val definePower = cookParmaEntity.defineFirepower
        isWrongMode = compareBefore(definePower)
        Log.i(TAG, "updateTemperatureTip: wrongBefore $isWrongMode")
        if (isWrongMode) {
            viewDataBinding.modeselectTemperatureTip.visibility = View.VISIBLE
            viewDataBinding.modeselectTemperatureTip.text =
                getString(R.string.custommode_select_tempearaturewarm_before)
            return
        }
        isWrongMode = compareAfter(definePower)
        Log.i(TAG, "updateTemperatureTip: wrongAfter:$isWrongMode")
        if (isWrongMode) {
            viewDataBinding.modeselectTemperatureTip.visibility = View.VISIBLE
            viewDataBinding.modeselectTemperatureTip.text =
                getString(R.string.custommode_select_tempearaturewarm_after)
            return
        }
        if (!isWrongMode) {
            viewDataBinding.modeselectTemperatureTip.visibility = View.INVISIBLE
        }
    }

    private fun compareBefore(definePower: Int): Boolean {
        Log.i(TAG, "compareBefore: $definePower")
        var isWrongTemperatue = false
        for (index in selectModeTypeEntityList.indices) {
            if (index < selectPosition) {
                val indexPower =
                    selectModeTypeEntityList[index].cookParamEntityList[0].defineFirepower
                if (indexPower > definePower) {
                    isWrongTemperatue = true
                }
            }
        }
        return isWrongTemperatue
    }

    private fun compareAfter(definePower: Int): Boolean {
        Log.i(TAG, "compareAfter: $definePower")
        var isWrongTemperatue = false
        for (index in selectModeTypeEntityList.indices) {
            if (index > selectPosition) {
                val cookParmaEntity =
                    selectModeTypeEntityList[index].cookParamEntityList[0]
                if (cookParmaEntity.modeId == OvenConstants.MODE_ID_ADD) {
                    continue
                }
                val selectName = cookParmaEntity.modeName
                if (OvenUtil.isMicroMode(selectName)) {
                    continue
                }
                val selectPower = cookParmaEntity.defineFirepower
                if (selectPower < definePower) {
                    isWrongTemperatue = true
                }
            }
        }
        return isWrongTemperatue
    }

}