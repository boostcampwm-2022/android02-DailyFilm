package com.boostcamp.dailyfilm.data.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.boostcamp.dailyfilm.data.model.FilmEntity

@Database(
    entities = [FilmEntity::class],
    version = 1,
    exportSchema = false
)

abstract class CalendarDB : RoomDatabase() {
    companion object {
        fun create(context: Context): CalendarDB {
            val databaseBuilder =
                Room.databaseBuilder(context, CalendarDB::class.java, "calendar.db")
            return databaseBuilder.fallbackToDestructiveMigration().build()
        }
    }

    abstract fun calendarDao(): CalendarDao
}
