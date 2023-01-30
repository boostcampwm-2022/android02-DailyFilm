package com.boostcamp.dailyfilm.presentation.trimvideo

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrimVideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val infoItem = savedStateHandle.get<DateAndVideoModel>(SelectVideoActivity.DATE_VIDEO_ITEM)
    val dateModel = savedStateHandle.get<DateModel>(CalendarActivity.KEY_DATE_MODEL)
    val calendarIndex = savedStateHandle.get<Int>(DateFragment.KEY_CALENDAR_INDEX)
    val editFlag = savedStateHandle.get<Boolean>(CalendarActivity.KEY_EDIT_FLAG)

    private val HD_WIDTH = 1280
    private val _eventFlow = MutableSharedFlow<Event<TrimVideoEvent>>(replay = 1)
    val eventFlow: SharedFlow<Event<TrimVideoEvent>> = _eventFlow.asSharedFlow()

    private fun event(event: TrimVideoEvent) {
        viewModelScope.launch {
            _eventFlow.emit(Event(event))
        }
    }

    fun initOpenTrimVideo() {
        event(TrimVideoEvent.InitOpenTrimVideo(infoItem!!))
    }

    fun moveToSelectVideo() {
        event(TrimVideoEvent.BackButtonResult(infoItem!!.getDateModel()))
    }

    fun moveToUpload(uriString: Uri, startTime: Long) {
        event(TrimVideoEvent.NextButtonResult(DateAndVideoModel(uriString, infoItem!!.uploadDate), startTime))
    }

    fun openTrimActivity(startForResult: ActivityResultLauncher<Intent>,videoWidthAndHeight:IntArray) {
        val videoWidth = videoWidthAndHeight.first()
        val videoHeight = videoWidthAndHeight.last()
        val (newWidth, newHeight) = getCompressedWidthAndHeight(videoWidth, videoHeight)
        event(TrimVideoEvent.OpenTrimVideoResult(infoItem!!,startForResult,newWidth,newHeight))
    }

    private fun getCompressedWidthAndHeight(videoWidth: Int, videoHeight: Int): IntArray {
        return if (videoWidth > videoHeight) {
            val newWidth = if (videoWidth > HD_WIDTH) HD_WIDTH else videoWidth
            val newHeight =
                if (videoWidth > HD_WIDTH) (videoHeight / (videoWidth.toDouble() / HD_WIDTH)).toInt() else videoHeight

            intArrayOf(newWidth, newHeight)
        } else {
            val newWidth =
                if (videoHeight > HD_WIDTH) (videoWidth / (videoHeight.toDouble() / HD_WIDTH)).toInt() else videoWidth
            val newHeight = if (videoHeight > HD_WIDTH) HD_WIDTH else videoHeight

            intArrayOf(newWidth, newHeight)
        }
    }
}

sealed class TrimVideoEvent {
    data class NextButtonResult(val dateAndVideoModelItem: DateAndVideoModel, val startTime: Long) : TrimVideoEvent()
    data class BackButtonResult(val dateModel: DateModel) : TrimVideoEvent()
    data class InitOpenTrimVideo(val dateModel: DateAndVideoModel) :TrimVideoEvent()
    data class OpenTrimVideoResult(
        val dateModel: DateAndVideoModel,
        val startForResult: ActivityResultLauncher<Intent>,
        val newWidth: Int,
        val newHeight : Int
    ) : TrimVideoEvent()
}
