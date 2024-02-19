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
            "WHERE updateDate BETWEEN :startAt AND :endAt ",
    )
    fun loadFilmFlow(startAt: Int, endAt: Int): Flow<List<FilmEntity?>>

    @Query(
        "SELECT * FROM film_entity " +
            "WHERE updateDate BETWEEN :startAt AND :endAt ",
    )
    suspend fun loadFilm(startAt: Int, endAt: Int): List<FilmEntity?>

    @Query(
        "SELECT * FROM film_entity " +
            "WHERE updateDate BETWEEN :startAt AND :endAt " +
            "ORDER BY updateDate ASC LIMIT :count OFFSET :page",
    )
    suspend fun loadPagedFilm(startAt: Int, endAt: Int, page: Int, count: Int = 10): List<FilmEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(filmEntityList: List<FilmEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filmEntity: FilmEntity)

    @Query("DELETE FROM film_entity")
    suspend fun deleteAll()
}
