package com.boostcamp.dailyfilm.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeVideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val infoItem = savedStateHandle.get<DateAndVideoModel>(SelectVideoActivity.DATE_VIDEO_ITEM)

}

