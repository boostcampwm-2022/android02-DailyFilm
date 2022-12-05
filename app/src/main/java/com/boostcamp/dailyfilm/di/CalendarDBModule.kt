package com.boostcamp.dailyfilm.di

import android.content.Context
import com.boostcamp.dailyfilm.data.calendar.CalendarDB
import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarDBModule {

    @Provides
    @Singleton
    fun provideDB(
        @ApplicationContext context: Context
    ): CalendarDB = CalendarDB.create(context)

    @Provides
    @Singleton
    fun provideCalendarDao(
        calendarDB: CalendarDB
    ): CalendarDao = calendarDB.calendarDao()
}
