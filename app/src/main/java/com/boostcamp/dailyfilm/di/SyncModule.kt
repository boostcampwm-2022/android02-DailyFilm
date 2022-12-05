package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.sync.SyncDataSource
import com.boostcamp.dailyfilm.data.sync.SyncRemoteDataSource
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.boostcamp.dailyfilm.data.sync.SyncRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideSyncRepository(syncDataSource: SyncDataSource, calendarDataSource: CalendarDataSource): SyncRepository =
        SyncRepositoryImpl(syncDataSource, calendarDataSource)

    @Provides
    @Singleton
    fun provideSyncDataSource(): SyncDataSource = SyncRemoteDataSource()
}
