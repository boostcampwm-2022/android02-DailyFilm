package com.boostcamp.dailyfilm.data.calendar

import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun loadFilmInfo(userId: String, startAt: String, endAt: String): Flow<DailyFilmItem?>
}

class CalendarRepositoryImpl(
    private val calendarDataSource: CalendarDataSource
) : CalendarRepository {
    override fun loadFilmInfo(userId: String, startAt: String, endAt: String): Flow<DailyFilmItem?> =
        calendarDataSource.loadFilmInfo(userId, startAt, endAt)
}
