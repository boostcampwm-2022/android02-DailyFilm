package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
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

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR) ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    private val dayOfWeek = Calendar.getInstance().apply {
        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK)

    private val prevCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
    }

    private val prevMaxDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: error("Unknown User")

    private val _dateFlow = MutableStateFlow(initialDateList())
    val dateFlow: StateFlow<List<DateModel>> = _dateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            calendarRepository.loadFilmInfo(userId, getStartAt(), getEndAt()).collect { item ->
                reloadCalendar(item)
            }
        }
    }

    private fun getStartAt(): String {
        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        return dateFormat.format(tempCalendar.time)
    }

    private fun getEndAt(): String {
        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        tempCalendar.add(Calendar.DAY_OF_MONTH, 34)
        if (calendar.get(Calendar.MONTH) == tempCalendar.get(Calendar.MONTH)) {
            tempCalendar.add(Calendar.DAY_OF_MONTH, 7)
        }
        return dateFormat.format(tempCalendar.time)
    }

    private fun reloadCalendar(item: DailyFilmItem?) {
        item ?: return

        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        val dateModelList = _dateFlow.value.toMutableList()

        tempCalendar.set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        val prevDay = tempCalendar.get(Calendar.DAY_OF_MONTH)
        val prevCnt = prevMaxDay - prevDay

        val currentMonth = calendar.get(Calendar.MONTH)
        val currentMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val itemDate = dateFormat.parse(item.updateDate) ?: return
        val itemCalendar = Calendar.getInstance().apply {
            time = itemDate
        }

        val itemMonth = itemCalendar.get(Calendar.MONTH)
        val itemDay = itemCalendar.get(Calendar.DAY_OF_MONTH)
        val itemYear = itemCalendar.get(Calendar.YEAR)

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

        _dateFlow.value = dateModelList
    }

    private fun initialDateList(): List<DateModel> {
        val tempCalendar = Calendar.getInstance().apply {
            timeInMillis = prevCalendar.timeInMillis
            set(Calendar.DAY_OF_MONTH, prevMaxDay - (dayOfWeek - 2))
        }

        val dateModelList = mutableListOf<DateModel>()

        for (i in 0 until 42) {
            val year = tempCalendar.get(Calendar.YEAR)
            val month = tempCalendar.get(Calendar.MONTH) + 1
            val dayOfMonth = tempCalendar.get(Calendar.DAY_OF_MONTH)

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
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dateModelList
    }
}
