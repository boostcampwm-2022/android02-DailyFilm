package com.boostcamp.dailyfilm.data.uploadfilm.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalUriDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cachedVideoEntity: CachedVideoEntity)

    @Query("SELECT * FROM cached_video_entity WHERE updateDate = :updateDate LIMIT 1")
    suspend fun loadFilm(updateDate: Int): CachedVideoEntity

}
