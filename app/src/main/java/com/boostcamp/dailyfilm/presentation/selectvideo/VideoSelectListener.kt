package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.lifecycle.MutableLiveData
import com.boostcamp.dailyfilm.data.model.VideoItem

interface VideoSelectListener {

    val selectedVideo : MutableLiveData<VideoItem>

    fun chooseVideo(videoItem: VideoItem)
}