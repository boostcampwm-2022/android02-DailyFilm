package com.boostcamp.dailyfilm.presentation.selectvideo.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class VideoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<VideoLoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = VideoLoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(
        holder: VideoLoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}