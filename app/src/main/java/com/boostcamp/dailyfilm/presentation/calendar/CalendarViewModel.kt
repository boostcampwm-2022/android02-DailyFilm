package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


sealed class Event {
    data class UploadSuccess(val dateModel: DateModel) : Event()
    data class UpdateMonth(val month: String) : Event()
}

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private lateinit var item: DateModel
    private var calendar = Calendar.getInstance(Locale.getDefault())

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow.asSharedFlow()

    private val _calendarFlow = MutableStateFlow("${calendar.get(Calendar.MONTH) + 1}월")
    val calendarFlow: StateFlow<String> = _calendarFlow.asStateFlow()

    fun onDateItemClicked(item: DateModel) {
        this.item = item
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun getViewPagerPosition(position: Int) {

        viewModelScope.launch {
            calendar = Calendar.getInstance(Locale.getDefault()).apply {
                add(Calendar.MONTH, position - CalendarPagerAdapter.START_POSITION)
            }
            _calendarFlow.emit("${calendar.get(Calendar.MONTH) + 1}월")
        }
    }

    fun uploadClicked() {
        event(Event.UploadSuccess(item))
    }
}
