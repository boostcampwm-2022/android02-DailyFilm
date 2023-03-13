package com.boostcamp.dailyfilm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.boostcamp.dailyfilm.data.dataStore.UserPreferencesRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_SPEED
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    private val Context.dataStore by preferencesDataStore(KEY_SPEED)

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(dataStore: DataStore<Preferences>) =
        UserPreferencesRepository(dataStore)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = context.dataStore

}