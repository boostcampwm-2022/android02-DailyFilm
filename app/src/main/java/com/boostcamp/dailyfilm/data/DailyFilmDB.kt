package com.boostcamp.dailyfilm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import com.boostcamp.dailyfilm.data.model.CachedVideoEntity
import com.boostcamp.dailyfilm.data.model.FilmEntity
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao

@Database(
    entities = [FilmEntity::class, CachedVideoEntity::class],
    version = 2,
    exportSchema = false
)

abstract class DailyFilmDB : RoomDatabase() {
    companion object {
        fun create(context: Context): DailyFilmDB {
            val databaseBuilder =
                Room.databaseBuilder(context, DailyFilmDB::class.java, "dailyfilm.db")
            return databaseBuilder.fallbackToDestructiveMigration().build()
        }
    }

    abstract fun calendarDao(): CalendarDao

    abstract fun localUriDao(): LocalUriDao
}
