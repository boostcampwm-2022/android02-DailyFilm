package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private var item: DateModel? = null
    private val localeCalendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow.asSharedFlow()

    private val _calendarFlow = MutableStateFlow("${localeCalendar.get(Calendar.YEAR)}년 ${localeCalendar.get(Calendar.MONTH) + 1}월")
    val calendarFlow: StateFlow<String> = _calendarFlow.asStateFlow()

    private val _isTodayFlow = MutableStateFlow(DateState.TODAY)
    val isTodayFlow: StateFlow<DateState> = _isTodayFlow.asStateFlow()

    private val _userFlow = MutableStateFlow(FirebaseAuth.getInstance().currentUser)
    val userFlow: StateFlow<FirebaseUser?> = _userFlow.asStateFlow()

    fun changeSelectedItem(item: DateModel?) {
        this.item = item
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun getViewPagerPosition(position: Int) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance(Locale.getDefault()).apply {
                add(Calendar.MONTH, position - CalendarPagerAdapter.START_POSITION)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            _calendarFlow.emit("${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월")

            val diff = calendar.timeInMillis - localeCalendar.timeInMillis
            _isTodayFlow.emit(
                when {
                    diff < 0L -> {
                        DateState.BEFORE
                    }
                    diff == 0L -> {
                        DateState.TODAY
                    }
                    else -> {
                        DateState.AFTER
                    }
                }
            )
        }
    }

    fun uploadClicked() {
        event(Event.UploadSuccess(item))
    }
}

sealed class Event {
    data class UploadSuccess(val dateModel: DateModel?) : Event()
    data class UpdateMonth(val month: String) : Event()
}
