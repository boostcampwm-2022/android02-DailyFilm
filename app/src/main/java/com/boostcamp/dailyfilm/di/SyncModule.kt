package com.boostcamp.dailyfilm.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    fun provideSyncRepository(
        syncDataSource: SyncDataSource,
        calendarDataSource: CalendarDataSource,
        dataStore: DataStore<Preferences>
    ): SyncRepository = SyncRepositoryImpl(syncDataSource, calendarDataSource, dataStore)

    @Provides
    @Singleton
    fun provideSyncDataSource(): SyncDataSource = SyncRemoteDataSource()
}
