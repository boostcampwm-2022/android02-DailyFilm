package com.boostcamp.dailyfilm.di

import android.content.Context
import com.boostcamp.dailyfilm.data.DailyFilmDB
import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DailyFilmDBModule {

    @Provides
    @Singleton
    fun provideDB(
        @ApplicationContext context: Context
    ): DailyFilmDB = DailyFilmDB.create(context)

    @Provides
    @Singleton
    fun provideCalendarDao(
        dailyFilmDB: DailyFilmDB
    ): CalendarDao = dailyFilmDB.calendarDao()

    @Provides
    @Singleton
    fun provideLocalUriDao(
        dailyFilmDB: DailyFilmDB
    ): LocalUriDao = dailyFilmDB.localUriDao()

}
