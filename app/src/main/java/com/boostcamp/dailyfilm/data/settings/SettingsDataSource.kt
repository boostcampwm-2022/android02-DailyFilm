package com.boostcamp.dailyfilm.data.settings

import com.boostcamp.dailyfilm.data.calendar.CalendarDao
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SettingsDataSource {

    fun deleteAllData(): Flow<Result<Unit>>

}

class SettingsLocalDataSource(
    private val calendarDao: CalendarDao
) : SettingsDataSource {

    override fun deleteAllData(): Flow<Result<Unit>> = callbackFlow {
        runCatching {
            calendarDao.deleteAll()
        }.onSuccess {
            trySend(Result.Success(Unit))
        }.onFailure { exception ->
            trySend(Result.Error(exception))
        }

        awaitClose()
    }
}