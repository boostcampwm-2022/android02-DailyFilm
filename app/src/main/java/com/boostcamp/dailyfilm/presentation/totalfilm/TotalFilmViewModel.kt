package com.boostcamp.dailyfilm.presentation.totalfilm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TotalFilmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val filmArray = savedStateHandle.get<ArrayList<DateModel>>(CalendarActivity.KEY_FILM_ARRAY)

    init {
        Log.d("TotalFilmViewModel", "TotalFilmViewModel: $filmArray")
    }
}
