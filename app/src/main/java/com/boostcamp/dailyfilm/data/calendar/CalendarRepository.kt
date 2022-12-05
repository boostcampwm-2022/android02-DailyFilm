package com.boostcamp.dailyfilm.data.calendar

import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CalendarRepository {
    fun loadFilmInfo(startAt: String, endAt: String): Flow<List<DailyFilmItem?>>
}

class CalendarRepositoryImpl(
    private val calendarLocalDataSource: CalendarDataSource
) : CalendarRepository {

    override fun loadFilmInfo(startAt: String, endAt: String): Flow<List<DailyFilmItem?>> =
        calendarLocalDataSource.loadFilm(startAt.toInt(), endAt.toInt()).map { filmList ->
            filmList.map { film ->
                film?.mapToDailyFilmItem()
            }
        }
}
