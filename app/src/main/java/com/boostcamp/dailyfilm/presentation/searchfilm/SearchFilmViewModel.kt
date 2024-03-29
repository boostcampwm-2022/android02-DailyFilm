package com.boostcamp.dailyfilm.presentation.searchfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchFilmViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val dottedDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    var startAt: Long? = null
        private set
    var endAt: Long? = null
        private set
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: error("Unknown User")

    private val _itemListFlow = MutableStateFlow<List<DailyFilmItem?>>(emptyList())
    val itemListFlow: StateFlow<List<DailyFilmItem?>> = _itemListFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SearchEvent>()
    val eventFlow: SharedFlow<SearchEvent> = _eventFlow.asSharedFlow()

    private val _startDateFlow = MutableStateFlow<String?>(null)
    val startDateFlow: StateFlow<String?> = _startDateFlow.asStateFlow()

    private val _endDateFlow = MutableStateFlow<String?>(null)
    val endDateFlow: StateFlow<String?> = _endDateFlow.asStateFlow()

    fun searchDateRange(startAt: Long, endAt: Long) {
        this.startAt = startAt
        this.endAt = endAt

        viewModelScope.launch {
            _startDateFlow.tryEmit(dottedDateFormat.format(startAt))
            _endDateFlow.tryEmit(dottedDateFormat.format(endAt))

            val start = dateFormat.format(startAt)
            val end = dateFormat.format(endAt)
            val startYear = start.substring(0, 4).toInt()
            val endYear = end.substring(0, 4).toInt()

            for (year in startYear..endYear) {
                if (syncRepository.isSynced(year).not()) {
                    val startDate = "${year}0101"
                    val endDate = "${year}1231"
                    syncRepository.addSyncedYear(year)
                    syncRepository.startSync(userId, startDate, endDate)
                }
            }

            _itemListFlow.emit(calendarRepository.loadFilm(start, end))
        }
    }

    fun searchKeyword(query: String) {
        viewModelScope.launch {
            if (startAt != null && endAt != null) {
                _itemListFlow.emit(
                    calendarRepository.loadFilm(dateFormat.format(startAt), dateFormat.format(endAt))
                        .filter { it?.text?.contains(query) ?: false }
                )
            }
        }
    }

    fun onClickItem(index: Int) {
        event(SearchEvent.ItemClickEvent(index))
    }

    private fun event(event: SearchEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }
}

sealed class SearchEvent {
    data class ItemClickEvent(val index: Int) : SearchEvent()
}
