package com.boostcamp.dailyfilm

import com.boostcamp.dailyfilm.presentation.util.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class CalendarUtilTest {

    lateinit var calendar: Calendar

    @Before
    fun init() {
        calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 15)
        }
    }

    @Test
    fun getTest() {
        // year
        assertEquals(calendar.year(), 2023)
        // month
        assertEquals(calendar.month(), 1)
        // day
        assertEquals(calendar.day(), 15)
    }

    @Test
    fun createTest() {
        val calendar = createCalendar(calendar, day = 2)

        // month
        assertEquals(calendar.month(), 1)
        // day
        assertEquals(calendar.day(), 2)
    }

    @Test
    fun getStartCalendarTest() {
        val prevMaxDay = 31
        val dayOfWeek = 4

        val testCalendar = getStartCalendar(calendar, prevMaxDay, dayOfWeek)
        // month
        assertEquals(calendar.month(), 1)
        // month
        assertEquals(testCalendar.month(), 1)
        // day
        assertEquals(testCalendar.day(), 29)
    }

    @Test
    fun startEndAtTest() {
        val calendar1 = createCalendar(2023, 2, 26)
        // startAt
        assertEquals(getStartAt(calendar1), "20230226")
        // endAt
        assertEquals(getEndAt(3, calendar1), "20230401")
    }
}