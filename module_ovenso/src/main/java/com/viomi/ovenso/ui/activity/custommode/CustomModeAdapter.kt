package com.viomi.ovenso.ui.activity.custommode

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.viomi.common.ApplicationUtils
import com.viomi.ovenso.OvenApplication
import com.viomi.ovenso.OvenConstants
import com.viomi.ovenso.bean.ModeTypeEntity
import com.viomi.ovenso.microwave.R
import com.viomi.ovenso.microwave.databinding.ItemCustomModeBinding
import com.viomi.ovenso.util.OvenUtil
import com.viomi.ovensocommon.BaseRecyclerViewAdapter

/**
 *@description:
 *@data:2021/12/22
 */
class CustomModeAdapter :
    BaseRecyclerViewAdapter<CustomModeAdapter.ViewHolder>() {
    private lateinit var itemButtonListener: ItemButtonListener

    // 父类定义的有Tag const 只能引用到父类
    val TAG = "CustomModeAdapter"
    private var modeAddEntity: ModeTypeEntity? = null
    var modeTypeEntityList: MutableList<ModeTypeEntity>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemCustomModeBinding = DataBindingUtil.inflate<ItemCustomModeBinding>(
            layoutInflater,
            R.layout.item_custom_mode, null, false
        )
        return ViewHolder(itemCustomModeBinding, itemCustomModeBinding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemCount(): Int {
        val isZero = modeTypeEntityList == null
        if (isZero) {
            return 0
        }
        return modeTypeEntityList!!.size
    }

    fun setModeList(modeTypeEntityList: MutableList<ModeTypeEntity>?) {
        this.modeTypeEntityList = modeTypeEntityList
        modeAddEntity = modeTypeEntityList?.get(modeTypeEntityList.lastIndex)
        Log.i(TAG, "setModeList: $modeAddEntity")
    }

    inner class ViewHolder(itemCustomModeBinding: ItemCustomModeBinding, itemView: View) :
        RecyclerView.ViewHolder(
            itemView
        ) {
        private var itemCustomModeBinding: ItemCustomModeBinding? = itemCustomModeBinding
        fun bindView(position: Int) {
            val modeTypeEntity = modeTypeEntityList?.get(position)
            val imgResourceId = OvenApplication.getContext().resources.getIdentifier(
                modeTypeEntity!!.resIdBgCombine,
                "drawable", ApplicationUtils.getContext().packageName
            )
            itemCustomModeBinding?.custommodeBg?.setImageResource(imgResourceId)
            itemCustomModeBinding?.root?.rootView?.setOnClickListener {
                onItemHolderClick(this, OvenConstants.CLICK_MIN_TIME)
            }

            if (modeTypeEntity.modeId == OvenConstants.MODE_ID_ADD) {
                itemCustomModeBinding?.custommodeNormal?.visibility = View.GONE
                return
            }
            itemCustomModeBinding?.custommodeNormal?.visibility = View.VISIBLE
            itemCustomModeBinding?.custommodeName?.text = modeTypeEntity.name
            itemCustomModeBinding?.custommodeIndex?.text = (position + 1).toString()
            val cookParamEntity = modeTypeEntity.cookParamEntityList[0]
            val defineTime = cookParamEntity.defineTime
            Log.i(TAG, "bindView: defineTime: $defineTime")
            val isMicroWave = OvenUtil.isMicroMode(modeTypeEntity.name)
            val timeUnit = ApplicationUtils.getContext().getString(R.string.ovenso_timeunit)
            val temperatureUnit = ApplicationUtils.getContext().getString(R.string.ovenso_powerunit)
            val fireTimeAndPower = if (isMicroWave) {
                val currentDefineTimeName = OvenUtil.getFloatString(defineTime)
                val temperatureName = OvenUtil.getPowerName(cookParamEntity.defineFirepower)
                temperatureName + OvenConstants.SPLITER + currentDefineTimeName + timeUnit
            } else {
                val currentDefineTimeName = OvenUtil.getFloatString(defineTime)
                cookParamEntity.defineFirepower.toString() + temperatureUnit + OvenConstants.SPLITER + currentDefineTimeName + timeUnit
            }
            Log.i(TAG, "bindView: $fireTimeAndPower")
            itemCustomModeBinding?.custommodeFirepowerTime?.text = fireTimeAndPower
            itemCustomModeBinding?.custommodeDelete?.setOnClickListener {
                Log.i(TAG, "bindView: delete")
                itemButtonListener.delete(modeTypeEntity)
            }
        }
    }

    fun setItemButtonListener(itemButtonListener: ItemButtonListener) {
        this.itemButtonListener = itemButtonListener
    }

    interface ItemButtonListener {
        fun delete(modeTypeEntity: ModeTypeEntity)
    }

}