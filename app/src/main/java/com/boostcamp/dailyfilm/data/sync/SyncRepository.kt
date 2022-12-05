package com.boostcamp.dailyfilm.data.sync

import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource

interface SyncRepository {
    suspend fun startSync(userId: String, startAt: String, endAt: String)
}

class SyncRepositoryImpl(
    private val syncDataSource: SyncDataSource,
    private val calendarDataSource: CalendarDataSource
) : SyncRepository {

    override suspend fun startSync(userId: String, startAt: String, endAt: String) {
        syncDataSource.loadFilmInfo(userId, startAt, endAt).collect { filmItemList ->
            if (filmItemList == null) return@collect
            calendarDataSource.insertAllFilm(
                filmItemList.filterNotNull().map { filmItem ->
                    filmItem.mapToFilmEntity()
                }
            )
        }
    }
}
