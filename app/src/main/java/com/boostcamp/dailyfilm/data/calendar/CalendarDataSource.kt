package com.boostcamp.dailyfilm.data.calendar

import com.boostcamp.dailyfilm.data.model.FilmEntity
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface CalendarDataSource {
    fun loadFilmFlow(startAt: Int, endAt: Int): Flow<List<FilmEntity?>>

    suspend fun loadFilm(startAt: Int, endAt: Int): List<FilmEntity?>

    suspend fun loadPagedFilm(startAt: Int, endAt: Int, page: Int): List<FilmEntity?>

    suspend fun insertFilm(film: FilmEntity)

    suspend fun insertAllFilm(filmList: List<FilmEntity>)

    suspend fun deleteAllData(): Result<Unit>
}

class CalendarLocalDataSource(
    private val calendarDao: CalendarDao,
) : CalendarDataSource {

    override fun loadFilmFlow(startAt: Int, endAt: Int): Flow<List<FilmEntity?>> =
        calendarDao.loadFilmFlow(startAt, endAt)

    override suspend fun loadFilm(startAt: Int, endAt: Int): List<FilmEntity?> =
        calendarDao.loadFilm(startAt, endAt)

    override suspend fun loadPagedFilm(startAt: Int, endAt: Int, page: Int): List<FilmEntity?> =
        calendarDao.loadPagedFilm(startAt, endAt, page)

    override suspend fun insertFilm(film: FilmEntity) {
        calendarDao.insert(film)
    }

    override suspend fun insertAllFilm(filmList: List<FilmEntity>) {
        calendarDao.insertAll(filmList)
    }

    override suspend fun deleteAllData() = suspendCoroutine { continuation ->
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                calendarDao.deleteAll()
            }.onSuccess {
                continuation.resume(Result.Success(Unit))
            }.onFailure { exception ->
                continuation.resume(Result.Error(exception))
            }
        }
    }
}
