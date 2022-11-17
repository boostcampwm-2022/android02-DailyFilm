package com.boostcamp.dailyfilm.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR)
        ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    private val _dateFlow = MutableStateFlow(initialDateList())
    val dateFlow: StateFlow<List<DateModel>> = _dateFlow.asStateFlow()

    private var test: Boolean = true

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

        val tmpUrl = "https://plchldr.co/i/500x250?text="

        for (i in 0 until 42) {
            val year = prevCalendar.get(Calendar.YEAR)
            val month = prevCalendar.get(Calendar.MONTH) + 1
            val dayOfMonth = prevCalendar.get(Calendar.DAY_OF_MONTH)

            prevCalendar.add(Calendar.DAY_OF_MONTH, 1)
            dateModelList.add(
                DateModel(
                    year.toString(),
                    month.toString(),
                    dayOfMonth.toString(),
                    if (test) {
                        tmpUrl + "$year$month$dayOfMonth"
                    } else {
                        null
                    }
                )
            )
            test = !test
        }

        return dateModelList
    }
}
