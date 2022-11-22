package com.boostcamp.dailyfilm.presentation.playfilm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayFilmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val dateModel = savedStateHandle.get<DateModel>(DateFragment.KEY_DATE_MODEL)

    init {
        Log.d("URL_TEST_ViewModel", "initAdapter: $dateModel")
    }
}