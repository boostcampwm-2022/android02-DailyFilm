package com.boostcamp.dailyfilm.presentation.uploadfilm.model

import android.net.Uri
import android.os.Parcelable
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateAndVideoModel(
    val uri: Uri,
    val uploadDate: String
) : Parcelable {
    fun getDateModel() = DateModel(
        uploadDate.substring(0, 4),
        uploadDate.substring(4, 6),
        uploadDate.substring(6, 8)
    )
}

