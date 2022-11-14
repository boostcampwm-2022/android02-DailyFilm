package com.boostcamp.dailyfilm.di

import com.boostcamp.dailyfilm.data.login.LoginRepository
import com.boostcamp.dailyfilm.data.login.LoginRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginRepository(): LoginRepository =
        LoginRepositoryImpl()
}