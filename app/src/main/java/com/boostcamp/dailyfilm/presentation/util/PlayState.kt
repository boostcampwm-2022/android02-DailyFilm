package com.boostcamp.dailyfilm.presentation.util

import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel

sealed class PlayState {
    object Uninitialized : PlayState()
    object Loading : PlayState()
    object Playing : PlayState()
    data class Deleted(val dateModel: DateModel) : PlayState()
    data class Failure(val throwable: Throwable) : PlayState()
}
