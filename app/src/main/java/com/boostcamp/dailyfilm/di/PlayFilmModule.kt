package com.boostcamp.dailyfilm.di

import android.content.Context
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmDataSource
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmRepository
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmRepositoryImpl
import com.boostcamp.dailyfilm.data.playfilm.local.PlayFilmLocalDataSource
import com.boostcamp.dailyfilm.data.playfilm.remote.PlayFilmRemoteDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayFilmModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalPlayFilmDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemotePlayFilmDataSource

    @Singleton
    @LocalPlayFilmDataSource
    @Provides
    fun providePlayFilmLocalDataSource(localUriDao: LocalUriDao): PlayFilmDataSource =
        PlayFilmLocalDataSource(localUriDao)

    @Singleton
    @RemotePlayFilmDataSource
    @Provides
    fun providePlayFilmRemoteDataSource(@ApplicationContext context: Context): PlayFilmDataSource =
        PlayFilmRemoteDataSource(context)

    @Singleton
    @Provides
    fun providePlayFilmRepository(
        @LocalPlayFilmDataSource playFilmLocalDataSource: PlayFilmDataSource,
        @RemotePlayFilmDataSource playFilmRemoteDataSource: PlayFilmDataSource
    ) : PlayFilmRepository = PlayFilmRepositoryImpl(playFilmLocalDataSource, playFilmRemoteDataSource)

}