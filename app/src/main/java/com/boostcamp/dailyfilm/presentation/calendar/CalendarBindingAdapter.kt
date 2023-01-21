package com.boostcamp.dailyfilm.presentation.calendar

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import java.util.*

@BindingAdapter(
    value = ["setAdapter", "setViewModel"]
)
fun ViewPager2.initViewPager(
    calendarPagerAdapter: CalendarPagerAdapter,
    viewModel: CalendarViewModel
) {

    adapter = calendarPagerAdapter
    setCurrentItem(CalendarPagerAdapter.START_POSITION, false)
    offscreenPageLimit = 2

    val todayCalendar = Calendar.getInstance(Locale.getDefault())
    val todayYear = todayCalendar.get(Calendar.YEAR)
    val todayMonth = todayCalendar.get(Calendar.MONTH)

    val datePickerDialog = DatePickerDialog(viewModel.calendar) { year, month ->
        val position = (year * 12 + month) - (todayYear * 12 + todayMonth)
        currentItem = CalendarPagerAdapter.START_POSITION + position
    }

    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.getViewPagerPosition(position)
            viewModel.changeSelectedItem(null, null)

            val calendar = Calendar.getInstance(Locale.getDefault()).apply {
                add(Calendar.MONTH, position - CalendarPagerAdapter.START_POSITION)
            }
            datePickerDialog.setCalendar(calendar)
        }
    })
}