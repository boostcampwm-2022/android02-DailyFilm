package com.boostcamp.dailyfilm.presentation.selectvideo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.paging.PagingDataAdapter
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.data.model.VideoItem

class SelectVideoAdapter(
    private val videoSelectListener: VideoSelectListener
) : PagingDataAdapter<VideoItem, SelectVideoViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectVideoViewHolder {
        return SelectVideoViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_select_video,
                parent,
                false
            ),
            videoSelectListener
        )
    }

    override fun onBindViewHolder(holder: SelectVideoViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
                return oldItem == newItem
            }
        }

    }

}