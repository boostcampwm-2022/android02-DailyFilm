package com.boostcamp.dailyfilm.presentation.playfilm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_FILM_ARRAY
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_DATE_MODEL_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayFilmActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val dateModelIndex = savedStateHandle.get<Int>(KEY_DATE_MODEL_INDEX)
    val calendarIndex = savedStateHandle.get<Int>(KEY_CALENDAR_INDEX)
    val filmArray = savedStateHandle.get<ArrayList<DateModel>>(KEY_FILM_ARRAY)
}