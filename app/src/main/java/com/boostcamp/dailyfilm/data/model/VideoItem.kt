package com.boostcamp.dailyfilm.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoItem(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int
) : Parcelable

