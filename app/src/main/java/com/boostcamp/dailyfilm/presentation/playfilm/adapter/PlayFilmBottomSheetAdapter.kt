package com.boostcamp.dailyfilm.presentation.playfilm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.databinding.ItemBottomSheetBinding
import com.boostcamp.dailyfilm.presentation.playfilm.model.BottomSheetModel

class PlayFilmBottomSheetAdapter(
    val onClick: (Int) -> (Unit)
) :
    ListAdapter<BottomSheetModel, PlayFilmBottomSheetAdapter.PlayFilmBottomSheetItemViewHolder>(
        diffUtil
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlayFilmBottomSheetItemViewHolder {
        return PlayFilmBottomSheetItemViewHolder(
            ItemBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlayFilmBottomSheetItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setItem(getItem(position))
        }
    }

    inner class PlayFilmBottomSheetItemViewHolder(private val bind: ItemBottomSheetBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun setItem(item: BottomSheetModel) {
            bind.item = item
            bind.root.setOnClickListener { onClick(item.title) }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BottomSheetModel>() {
            override fun areItemsTheSame(
                oldItem: BottomSheetModel,
                newItem: BottomSheetModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BottomSheetModel,
                newItem: BottomSheetModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}