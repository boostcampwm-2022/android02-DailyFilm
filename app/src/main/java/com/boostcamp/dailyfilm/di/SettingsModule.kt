package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import com.boostcamp.dailyfilm.data.settings.SettingsDataSource
import com.boostcamp.dailyfilm.data.settings.SettingsLocalDataSource
import com.boostcamp.dailyfilm.data.settings.SettingsRepository
import com.boostcamp.dailyfilm.data.settings.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Singleton
    @Provides
    fun provideSettingsDataSource(calendarDao: CalendarDao): SettingsDataSource =
        SettingsLocalDataSource(calendarDao)

    @Singleton
    @Provides
    fun provideSettingsRepository(
        settingsLocalDataSource: SettingsDataSource
    ): SettingsRepository = SettingsRepositoryImpl(settingsLocalDataSource)

}