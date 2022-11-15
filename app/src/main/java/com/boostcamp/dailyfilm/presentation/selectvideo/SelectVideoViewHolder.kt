package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.boostcamp.dailyfilm.databinding.ItemSelectVideoBinding
import com.boostcamp.dailyfilm.data.model.VideoItem

class SelectVideoViewHolder(
    private val binding:ItemSelectVideoBinding,
    private val videoSelectListener: VideoSelectListener
    ):ViewHolder(binding.root) {

    private var lifecycleOwner: LifecycleOwner? = null

    init {

        itemView.doOnAttach {
            lifecycleOwner = itemView.findViewTreeLifecycleOwner()
        }

        itemView.doOnDetach {
            lifecycleOwner = null
        }

    }

    fun bind(item:VideoItem){
        binding.item = item
        binding.lifecycleOwner = lifecycleOwner
        binding.clickListener = videoSelectListener
        binding.executePendingBindings()
    }

}