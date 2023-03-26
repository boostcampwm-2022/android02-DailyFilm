package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.settings.*
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
    fun provideSettingsDataSource(settingsDao: SettingsDao): SettingsDataSource =
        SettingsLocalDataSource(settingsDao)

    @Singleton
    @Provides
    fun provideSettingsRepository(
        settingsLocalDataSource: SettingsDataSource
    ): SettingsRepository = SettingsRepositoryImpl(settingsLocalDataSource)

}