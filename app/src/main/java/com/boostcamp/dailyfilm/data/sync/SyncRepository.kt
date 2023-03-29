package com.boostcamp.dailyfilm.data.sync

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.boostcamp.dailyfilm.data.calendar.CalendarDataSource
import com.boostcamp.dailyfilm.data.dataStore.PreferencesKeys.CACHED_YEAR_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface SyncRepository {
    suspend fun startSync(userId: String, startAt: String, endAt: String)

    fun isSynced(year: Int): Boolean

    suspend fun addSyncedYear(year: Int)

    suspend fun saveSyncedYear() {
    }
}

class SyncRepositoryImpl(
    private val syncDataSource: SyncDataSource,
    private val calendarDataSource: CalendarDataSource,
    private val dataStore: DataStore<Preferences>
) : SyncRepository {

    private val syncedYearSet = mutableSetOf<String>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map {
                it[CACHED_YEAR_KEY] ?: setOf()
            }.collectLatest {
                syncedYearSet.addAll(it)
                Log.d("SearchList", "Synced: $it")
                Log.d("SearchList", "Synced: $syncedYearSet")
            }
        }
    }

    override suspend fun startSync(userId: String, startAt: String, endAt: String) {
        val filmItemList = syncDataSource.loadFilmInfo(userId, startAt, endAt) ?: return
        calendarDataSource.insertAllFilm(
            filmItemList.filterNotNull().map { filmItem ->
                filmItem.mapToFilmEntity()
            }
        )
    }

    override fun isSynced(year: Int): Boolean = syncedYearSet.contains(year.toString())

    override suspend fun addSyncedYear(year: Int) {
        syncedYearSet.add(year.toString())
    }

    override suspend fun saveSyncedYear() {
        dataStore.edit { it[CACHED_YEAR_KEY] = syncedYearSet.toSet() }
    }
}
