package com.boostcamp.dailyfilm.data.calendar

import com.boostcamp.dailyfilm.data.model.FilmEntity
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface CalendarDataSource {
    fun loadFilm(startAt: Int, endAt: Int): Flow<List<FilmEntity?>>

    suspend fun insertFilm(film: FilmEntity)

    suspend fun insertAllFilm(filmList: List<FilmEntity>)
}

class CalendarLocalDataSource(
    private val calendarDao: CalendarDao
) : CalendarDataSource {

    override fun loadFilm(startAt: Int, endAt: Int): Flow<List<FilmEntity?>> =
        calendarDao.loadFilm(startAt, endAt)

    override suspend fun insertFilm(film: FilmEntity) {
        calendarDao.insert(film)
    }

    override suspend fun insertAllFilm(filmList: List<FilmEntity>) {
        calendarDao.insertAll(filmList)
    }

}
