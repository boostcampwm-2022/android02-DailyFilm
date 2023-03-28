package com.boostcamp.dailyfilm.presentation.util

import java.text.SimpleDateFormat
import java.util.*

private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

// get
fun Calendar.month() = this.get(Calendar.MONTH) + 1
fun Calendar.day() = this.get(Calendar.DAY_OF_MONTH)
fun Calendar.year() = this.get(Calendar.YEAR)
fun Calendar.maximum() = this.getActualMaximum(Calendar.DAY_OF_MONTH)
fun Calendar.dayOfWeek() = this.get(Calendar.DAY_OF_WEEK)

// add
fun Calendar.addDay(amount: Int) = this.apply { add(Calendar.DAY_OF_MONTH, amount) }
fun Calendar.addMonth(amount: Int) = this.apply { add(Calendar.MONTH, amount) }

// set
fun Calendar.setYear(year: Int) = this.apply { set(Calendar.YEAR, year) }
fun Calendar.setMonth(month: Int) = this.apply { set(Calendar.MONTH, month - 1) }
fun Calendar.setDay(day: Int) = this.apply { set(Calendar.DAY_OF_MONTH, day) }
fun Calendar.setDate(year: Int = this.year(), month: Int = this.month(), day: Int = this.day()) =
    this.apply {
        setYear(year)
        setMonth(month)
        setDay(day)
    }

// create
fun createCalendar(): Calendar = Calendar.getInstance()
fun createCalendar(locale: Locale): Calendar = Calendar.getInstance(locale)
fun createCalendar(time: Date) = createCalendar().apply { this.time = time }
fun createCalendar(year: Int, month: Int, day: Int) = createCalendar().apply {
    setDate(year, month, day)
}

fun createCalendar(
    calendar: Calendar,
    year: Int = calendar.year(),
    month: Int = calendar.month(),
    day: Int = calendar.day()
): Calendar = createCalendar().apply {
    timeInMillis = calendar.timeInMillis
    setDate(year, month, day)
}

// DateFormat
fun getStartAt(startCalendar: Calendar): String = dateFormat.format(startCalendar.time)
fun getEndAt(currentMonth: Int, startCalendar: Calendar): String = with(startCalendar) {
    addDay(34)
    if (currentMonth != month()) {
        if (currentMonth == month() && maximum() != day()) {
            addDay(7)
        }
    }
    dateFormat.format(time)
}

fun getStartCalendar(
    prevCalendar: Calendar,
    prevMaxDay: Int,
    dayOfWeek: Int
): Calendar {
    return createCalendar().apply {
        timeInMillis = prevCalendar.timeInMillis
        val day = if (dayOfWeek == 1) {
            addMonth(1)
            1
        } else {
            prevMaxDay - (dayOfWeek - 2)
        }
        setDay(day)
    }
}
