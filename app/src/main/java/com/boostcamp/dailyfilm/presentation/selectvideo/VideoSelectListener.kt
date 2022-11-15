package com.boostcamp.dailyfilm.presentation.selectvideo

import com.boostcamp.dailyfilm.data.model.VideoItem

interface VideoSelectListener {
    fun chooseVideo(videoItem: VideoItem)
}