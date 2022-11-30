package com.boostcamp.dailyfilm.presentation.calendar.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "dateModel")
data class DateModel(
    @PrimaryKey val year: String,
    val month: String,
    val day: String,
    val text: String? = null,
    val videoUrl: String? = null
) : Parcelable{
    fun getDate() =
        year + month.padStart(2, '0') + day.padStart(2, '0')
}
