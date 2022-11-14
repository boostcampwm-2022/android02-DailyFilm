package com.boostcamp.dailyfilm.presentation.calendar

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boostcamp.dailyfilm.presentation.calendar.CalendarFragment.Companion.KEY_CALENDAR
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calendar = savedStateHandle.get<Calendar>(KEY_CALENDAR)
        ?: throw IllegalStateException("CalendarViewModel - calendar is null")

    fun testCalendar() {
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

        Log.d("Calendar", "")
        Log.d("Calendar", "testCalendar: Temp - ${tempCalendar.get(Calendar.YEAR)}/${tempCalendar.get(Calendar.MONTH) + 1}/${tempCalendar.get(Calendar.DAY_OF_MONTH)}")
        Log.d("Calendar", "testCalendar: Prev - ${prevCalendar.get(Calendar.YEAR)}/${prevCalendar.get(Calendar.MONTH) + 1}/${prevCalendar.get(Calendar.DAY_OF_MONTH)}")

        Log.d("Calendar", "Start testCalendar: ${calendar.get(Calendar.MONTH) + 1}")

        for (i in 0 until 42) {
            val year = prevCalendar.get(Calendar.YEAR)
            val month = prevCalendar.get(Calendar.MONTH) + 1
            val dayOfMonth = prevCalendar.get(Calendar.DAY_OF_MONTH)
            val testDayOfWeek = prevCalendar.get(Calendar.DAY_OF_WEEK)

            Log.d("Calendar", "testCalendar: $year/$month/$dayOfMonth, $testDayOfWeek")

            prevCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}
