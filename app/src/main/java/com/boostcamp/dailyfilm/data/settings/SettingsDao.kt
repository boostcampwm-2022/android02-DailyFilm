package com.boostcamp.dailyfilm.data.settings

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SettingsDao {

    @Query("DELETE FROM film_entity")
    suspend fun deleteAll()

}