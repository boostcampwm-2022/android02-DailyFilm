package com.boostcamp.dailyfilm.data.sync

import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource

interface SyncRepository {
    suspend fun startSync(userId: String, startAt: String, endAt: String)

    fun isSynced(year: Int): Boolean

    fun addSyncedYear(year: Int)
}

class SyncRepositoryImpl(
    private val syncDataSource: SyncDataSource,
    private val calendarDataSource: CalendarDataSource
) : SyncRepository {

    private val syncedYearSet = HashSet<Int>()

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

    override fun isSynced(year: Int): Boolean = syncedYearSet.contains(year)

    override fun addSyncedYear(year: Int) {
        syncedYearSet.add(year)
    }
}
