package com.boostcamp.dailyfilm.presentation.calendar

import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.ActivityCalendarBinding
import com.boostcamp.dailyfilm.presentation.BaseActivity
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarPagerAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding>(R.layout.activity_calendar) {

    private lateinit var calendarPagerAdapter: CalendarPagerAdapter
    private val viewModel: CalendarViewModel by viewModels()

    override fun initView() {

        binding.viewModel = viewModel

        calendarPagerAdapter = CalendarPagerAdapter(this) {
            viewModel.onDateItemClicked(it)
        }

        binding.vpCalendar.apply {
            adapter = calendarPagerAdapter
            setCurrentItem(CalendarPagerAdapter.START_POSITION, false)
            offscreenPageLimit = 2

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.getViewPagerPosition(position)
                }
            })
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is Event.UploadSuccess -> {
                            uploadFilm(event.dateModel)
                        }
                        is Event.UpdateMonth -> {
                            updateMonth(event.month)
                        }
                    }
                }
            }
        }
    }

    private fun updateMonth(month: String) {
        binding.tvMon.text = month
    }

    private fun uploadFilm(item: DateModel) {
        // TODO : 업로드 화면으로 이동
        Toast.makeText(this, "uploadFilm $item", Toast.LENGTH_SHORT).show()
    }
}
