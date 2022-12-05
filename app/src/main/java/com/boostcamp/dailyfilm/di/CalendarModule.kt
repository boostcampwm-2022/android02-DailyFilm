package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.calendar.CalendarLocalDataSource
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.calendar.CalendarRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CalendarModule {

    @Singleton
    @Provides
    fun provideCalenderDataSource(calendarDao: CalendarDao): CalendarDataSource = CalendarLocalDataSource(calendarDao)

    @Singleton
    @Provides
    fun provideCalenderRepository(
        calendarLocalDataSource: CalendarDataSource
    ): CalendarRepository =
        CalendarRepositoryImpl(calendarLocalDataSource)
}
