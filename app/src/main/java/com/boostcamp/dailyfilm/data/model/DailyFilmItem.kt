package com.boostcamp.dailyfilm.data.model

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
}
