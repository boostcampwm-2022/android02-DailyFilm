package com.boostcamp.dailyfilm.presentation.playfilm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import javax.inject.Inject

class PlayFilmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val dateModel = savedStateHandle.get<DateModel>(PlayFilmFragment.KEY_DATE_MODEL)
        ?: throw IllegalStateException("PlayFilmViewModel - DateModel is null")
}