package com.boostcamp.dailyfilm

import androidx.lifecycle.SavedStateHandle
import com.boostcamp.dailyfilm.data.calendar.CalendarRepository
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR
import com.boostcamp.dailyfilm.presentation.calendar.DateViewModel
import com.boostcamp.dailyfilm.presentation.util.*
import org.junit.Before
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
}