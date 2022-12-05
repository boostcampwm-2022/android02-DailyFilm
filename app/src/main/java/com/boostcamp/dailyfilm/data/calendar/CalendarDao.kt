package com.boostcamp.dailyfilm.data.calendar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boostcamp.dailyfilm.data.model.FilmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query(
        "SELECT * FROM film_entity " +
            "WHERE updateDate BETWEEN :startAt AND :endAt "
    )
    fun loadFilm(startAt: Int, endAt: Int): Flow<List<FilmEntity?>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(filmEntityList: List<FilmEntity?>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filmEntity: FilmEntity)
}
