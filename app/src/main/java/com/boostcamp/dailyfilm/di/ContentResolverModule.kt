package com.boostcamp.dailyfilm.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ContentResolverModule {

    @Singleton
    @Provides
    fun provideContentResolver(@ApplicationContext context:Context):ContentResolver =
        context.contentResolver

}