package com.boostcamp.dailyfilm.presentation.selectvideo

import com.boostcamp.dailyfilm.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface VideoSelectListener {

    val selectedVideo: StateFlow<VideoItem?>

    val viewTreeLifecycleScope: CoroutineScope

    fun chooseVideo(videoItem: VideoItem?)

}