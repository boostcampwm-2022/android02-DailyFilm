package com.boostcamp.dailyfilm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film_entity")
data class FilmEntity(
    val videoUrl: String = "",
    val text: String = "",
    @PrimaryKey val updateDate: Int = 0
) {
    fun mapToDailyFilmItem(): DailyFilmItem =
        DailyFilmItem(
            videoUrl,
            text,
            updateDate.toString()
        )
}
