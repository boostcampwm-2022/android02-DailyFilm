package com.boostcamp.dailyfilm.data.model

import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel

data class DailyFilmItem(
    val videoUrl: String = "",
    val text: String = "",
    val updateDate: String = ""
) {
    fun mapToFilmEntity(): FilmEntity =
        FilmEntity(
            videoUrl,
            text,
            updateDate.toInt()
        )

    fun toDateModel(): DateModel =
        DateModel(
            updateDate.substring(0, 4),
            updateDate.substring(4, 6),
            updateDate.substring(6),
            text,
            videoUrl
        )
}
