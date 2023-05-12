package com.boostcamp.dailyfilm.data.dataStore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {

    private const val SPEED_INDEX = "speed"
    private const val CACHED_YEAR = "year"

    val SPEED_INDEX_KEY = intPreferencesKey(SPEED_INDEX)
    val CACHED_YEAR_KEY = stringSetPreferencesKey(CACHED_YEAR)
}
