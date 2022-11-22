package com.boostcamp.dailyfilm.presentation.uploadfilm.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateAndVideoModel(
    val uri: Uri,
    val uploadDate: String
) : Parcelable
