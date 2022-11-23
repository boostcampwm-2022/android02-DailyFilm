package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSourceImpl
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
    fun provideCalenderDataSource(): CalendarDataSource = CalendarDataSourceImpl()

    @Singleton
    @Provides
    fun provideCalenderRepository(
        calendarDataSource: CalendarDataSource
    ): CalendarRepository = CalendarRepositoryImpl(calendarDataSource)
}