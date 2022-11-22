package com.boostcamp.dailyfilm.presentation.calendar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DateModel(
    val year: String,
    val month: String,
    val day: String,
    val imgUrl: String?
) : Parcelable{
    fun getDate() =
        year + month.padStart(2, '0') + day.padStart(2, '0')

}
