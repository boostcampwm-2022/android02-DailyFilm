package com.boostcamp.dailyfilm.data.settings


import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun deleteAllData(): Flow<Result<Unit>>
}

class SettingsRepositoryImpl(
    private val settingsLocalDataSource: SettingsDataSource
) : SettingsRepository {

    override fun deleteAllData(): Flow<Result<Unit>> =
        settingsLocalDataSource.deleteAllData()

}