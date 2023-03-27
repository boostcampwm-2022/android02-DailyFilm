package com.boostcamp.dailyfilm

import androidx.lifecycle.SavedStateHandle
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.DateViewModel
import com.boostcamp.dailyfilm.presentation.util.getStartCalendar
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DateViewModelUnitTest {

    private lateinit var dateViewModel: DateViewModel
    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    @Before
    fun init() {
        val calendarRepository = Mockito.mock(CalendarRepository::class.java)
        val syncRepository = Mockito.mock(SyncRepository::class.java)
        val savedStateHandle =
            SavedStateHandle().apply {
                set(
                    KEY_CALENDAR,
                    Calendar.getInstance(Locale.getDefault())
                )
            }
        dateViewModel = DateViewModel(calendarRepository, syncRepository, "", savedStateHandle)
    }

    @Test
    fun example1() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_WEEK, 15)
        }
        val maxDay = 28
        val dayOfWeek = 4

        val startCalendar = getStartCalendar(calendar, maxDay, dayOfWeek)
        val formatCalendar = dateFormat.format(startCalendar.time)
        assertEquals(formatCalendar, "20230226")
    }

    @Test
    fun example2() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, 10)
            set(Calendar.DAY_OF_WEEK, 15)
        }
        val maxDay = 30
        val dayOfWeek = 6

        val startCalendar = getStartCalendar(calendar, maxDay, dayOfWeek)
        val formatCalendar = dateFormat.format(startCalendar.time)
        assertEquals(formatCalendar, "20231126")
    }

    @Test
    fun month() {
        val calendar = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.MONTH, 3)
        }
        val formatCalendar = dateFormat.format(calendar.time)
        assertEquals(formatCalendar, "20230322")
    }
}