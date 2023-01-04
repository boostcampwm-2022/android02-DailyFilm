package com.boostcamp.dailyfilm.presentation.selectvideo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ItemVideoLoadStateBinding

class VideoLoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_video_load_state, parent, false)
) {
    private val binding = ItemVideoLoadStateBinding.bind(itemView)
    private val progressBar: ProgressBar = binding.loadStateProgress
    private val errorMsg: TextView = binding.loadStateErrorMessage
    private val retry: Button = binding.loadStateRetry
        .also {
            it.setOnClickListener { retry() }
        }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error

    }
}