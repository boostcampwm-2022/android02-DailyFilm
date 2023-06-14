package com.boostcamp.dailyfilm.data.calendar

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.boostcamp.dailyfilm.data.calendar.CalendarPagingSource.Companion.PAGING_SIZE
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CalendarRepository {
    fun loadFilmInfo(startAt: String, endAt: String): Flow<List<DailyFilmItem?>>

    suspend fun loadFilm(startAt: String, endAt: String): List<DailyFilmItem?>

    suspend fun loadPagedFilm(startAt: String, endAt: String): Flow<PagingData<DailyFilmItem>>

    suspend fun deleteAllData(): Result<Unit>
}

class CalendarRepositoryImpl(
    private val calendarLocalDataSource: CalendarDataSource,
) : CalendarRepository {

    override fun loadFilmInfo(startAt: String, endAt: String): Flow<List<DailyFilmItem?>> =
        calendarLocalDataSource.loadFilmFlow(startAt.toInt(), endAt.toInt()).map { filmList ->
            filmList.map { filmEntity ->
                filmEntity?.mapToDailyFilmItem()
            }
        }

    override suspend fun loadFilm(startAt: String, endAt: String): List<DailyFilmItem?> =
        calendarLocalDataSource.loadFilm(startAt.toInt(), endAt.toInt()).map { filmEntity ->
            filmEntity?.mapToDailyFilmItem()
        }

    override suspend fun loadPagedFilm(startAt: String, endAt: String): Flow<PagingData<DailyFilmItem>> =
        Pager(config = PagingConfig(pageSize = PAGING_SIZE)) {
            CalendarPagingSource(startAt.toInt(), endAt.toInt(), calendarLocalDataSource)
        }.flow

    override suspend fun deleteAllData(): Result<Unit> =
        calendarLocalDataSource.deleteAllData()
}
