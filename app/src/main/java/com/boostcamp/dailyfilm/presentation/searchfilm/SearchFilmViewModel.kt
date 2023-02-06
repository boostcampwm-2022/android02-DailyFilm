package com.boostcamp.dailyfilm.presentation.searchfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchFilmViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    var startAt: Long? = null
        private set
    var endAt: Long? = null
        private set

    private val _itemListFlow = MutableStateFlow<List<DailyFilmItem?>>(emptyList())
    val itemListFlow: StateFlow<List<DailyFilmItem?>> = _itemListFlow.asStateFlow()

    fun searchDateRange(startAt: Long, endAt: Long) {
        this.startAt = startAt
        this.endAt = endAt

        viewModelScope.launch {
            val start = dateFormat.format(startAt)
            val end = dateFormat.format(endAt)

            calendarRepository.loadFilmInfo(start, end).collectLatest { itemList ->
                _itemListFlow.emit(itemList)
            }
        }
    }
}
