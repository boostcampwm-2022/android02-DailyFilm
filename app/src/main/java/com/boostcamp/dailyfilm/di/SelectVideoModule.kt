package com.boostcamp.dailyfilm.di

import android.content.ContentResolver
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SelectVideoModule {
    @Provides
    @Singleton
    fun provideGalleryVideoRepository(contentResolver: ContentResolver): GalleryVideoRepository =
        GalleryVideoRepositoryImpl(contentResolver)
}