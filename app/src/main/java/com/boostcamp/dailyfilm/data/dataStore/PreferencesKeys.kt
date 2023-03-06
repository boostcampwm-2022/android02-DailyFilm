package com.boostcamp.dailyfilm.data.dataStore

import androidx.datastore.preferences.core.intPreferencesKey

object PreferencesKeys {

    private const val SPEED_INDEX = "speed"

    val SPEED_INDEX_KEY = intPreferencesKey(SPEED_INDEX)
}