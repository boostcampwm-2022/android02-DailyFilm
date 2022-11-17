package com.boostcamp.dailyfilm.data.model

import android.net.Uri

data class VideoItem(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int
)
