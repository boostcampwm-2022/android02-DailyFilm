package com.boostcamp.dailyfilm.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.boostcamp.dailyfilm.data.dataStore.PreferencesKeys.SPEED_INDEX_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val userFastFlow: Flow<Int?> = dataStore.data.map {
        it[SPEED_INDEX_KEY]
    }

    suspend fun editFast(index: Int) {
        dataStore.edit {
            it[SPEED_INDEX_KEY] = index
        }
    }
}