package com.viomi.ovenso.ui.activity.custommode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.viomi.ovenso.bean.ModeDetailTitleEntity
import com.viomi.ovenso.microwave.R
import com.viomi.ovenso.microwave.databinding.ItemModeselectTitleBinding
import com.viomi.ovensocommon.BaseRecyclerViewAdapter

/**
 *@description:
 *@data:2021/12/23
 */
class ModeSelectTitleAdapter(private var modeTitleList: List<ModeDetailTitleEntity>) :
    BaseRecyclerViewAdapter<ModeSelectTitleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModeSelectTitleAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemDeselectTitleBinding = DataBindingUtil.inflate<ItemModeselectTitleBinding>(
            layoutInflater,
            R.layout.item_modeselect_title,
            null,
            false
        )
        return ViewHolder(itemDeselectTitleBinding.root, itemDeselectTitleBinding)
    }

    override fun onBindViewHolder(holder: ModeSelectTitleAdapter.ViewHolder, position: Int) {
        val modeTitleDetailEntity = modeTitleList[position]
        holder.bindView(modeTitleDetailEntity)
    }

    override fun getItemCount(): Int {
        return modeTitleList.size
    }

    inner class ViewHolder(
        itemView: View,
        private val itemDeselectTitleBinding: ItemModeselectTitleBinding
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun bindView(modeTitleDetailEntity: ModeDetailTitleEntity) {
            itemDeselectTitleBinding.selecttitleName.text = modeTitleDetailEntity.titleName
            if (modeTitleDetailEntity.isSelect) {
                itemDeselectTitleBinding.selecttitleSelecttip.visibility = View.VISIBLE
            } else {
                itemDeselectTitleBinding.selecttitleSelecttip.visibility = View.INVISIBLE
            }
            itemDeselectTitleBinding.root.setOnClickListener {
                onItemHolderClick(this, 250)
            }
        }
    }

}