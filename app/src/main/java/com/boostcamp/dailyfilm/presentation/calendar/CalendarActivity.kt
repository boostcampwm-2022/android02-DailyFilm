package com.boostcamp.dailyfilm.presentation.calendar

import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private lateinit var calendarPagerAdapter: CalendarPagerAdapter

    override fun initView() {
        calendarPagerAdapter = CalendarPagerAdapter(this)

        binding.vpCalendar.apply {
            adapter = calendarPagerAdapter
            currentItem = CalendarPagerAdapter.START_POSITION
        }
    }
}
