package com.boostcamp.dailyfilm.data.calendar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel

@Dao
interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dateModelList: List<DateModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dateModel: DateModel)
}