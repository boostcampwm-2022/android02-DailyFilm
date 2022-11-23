package com.boostcamp.dailyfilm.presentation.calendar

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DateViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR)
        ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    private val _dateFlow = MutableStateFlow(initialDateList())
    val dateFlow: StateFlow<List<DateModel>> = _dateFlow.asStateFlow()

    private var test: Boolean = true

    init {
        fetchCalendar()
    }

    fun fetchCalendar() {

        Log.d("DateViewModel", "fetchCalendar: ")

        val tempCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val prevCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }

        val prevMaxDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        prevCalendar.set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val startAt = dateFormat.format(prevCalendar.time)

        prevCalendar.add(Calendar.DAY_OF_MONTH, 34)
        if (calendar.get(Calendar.MONTH) == prevCalendar.get(Calendar.MONTH)) {
            prevCalendar.add(Calendar.DAY_OF_MONTH, 7)
        }
        val endAt = dateFormat.format(prevCalendar.time)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            calendarRepository.loadFilmInfo(userId, startAt, endAt).collect { itemList ->

                if (itemList.isEmpty()) {
                    return@collect
                }

                val dateModelList = _dateFlow.value.toMutableList()

                prevCalendar.set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))

                itemList.forEach { item ->

                    item ?: return@collect
                    val date = item.updateDate

                    val format = dateFormat.parse(date) ?: return@collect
                    val itemCalendar = Calendar.getInstance().apply {
                        time = format
                    }

                    val itemMonth = itemCalendar.get(Calendar.MONTH)
                    val itemDay = itemCalendar.get(Calendar.DAY_OF_MONTH)
                    val itemYear = itemCalendar.get(Calendar.YEAR)
                    val prevDay = prevCalendar.get(Calendar.DAY_OF_MONTH)

                    val prevCnt = prevMaxDay - prevDay

                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                    val index = if (currentMonth > itemMonth) {
                        itemDay - prevDay
                    } else if (currentMonth == itemMonth) {
                        prevCnt + itemDay
                    } else {
                        prevCnt + currentMaxDay + itemDay
                    }

                    dateModelList[index] = DateModel(
                        itemYear.toString(),
                        (itemMonth + 1).toString(),
                        itemDay.toString(),
                        item.text,
                        item.videoUrl
                    )
                }

                _dateFlow.value = dateModelList
            }
        }
    }

    private fun initialDateList(): List<DateModel> {

        val tempCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val prevCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }

        val prevMaxDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        prevCalendar.set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))

        val dateModelList = mutableListOf<DateModel>()

        for (i in 0 until 42) {
            val year = prevCalendar.get(Calendar.YEAR)
            val month = prevCalendar.get(Calendar.MONTH) + 1
            val dayOfMonth = prevCalendar.get(Calendar.DAY_OF_MONTH)

            if (i == 35 && calendar.get(Calendar.MONTH) + 1 != month) {
                break
            }

            dateModelList.add(
                DateModel(
                    year.toString(),
                    month.toString(),
                    dayOfMonth.toString()
                )
            )
            prevCalendar.add(Calendar.DAY_OF_MONTH, 1)
            test = !test
        }

        return dateModelList
    }
}
