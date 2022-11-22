package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.lifecycle.MutableLiveData
import com.boostcamp.dailyfilm.data.model.VideoItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface VideoSelectListener {

    val selectedVideo : StateFlow<VideoItem?>

    fun chooseVideo(videoItem: VideoItem)
}