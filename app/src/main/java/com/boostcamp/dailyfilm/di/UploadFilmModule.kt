package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmDataSource
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmDataSourceImpl
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepositoryImpl
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UploadFilmModule {

    @Provides
    @Singleton
    fun provideUploadRepository() : UploadFilmRepository =
        UploadFilmRepositoryImpl(UploadFilmDataSourceImpl())

}