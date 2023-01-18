package com.boostcamp.dailyfilm.data.uploadfilm.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity

@Dao
interface LocalUriDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cachedVideoEntity: CachedVideoEntity)

    @Query("SELECT * FROM cached_video_entity WHERE updateDate = :updateDate LIMIT 1")
    suspend fun loadFilm(updateDate: Int): CachedVideoEntity?

    @Query("DELETE FROM cached_video_entity WHERE updateDate = :updateDate")
    suspend fun deleteVideoFilm(updateDate: Int)

    @Query("DELETE FROM film_entity WHERE updateDate = :updateDate")
    suspend fun deleteFilm(updateDate: Int)
}
