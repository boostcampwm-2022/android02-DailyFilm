package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepositoryImpl
import com.boostcamp.dailyfilm.data.uploadfilm.local.LocalUriDao
import com.boostcamp.dailyfilm.data.uploadfilm.local.UploadFilmLocalDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.remote.UploadFilmRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadFilmModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalUploadDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteUploadDataSource

    @Singleton
    @LocalUploadDataSource
    @Provides
    fun provideUploadLocalDataSource(localUriDao: LocalUriDao): UploadFilmDataSource =
        UploadFilmLocalDataSource(localUriDao)

    @Singleton
    @RemoteUploadDataSource
    @Provides
    fun provideUploadRemoteDataSource(): UploadFilmDataSource =
        UploadFilmRemoteDataSource()

    @Provides
    @Singleton
    fun provideUploadRepository(
        @LocalUploadDataSource uploadFilmLocalDataSource: UploadFilmDataSource,
        @RemoteUploadDataSource uploadFilmRemoteDataSource: UploadFilmDataSource,
        calendarDataSource: CalendarDataSource
    ): UploadFilmRepository =
        UploadFilmRepositoryImpl(uploadFilmLocalDataSource, uploadFilmRemoteDataSource, calendarDataSource)
}
